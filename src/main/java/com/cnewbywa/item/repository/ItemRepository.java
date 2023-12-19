package com.cnewbywa.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.cnewbywa.item.model.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

	Mono<Item> findByItemId(String itemId);
	
	Mono<Void> deleteByItemId(String itemId);
	
	@Query(fields = "{ 'itemId' : 1, 'name' : 1, 'createTime' : 1, 'version' : 1 }")
	Flux<Item> findBy(Pageable pageable);
}
