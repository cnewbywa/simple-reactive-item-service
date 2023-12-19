package com.cnewbywa.item.service;

import org.springframework.stereotype.Service;

import com.cnewbywa.item.error.ItemNotFoundException;
import com.cnewbywa.item.model.Item;
import com.cnewbywa.item.model.ItemDto;
import com.cnewbywa.item.model.ItemResponseDto;
import com.cnewbywa.item.repository.ItemRepository;

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
}
