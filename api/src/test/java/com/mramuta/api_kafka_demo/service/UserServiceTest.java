package com.mramuta.api_kafka_demo.service;

import com.mramuta.api_kafka_demo.messaging.PublishingService;
import com.mramuta.api_kafka_demo.model.User;
import com.mramuta.api_kafka_demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceTest {

    private UserService subject;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private PublishingService publishingService;
    @Mock
    private SecurityService securityService;
    private User userRequest;
    private String hashedPass;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new UserService(
                userRepository,
                addressService,
                publishingService,
                securityService
        );
        userRequest = new User(
                "someFirstName",
                "someLastName",
                "someEmail",
                "somePassword"
        );
        hashedPass = "someHashedPass";
        when(securityService.hashPassword(userRequest.getPassword()))
                .thenReturn(hashedPass);
    }

    @Test
    void shouldSaveUserToRepository() {
        User userToSave = new User(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getEmail(),
                hashedPass
        );

        subject.createUser(userRequest);

        verify(userRepository).save(userToSave);
    }

    @Test
    void shouldReturnUserFromRepository() {
        User userToSave = new User(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getEmail(),
                hashedPass
        );
        User expectedUser = new User(
                99L,
                "someFirst",
                "someLast",
                "someEmail",
                "someHash"
        );

        when(userRepository.save(userToSave)).thenReturn(expectedUser);
        User actualUser = subject.createUser(userRequest);

        assertThat(actualUser).isEqualTo(expectedUser);
    }
}