package com.cnewbywa.item.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.cnewbywa.item.model.ItemDto;
import com.cnewbywa.item.model.ItemResponseDto;
import com.cnewbywa.item.service.ItemService;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

	@Mock
	private ItemService itemService;
	
	@InjectMocks
	private ItemController itemController;
	
	private String item1Id = UUID.randomUUID().toString();
	private String item2Id = UUID.randomUUID().toString();
	
	@Test
	void testGetItem_Success() {
		ItemResponseDto response = ItemResponseDto.builder().id(item1Id).name("Item 1").description("Description for item 1").build();
		
		when(itemService.getItem(item1Id)).thenReturn(Mono.just(response));
		
		Mono<ItemResponseDto> responseMono = itemController.getItem(item1Id);
		
		assertNotNull(responseMono);
		
		ItemResponseDto responseDto = responseMono.block();
		
		assertNotNull(responseDto);
		assertResponseDto(response, responseDto);
		
		verify(itemService).getItem(item1Id);
	}
	
	@Test
	void testAddItem() {
		ItemResponseDto response = ItemResponseDto.builder().id(item1Id).name("Item 2").description("Description for item 2").build();
		
		ItemDto input = new ItemDto("Item 2", "Description for item 2");
		
		when(itemService.addItem(input, "test-user-id")).thenReturn(Mono.just(response));
		
		Mono<ItemResponseDto> responseMono = itemController.addItem(createAuthentication("test-user-id"), input);
		
		assertNotNull(responseMono);
		
		ItemResponseDto responseDto = responseMono.block();
		
		assertNotNull(responseDto);
		assertResponseDto(response, responseDto);
		
		verify(itemService).addItem(input, "test-user-id");
	}
	
	@Test
	void testDeleteItem() {
		when(itemService.deleteItem(item2Id)).thenReturn(Mono.empty().then());
		
		itemController.deleteItem(item2Id);
		
		verify(itemService).deleteItem(item2Id);
	}
	
	private Authentication createAuthentication(String user) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("alg", "HS256");
		headers.put("typ", "JWT");
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("sub", user);
		
		Jwt jwt = new Jwt("not empty", Instant.now(), Instant.now().plusSeconds(60), headers, claims);
		
		return new JwtAuthenticationToken(jwt, null, user);
	}
	
	private void assertResponseDto(ItemResponseDto expectedResponse, ItemResponseDto actualResponse) {
		assertEquals(expectedResponse.getId(), actualResponse.getId());
		assertEquals(expectedResponse.getName(), actualResponse.getName());
		assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
	}
}
