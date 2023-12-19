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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.cnewbywa.item.error.ItemNotFoundException;
import com.cnewbywa.item.model.Item;
import com.cnewbywa.item.model.ItemDto;
import com.cnewbywa.item.model.ItemListResponseDto;
import com.cnewbywa.item.model.ItemResponseDto;
import com.cnewbywa.item.model.ItemsResponseDto;
import com.cnewbywa.item.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Captor
	ArgumentCaptor<Item> itemCaptor;
	
	@InjectMocks
	private ItemService itemService;
	
	@Mock
	private ItemRepository itemRepository;
	
	private String item1Id = UUID.randomUUID().toString();
	private String item2Id = UUID.randomUUID().toString();
	
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
	void testGetItems() {
		Item dbItem1 = Item.builder().itemId(item1Id).name("Item 1").createTime(Instant.now()).build();
		Item dbItem2 = Item.builder().itemId(item2Id).name("Item 2").createTime(Instant.now()).build();
		
		Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "name"));
		
		when(itemRepository.findAll(sort)).thenReturn(Flux.just(dbItem1, dbItem2));
		
		Flux<ItemListResponseDto> items = itemService.getItems(sort);
		
		StepVerifier.create(items)
			.consumeNextWith(item -> {
				assertEquals(dbItem1.getItemId(), item.getId());
				assertEquals(dbItem1.getName(), item.getName());
				assertNotNull(item.getCreateTime());
			})
			.consumeNextWith(item -> {
				assertEquals(dbItem2.getItemId(), item.getId());
				assertEquals(dbItem2.getName(), item.getName());
				assertNotNull(item.getCreateTime());
			})
			.verifyComplete();
		
		verify(itemRepository).findAll(sort);
	}
	
	@Test
	void testGetItemsWithPaging() {
		Item dbItem1 = Item.builder().itemId(item1Id).name("Item 1").createTime(Instant.now()).build();
		Item dbItem2 = Item.builder().itemId(item2Id).name("Item 2").createTime(Instant.now()).build();
		
		Pageable pageable = PageRequest.of(0, 10, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));
		
		when(itemRepository.findBy(pageable)).thenReturn(Flux.just(dbItem1, dbItem2));
		when(itemRepository.count()).thenReturn(Mono.just(3L));
		
		Mono<ItemsResponseDto> responseMono = itemService.getItemsWithPaging(pageable);
		
		assertNotNull(responseMono);
		
		ItemsResponseDto response = responseMono.block();
		
		assertNotNull(response);
		assertEquals(2, response.getAmount());
		assertEquals(3, response.getTotalAmount());
		assertNotNull(response.getItems());
		assertEquals(2, response.getItems().size());
		assertListResponseDto(response.getItems().get(0), dbItem1);
		assertListResponseDto(response.getItems().get(1), dbItem2);
		
		verify(itemRepository).findBy(pageable);
		verify(itemRepository).count();
	}
	
	@Test
	void testGetItemsWithSkipAndTake() {
		Item dbItem1 = Item.builder().itemId(item1Id).name("Item 1").createTime(Instant.now()).build();
		Item dbItem2 = Item.builder().itemId(item2Id).name("Item 2").createTime(Instant.now()).build();
		
		Pageable pageable = PageRequest.of(0, 10, Sort.by(new Sort.Order(Sort.Direction.ASC, "name")));
		
		when(itemRepository.findAll(pageable.getSort())).thenReturn(Flux.just(dbItem1, dbItem2));
		when(itemRepository.count()).thenReturn(Mono.just(3L));
		
		Mono<ItemsResponseDto> responseMono = itemService.getItemsWithSkipAndTake(pageable);
		
		assertNotNull(responseMono);
		
		ItemsResponseDto response = responseMono.block();
		
		assertNotNull(response);
		assertEquals(2, response.getAmount());
		assertEquals(3, response.getTotalAmount());
		assertNotNull(response.getItems());
		assertEquals(2, response.getItems().size());
		assertListResponseDto(response.getItems().get(0), dbItem1);
		assertListResponseDto(response.getItems().get(1), dbItem2);
		
		verify(itemRepository).findAll(pageable.getSort());
		verify(itemRepository).count();
	}
	
	@Test
	void testAddItem_Success() {
		String name = "Item 1";
		String description = "Description of item 1";
		
		Item item = Item.builder().itemId(item1Id).name(name).description(description).createTime(Instant.now()).createdBy("user1").build();
		
		when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
		
		ItemDto itemDto = new ItemDto("Item 1", "Description of item 1");
		
		Mono<ItemResponseDto> responseMono = itemService.addItem(itemDto, "user1");
		
		// assert response
		
		assertNotNull(responseMono);
		
		ItemResponseDto response = responseMono.block();
		
		assertEquals(item1Id, response.getId());
		assertEquals(itemDto.getName(), response.getName());
		assertEquals(itemDto.getDescription(), response.getDescription());
		assertNotNull(response.getCreateTime());
		assertEquals("user1", response.getCreatedBy());
		
		verify(itemRepository).save(itemCaptor.capture());
		
		// assert values in mocked save
		
		Item capturedItem = itemCaptor.getValue();
		
		assertNotNull(capturedItem);
		assertNotNull(capturedItem.getItemId());
		assertEquals(itemDto.getName(), capturedItem.getName());
		assertEquals(itemDto.getDescription(), capturedItem.getDescription());
		assertEquals("user1", capturedItem.getCreatedBy());
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
		
		// assert response
		
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
		verify(itemRepository).save(itemCaptor.capture());
		
		// assert values in mocked save
		
		Item capturedItem = itemCaptor.getValue();
		
		assertNotNull(capturedItem);
		assertEquals(item1Id, capturedItem.getItemId());
		assertEquals(itemDto.getName(), capturedItem.getName());
		assertEquals(itemDto.getDescription(), capturedItem.getDescription());
		assertEquals("user1", capturedItem.getUpdatedBy());
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
	
	private void assertListResponseDto(ItemListResponseDto receivedItemListResponseDto, Item dbItem) {
		assertEquals(dbItem.getItemId(), receivedItemListResponseDto.getId());
		assertEquals(dbItem.getName(), receivedItemListResponseDto.getName());
		assertNotNull(receivedItemListResponseDto.getCreateTime());
	}
}
