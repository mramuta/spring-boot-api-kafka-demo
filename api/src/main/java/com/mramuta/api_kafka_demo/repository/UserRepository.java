package com.mramuta.api_kafka_demo.repository;

import com.mramuta.api_kafka_demo.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
