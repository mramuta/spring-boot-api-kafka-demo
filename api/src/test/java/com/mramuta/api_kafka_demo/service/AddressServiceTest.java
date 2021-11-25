package com.mramuta.api_kafka_demo.service;

import com.mramuta.api_kafka_demo.model.Address;
import com.mramuta.api_kafka_demo.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    private AddressService subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new AddressService(
                addressRepository
        );
    }

    @Test
    void shouldReturnAddressesByCountry() {
        String country = "someCountry";
        List<Address> expectedAddresses = asList(
                new Address(
                        1L,
                        "someAddress1",
                        "someAddress2",
                        "someCity",
                        "someState",
                        "someZip",
                        "someCountry"
                ),
                new Address(
                        2L,
                        "someAddress1",
                        "someAddress2",
                        "someCity",
                        "someState",
                        "someZip",
                        "someCountry"
                )
        );
        when(addressRepository.findByCountry(country)).thenReturn(expectedAddresses);
        List<Address> actualAddresses = subject.getAddressesByCountry(country);
        assertThat(actualAddresses).containsExactlyElementsOf(expectedAddresses);
    }
}