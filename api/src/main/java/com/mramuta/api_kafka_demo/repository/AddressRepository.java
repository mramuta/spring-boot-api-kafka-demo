package com.mramuta.api_kafka_demo.repository;

import com.mramuta.api_kafka_demo.model.Address;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Long> {

    List<Address> findByCountry(String country);

}