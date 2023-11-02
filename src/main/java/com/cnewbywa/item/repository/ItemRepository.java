package com.cnewbywa.item.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.cnewbywa.item.model.Item;

import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

	Mono<Item> findByItemId(String itemId);
	
	Mono<Void> deleteByItemId(String itemId);
}
