package com.cnewbywa.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.cnewbywa.item.configuration.NativeRuntimeHints;

@SpringBootApplication
@EnableReactiveMongoRepositories
@ImportRuntimeHints(value = { NativeRuntimeHints.class })
public class ItemReactiveApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ItemReactiveApplication.class, args);
	}
}
