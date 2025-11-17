package com.kata.app.car.infrastructure.config;

import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.infrastructure.jpa.entity.CarEntity;
import com.kata.app.car.infrastructure.jpa.entity.CustomerEntity;
import com.kata.app.car.infrastructure.jpa.repository.SpringDataCarRepository;
import com.kata.app.car.infrastructure.jpa.repository.SpringDataCustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner seedData(SpringDataCarRepository carRepository,
							   SpringDataCustomerRepository customerRepository) {
		return args -> {
			if (carRepository.count() == 0) {
				CarEntity c1 = new CarEntity();
				c1.setPlateNumber("AA-123-AA");
				c1.setStatus(CarStatus.AVAILABLE);

				CarEntity c2 = new CarEntity();
				c2.setPlateNumber("BB-456-BB");
				c2.setStatus(CarStatus.AVAILABLE);

				CarEntity c3 = new CarEntity();
				c3.setPlateNumber("CC-789-CC");
				c3.setStatus(CarStatus.AVAILABLE);

				carRepository.saveAll(List.of(c1, c2, c3));
			}

			if (customerRepository.count() == 0) {
				CustomerEntity u1 = new CustomerEntity();
				u1.setName("Alice");

				CustomerEntity u2 = new CustomerEntity();
				u2.setName("Bob");

				customerRepository.saveAll(List.of(u1, u2));
			}
		};
	}
}


