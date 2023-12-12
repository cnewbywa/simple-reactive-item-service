package com.cnewbywa.item.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cnewbywa.item.error.ItemNotFoundException;
import com.cnewbywa.item.model.Item;
import com.cnewbywa.item.model.ItemDto;
import com.cnewbywa.item.model.ItemResponseDto;
import com.cnewbywa.item.repository.ItemRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@InjectMocks
	private ItemService itemService;
	
	@Mock
	private ItemRepository itemRepository;
	
	private String item1Id = UUID.randomUUID().toString();
	
	@Test
	void testGetItem_Success() {
		Item dbItem = Item.builder().itemId(item1Id).build();
		
		when(itemRepository.findByItemId(item1Id)).thenReturn(Mono.just(dbItem));
		
		Mono<ItemResponseDto> responseMono = itemService.getItem(item1Id);
		
		assertNotNull(responseMono);
		
		ItemResponseDto response = responseMono.block();
		
		assertNotNull(response);
		assertEquals(item1Id, response.getId());
			
		verify(itemRepository).findByItemId(item1Id);
	}
	
	@Test
	void testGetItem_Failute() {
		when(itemRepository.findByItemId(item1Id)).thenReturn(Mono.empty());
		
		Mono<ItemResponseDto> responseMono = itemService.getItem(item1Id);
		
		StepVerifier.create(responseMono).expectError(ItemNotFoundException.class).verify();
		
		verify(itemRepository).findByItemId(item1Id);
	}
	
	@Test
	void testAddItem_Success() {
		String name = "Item 1";
		String description = "Description of item 1";
		
		Item item = Item.builder().itemId(item1Id).name(name).description(description).createTime(Instant.now()).createdBy("user1").build();
		
		when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
		
		ItemDto itemDto = new ItemDto("Item 1", "Description of item 1");
		
		Mono<ItemResponseDto> responseMono = itemService.addItem(itemDto, "user1");
		
		assertNotNull(responseMono);
		
		ItemResponseDto response = responseMono.block();
		
		assertEquals(item1Id, response.getId());
		assertEquals(itemDto.getName(), response.getName());
		assertEquals(itemDto.getDescription(), response.getDescription());
		assertNotNull(response.getCreateTime());
		assertEquals("user1", response.getCreatedBy());
		
		verify(itemRepository).save(any(Item.class));
	}
	
	@Test
	void testUpdateItem_Success() {
		String name = "Item 1";
		String description = "Description of item 1";
		
		Item item = Item.builder().itemId(item1Id).name(name).description(description).createTime(Instant.now()).createdBy("user1").build();
		
		Item modifiedItem = Item.builder().itemId(item1Id).name(name).description("New description of item 1").createTime(item.getCreateTime()).createdBy("user1").updateTime(Instant.now()).updatedBy("user1").build();
		
		when(itemRepository.findByItemId(item1Id)).thenReturn(Mono.just(item));
		
		when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(modifiedItem));
		
		ItemDto itemDto = new ItemDto("Item 1", "New description of item 1");
		
		Mono<ItemResponseDto> responseMono = itemService.updateItem(item1Id, itemDto, "user1");
		
		assertNotNull(responseMono);
		
		ItemResponseDto response = responseMono.block();
		
		assertEquals(item1Id, response.getId());
		assertEquals(modifiedItem.getName(), response.getName());
		assertEquals(modifiedItem.getDescription(), response.getDescription());
		assertNotNull(response.getCreateTime());
		assertEquals("user1", response.getCreatedBy());
		assertNotNull(response.getUpdateTime());
		assertEquals("user1", response.getUpdatedBy());
		
		verify(itemRepository).findByItemId(item1Id);
		verify(itemRepository).save(any(Item.class));
	}
	
	@Test
	void testUpdateItem_Failure() {
		ItemDto itemDto = new ItemDto("Item 9", "Description of item 9");
		
		when(itemRepository.findByItemId(item1Id)).thenReturn(Mono.empty());
		
		Mono<ItemResponseDto> responseMono = itemService.updateItem(item1Id, itemDto, "user1");
		
		StepVerifier.create(responseMono).expectError(ItemNotFoundException.class).verify();
		
		verify(itemRepository).findByItemId(item1Id);
	}
	
	@Test
	void testDeleteItem_Success() {
		itemService.deleteItem(item1Id);
		
		verify(itemRepository).deleteByItemId(item1Id);
	}
}
