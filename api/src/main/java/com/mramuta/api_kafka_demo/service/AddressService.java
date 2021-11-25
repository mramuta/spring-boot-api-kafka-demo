package com.mramuta.api_kafka_demo.service;

import com.mramuta.api_kafka_demo.model.Address;
import com.mramuta.api_kafka_demo.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getAddressesByCountry(String country){
        return addressRepository.findByCountry(country);
    }
}
