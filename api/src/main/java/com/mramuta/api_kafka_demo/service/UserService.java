package com.mramuta.api_kafka_demo.service;

import com.mramuta.api_kafka_demo.messaging.PublishingService;
import com.mramuta.api_kafka_demo.model.Address;
import com.mramuta.api_kafka_demo.model.User;
import com.mramuta.api_kafka_demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final PublishingService publishingService;
    private final SecurityService securityService;

    public UserService(
            UserRepository userRepository,
            AddressService addressService,
            PublishingService publishingService,
            SecurityService securityService
    ) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.publishingService = publishingService;
        this.securityService = securityService;
    }

    public User createUser(User user) {
        User newUser = new User(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                securityService.hashPassword(user.getPassword())
        );
        User createdUser = userRepository.save(newUser);
        publishingService.publishNewUserEvent(createdUser);
        return createdUser;
    }

    public Set<User> getUsersByCountry(String country) {
        List<Address> addresses = addressService.getAddressesByCountry(country);
        Set<User> users = new HashSet<>();
        for (Address address : addresses) {
            users.addAll(address.getUsers());
        }
        return users;
    }
}
