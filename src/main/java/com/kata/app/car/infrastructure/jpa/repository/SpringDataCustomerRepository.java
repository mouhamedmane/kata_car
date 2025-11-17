package com.kata.app.car.infrastructure.jpa.repository;

import com.kata.app.car.infrastructure.jpa.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, UUID> {
}


