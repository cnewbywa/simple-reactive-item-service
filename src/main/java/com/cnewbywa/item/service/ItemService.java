package com.cnewbywa.item.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cnewbywa.item.error.ItemNotFoundException;
import com.cnewbywa.item.model.Item;
import com.cnewbywa.item.model.ItemDto;
import com.cnewbywa.item.model.ItemListResponseDto;
import com.cnewbywa.item.model.ItemResponseDto;
import com.cnewbywa.item.model.ItemsResponseDto;
import com.cnewbywa.item.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemService {

	private ItemRepository itemRepository;

	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}
	
	public Mono<ItemResponseDto> getItem(String id) {
		return itemRepository.findByItemId(id).switchIfEmpty(Mono.error(new ItemNotFoundException("Item not found"))).map(this::createResponseDto);
	}
	
	public Flux<ItemListResponseDto> getItems(Sort sort) {
		return itemRepository.findAll(sort).map(this::createListResponseDto);
	}
	
	public Mono<ItemsResponseDto> getItemsWithPaging(Pageable pageable) {
		return itemRepository.findBy(pageable).collectList()
				.zipWith(itemRepository.count())
				.map(results -> new ItemsResponseDto(results.getT1().stream().map(this::createListResponseDto).toList(), results.getT1().size(), results.getT2()));
	}
	
	public Mono<ItemsResponseDto> getItemsWithSkipAndTake(Pageable pageable) {
		return itemRepository.findAll(pageable.getSort()).skip(pageable.getPageNumber() * Long.valueOf(pageable.getPageSize())).take(pageable.getPageSize()).collectList()
				.zipWith(itemRepository.count())
				.map(results -> new ItemsResponseDto(results.getT1().stream().map(this::createListResponseDto).toList(), results.getT1().size(), results.getT2()));
	}
	
	public Mono<ItemResponseDto> addItem(ItemDto itemDto, String user) {
		return itemRepository.save(createItem(itemDto, user)).map(this::createResponseDto);
	}
	
	public Mono<ItemResponseDto> updateItem(String id, ItemDto itemDto, String user) {
		return itemRepository.findByItemId(id).switchIfEmpty(Mono.error(new ItemNotFoundException("Item not found"))).map(dbItem -> {
			if (!itemDto.getName().equals(dbItem.getName())) {
				dbItem.setName(itemDto.getName());
			}
			
			if (!itemDto.getDescription().equals(dbItem.getDescription())) {
				dbItem.setDescription(itemDto.getDescription());
			}
			
			dbItem.setUpdatedBy(user);
			
			return dbItem;
		}).flatMap(modifiedItem -> itemRepository.save(modifiedItem).map(this::createResponseDto));
	}
	
	public Mono<Void> deleteItem(String id) {
		return itemRepository.deleteByItemId(id);
	}
	
	private Item createItem(ItemDto itemDto, String user) {
		return Item.builder()
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.createdBy(user)
				.build();
	}
	
	private ItemResponseDto createResponseDto(Item item) {
		return ItemResponseDto.builder()
				.id(item.getItemId())
				.name(item.getName())
				.description(item.getDescription())
				.createTime(item.getCreateTime())
				.createdBy(item.getCreatedBy())
				.updateTime(item.getUpdateTime())
				.updatedBy(item.getUpdatedBy())
				.build();
	}
	
	private ItemListResponseDto createListResponseDto(Item item) {
		return ItemListResponseDto.builder()
				.id(item.getItemId())
				.name(item.getName())
				.createTime(item.getCreateTime())
				.build();
	}
}
