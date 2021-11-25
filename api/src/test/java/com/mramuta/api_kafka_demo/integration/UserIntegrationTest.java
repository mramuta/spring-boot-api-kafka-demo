package com.mramuta.api_kafka_demo.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mramuta.api_kafka_demo.model.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@SpringJUnitConfig
@AutoConfigureMockMvc
@AutoConfigureDataJdbc
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:29092",
                "port=29092"
        })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private BlockingQueue<ConsumerRecord<Long, User>> records;
    private KafkaMessageListenerContainer<Long, User> container;


    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM USER_ADDRESS");
        jdbcTemplate.execute("DELETE FROM ADDRESS");
        jdbcTemplate.execute("DELETE FROM USER");

        Map<String, Object> configs = new HashMap<>(
                KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        JsonDeserializer<User> stringJsonDeserializer = new JsonDeserializer<>();
        stringJsonDeserializer.addTrustedPackages(User.class.getPackage().getName());
        DefaultKafkaConsumerFactory<Long, User> consumerFactory = new DefaultKafkaConsumerFactory<>(
                configs, new LongDeserializer(), stringJsonDeserializer);
        ContainerProperties containerProperties = new ContainerProperties("user");
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<Long, User>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    void tearDown() {
        container.stop();
        embeddedKafkaBroker.destroy();
    }

    @Test
    void shouldCreateUser() throws Exception {
        User newUser = new User(
                "someFirstName",
                "someLastName",
                "someEmail",
                "somePassword"
        );

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(newUser))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());

        Map<String, Object> userMap = jdbcTemplate.queryForMap("SELECT * FROM USER WHERE ID = 1");


        assertThat(userMap.get("id")).isEqualTo(1L);
        assertThat(userMap.get("first_name")).isEqualTo(newUser.getFirstName());
        assertThat(userMap.get("last_name")).isEqualTo(newUser.getLastName());
        assertThat(userMap.get("email")).isEqualTo(newUser.getEmail());
        assertThat(userMap.get("password")).isNotNull();
    }

    @Test
    void shouldPublishUserEvent() throws Exception {
        assertThat(records).isEmpty();
        User newUser = new User(
                "someFirstName",
                "someLastName",
                "someEmail",
                "somePassword"
        );
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(newUser))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());


        Awaitility.await().pollDelay(100, TimeUnit.MILLISECONDS).until(() -> !records.isEmpty());
        ArrayList<ConsumerRecord<Long, User>> publishedRecords = new ArrayList<>();
        records.drainTo(publishedRecords);


        ConsumerRecord<Long, User> publishedUserEvent = publishedRecords.get(0);
        assertThat(publishedUserEvent.key()).isEqualTo(1);

        User publishedUser = publishedUserEvent.value();

        assertThat(publishedUser.getId()).isEqualTo(1);
        assertThat(publishedUser.getFirstName()).isEqualTo(newUser.getFirstName());
        assertThat(publishedUser.getLastName()).isEqualTo(newUser.getLastName());
        assertThat(publishedUser.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(publishedUser.getPassword()).isNotNull();
    }

    @Test
    void shouldRetrieveUsersByCountry() throws Exception {
        User user1 = addUser(1);
        User user2 = addUser(2);
        User user3 = addUser(3);
        User user4 = addUser(4);
        User user5 = addUser(5);

        addAddress(1);
        addAddress(2);
        addAddress(3);
        addAddress(4);

        addUserAddress(1, 1);
        addUserAddress(2, 1);
        addUserAddress(3, 1);
        addUserAddress(5, 1);

        addUserAddress(4, 2);
        addUserAddress(5, 2);

        List<User> country1Users = objectMapper.readValue(mockMvc.perform(get("/users")
                .param("country", "country1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });

        List<User> country2Users = objectMapper.readValue(mockMvc.perform(get("/users")
                .param("country", "country2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });

        assertThat(country1Users).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(user1, user2, user3, user5);

        assertThat(country2Users).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(user4, user5);

    }

    private User addUser(long id) {
        User user = new User(
                id,
                "first" + id,
                "last" + id,
                "email" + id,
                "password" + id
        );

        jdbcTemplate.update("INSERT INTO USER (id,first_name,last_name,email,password) VALUES (?,?,?,?,?)",
                id, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return user;
    }

    private void addAddress(int id) {
        jdbcTemplate.update(
                "INSERT INTO ADDRESS (id,address_1,address_2,city,state,zip,country) VALUES (?,?,?,?,?,?,?)",
                id, "address1" + id, "address2" + id, "city" + id, "state" + id, "zip" + id, "country" + id
        );
    }

    private void addUserAddress(int userId, int addressId) {
        jdbcTemplate.update("INSERT INTO USER_ADDRESS (user_id,address_id) VALUES (?,?)",
                userId, addressId
        );
    }

}
