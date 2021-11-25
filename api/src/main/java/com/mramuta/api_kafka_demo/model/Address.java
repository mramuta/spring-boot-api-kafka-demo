package com.mramuta.api_kafka_demo.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "address_1")
    String address1;
    @Column(name = "address_2")
    String address2;
    String city;
    String state;
    String zip;
    String country;


    @ManyToMany
    @JoinTable(
            name = "user_address",
            joinColumns = @JoinColumn(name = "address_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> users;

    public Set<User> getUsers() {
        return users;
    }

    public Address() {
    }

    public Address(Long id, String address1, String address2, String city, String state, String zip, String country) {
        this.id = id;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }
}
