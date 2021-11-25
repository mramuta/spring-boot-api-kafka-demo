package com.mramuta.api_kafka_demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mramuta.api_kafka_demo.model.User;
import com.mramuta.api_kafka_demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private UserController subject;
    @Mock
    private UserService userService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(subject).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnUsersByCountry() throws Exception {
        String country = "US";
        Set<User> expectedUsers = new HashSet<>();
        expectedUsers.add(new User(
                "someFirstName",
                "someLastName",
                "someEmail",
                "somePassword"
        ));

        when(userService.getUsersByCountry(country)).thenReturn(expectedUsers);

        mockMvc.perform(get("/users").param("country", country))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedUsers)));

        verify(userService).getUsersByCountry(country);
    }

    @Test
    void shouldCreateUser() throws Exception {
        User expectedUser = new User(
                "someFirstName",
                "someLastName",
                "someEmail",
                "somePassword"
        );


        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(expectedUser))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).createUser(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedUser);
    }
}