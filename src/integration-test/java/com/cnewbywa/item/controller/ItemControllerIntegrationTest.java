package com.cnewbywa.item.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.cnewbywa.item.model.Item;
import com.cnewbywa.item.model.ItemDto;
import com.cnewbywa.item.model.ItemListResponseDto;
import com.cnewbywa.item.model.ItemResponseDto;
import com.cnewbywa.item.model.ItemsResponseDto;
import com.cnewbywa.item.repository.ItemRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = { "classpath:application-it-test.yml" })
@ActiveProfiles("it-test")
@AutoConfigureWebTestClient
@Testcontainers
class ItemControllerIntegrationTest {
	
	@Autowired 
	private WebTestClient webClient;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@LocalServerPort
	private int port;
	
	@Container
	@ServiceConnection
	final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));
	
	String item1Id = null;
	String item2Id = null;
	String item3Id = null;
	
	@BeforeEach
	void setupEach() {
		//webClient = webClient.mutateWith(SecurityMockServerConfigurers.mockJwt());
		
		item1Id = itemRepository.save(Item.builder().name("Item 1").description("Description for Item 1").createdBy("user").build()).block().getItemId();
		item2Id = itemRepository.save(Item.builder().name("Item 2").description("Description for Item 2").createdBy("user").build()).block().getItemId();
		item3Id = itemRepository.save(Item.builder().name("Item 3").description("Description for Item 3").createdBy("user-test").build()).block().getItemId();
	}
	
	@AfterEach
	void destroy() {
		itemRepository.deleteAll().block();
	}
	
	@Test
	void testGetItem() {
		webClient
			.mutateWith(SecurityMockServerConfigurers.mockJwt())
        	.get().uri("/items/" + item1Id)
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBody(ItemResponseDto.class)
        	.consumeWith(result -> {
    			ItemResponseDto dto = result.getResponseBody();
    			
    			assertNotNull(dto);
    			assertEquals(item1Id, dto.getId());
    			assertEquals("Item 1", dto.getName());
    			assertEquals("Description for Item 1", dto.getDescription());
    			assertNotNull(dto.getCreateTime());
    			assertEquals("user", dto.getCreatedBy());
    		});
	}
	
	@Test
	void testGetItemWithoutAuthenticatedUser() {
		webClient
        	.get().uri("/items/" + item1Id)
        	.exchange()
        	.expectStatus().isUnauthorized();
	}
	
	@Test
	void testGetItems() {
		webClient
        	.get().uri("/items")
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBodyList(ItemListResponseDto.class)
        	.consumeWith(result -> {
        		List<ItemListResponseDto> dtos = result.getResponseBody();
    			
    			assertNotNull(dtos);
    			assertEquals(3, dtos.size());
    			
    			assertEquals(item3Id, dtos.get(0).getId());
    			assertEquals("Item 3", dtos.get(0).getName());
    			assertNotNull(dtos.get(0).getCreateTime());
    			
    			assertEquals(item2Id, dtos.get(1).getId());
    			assertEquals("Item 2", dtos.get(1).getName());
    			assertNotNull(dtos.get(1).getCreateTime());
    			
    			assertEquals(item1Id, dtos.get(2).getId());
    			assertEquals("Item 1", dtos.get(2).getName());
    			assertNotNull(dtos.get(1).getCreateTime());
    		});
	}
	
	@Test
	void testGetItemsWithSortParameter() {
		webClient
        	.get().uri("/items?sort=name,asc")
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBodyList(ItemListResponseDto.class)
        	.consumeWith(result -> {
        		List<ItemListResponseDto> dtos = result.getResponseBody();
    			
    			assertNotNull(dtos);
    			assertEquals(3, dtos.size());
    			
    			assertEquals(item1Id, dtos.get(0).getId());
    			assertEquals("Item 1", dtos.get(0).getName());
    			assertNotNull(dtos.get(0).getCreateTime());
    			
    			assertEquals(item2Id, dtos.get(1).getId());
    			assertEquals("Item 2", dtos.get(1).getName());
    			assertNotNull(dtos.get(1).getCreateTime());
    			
    			assertEquals(item3Id, dtos.get(2).getId());
    			assertEquals("Item 3", dtos.get(2).getName());
    			assertNotNull(dtos.get(2).getCreateTime());
    		});
	}
	
	@Test
	void testGetItemsWithMultipleSortParameters() {
		webClient
        	.get().uri("/items?sort=name,asc&sort=createTime,asc")
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBodyList(ItemListResponseDto.class)
        	.consumeWith(result -> {
        		List<ItemListResponseDto> dtos = result.getResponseBody();
    			
    			assertNotNull(dtos);
    			assertEquals(3, dtos.size());
    			
    			assertEquals(item1Id, dtos.get(0).getId());
    			assertEquals("Item 1", dtos.get(0).getName());
    			assertNotNull(dtos.get(0).getCreateTime());
    			
    			assertEquals(item2Id, dtos.get(1).getId());
    			assertEquals("Item 2", dtos.get(1).getName());
    			assertNotNull(dtos.get(1).getCreateTime());
    			
    			assertEquals(item3Id, dtos.get(2).getId());
    			assertEquals("Item 3", dtos.get(2).getName());
    			assertNotNull(dtos.get(2).getCreateTime());
    		});
	}
	
	@Test
	void testGetItemsWithPagingPage0() {
		webClient
        	.get().uri("/items/paging?page=0&size=2&sort=name,asc")
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBody(ItemsResponseDto.class)
        	.consumeWith(result -> {
        		ItemsResponseDto dto = result.getResponseBody();
    			
    			assertNotNull(dto);
    			assertEquals(2, dto.getAmount());
    			assertEquals(3, dto.getTotalAmount());
    			
    			assertNotNull(dto.getItems());
    			assertEquals(2, dto.getItems().size());
    			
    			assertEquals(item1Id, dto.getItems().get(0).getId());
    			assertEquals("Item 1", dto.getItems().get(0).getName());
    			assertNotNull(dto.getItems().get(0).getCreateTime());
    			
    			assertEquals(item2Id, dto.getItems().get(1).getId());
    			assertEquals("Item 2", dto.getItems().get(1).getName());
    			assertNotNull(dto.getItems().get(1).getCreateTime());
    		});
	}
	
	@Test
	void testGetItemsWithPagingPage1() {
		webClient
        	.get().uri("/items/paging?page=1&size=2&sort=name,asc")
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBody(ItemsResponseDto.class)
        	.consumeWith(result -> {
        		ItemsResponseDto dto = result.getResponseBody();
    			
    			assertNotNull(dto);
    			assertEquals(1, dto.getAmount());
    			assertEquals(3, dto.getTotalAmount());
    			
    			assertEquals(item3Id, dto.getItems().get(0).getId());
    			assertEquals("Item 3", dto.getItems().get(0).getName());
    			assertNotNull(dto.getItems().get(0).getCreateTime());
    		});
	}
	
	@Test
	void testGetItemsWithSkipAndTakePage0() {
		webClient
        	.get().uri("/items/skip?page=0&size=2&sort=name,asc")
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBody(ItemsResponseDto.class)
        	.consumeWith(result -> {
        		ItemsResponseDto dto = result.getResponseBody();
    			
    			assertNotNull(dto);
    			assertEquals(2, dto.getAmount());
    			assertEquals(3, dto.getTotalAmount());
    			
    			assertNotNull(dto.getItems());
    			assertEquals(2, dto.getItems().size());
    			
    			assertEquals(item1Id, dto.getItems().get(0).getId());
    			assertEquals("Item 1", dto.getItems().get(0).getName());
    			assertNotNull(dto.getItems().get(0).getCreateTime());
    			
    			assertEquals(item2Id, dto.getItems().get(1).getId());
    			assertEquals("Item 2", dto.getItems().get(1).getName());
    			assertNotNull(dto.getItems().get(1).getCreateTime());
    		});
	}
	
	@Test
	void testGetItemsWithSkipAndTakePage1() {
		webClient
        	.get().uri("/items/skip?page=1&size=2&sort=name,asc")
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBody(ItemsResponseDto.class)
        	.consumeWith(result -> {
        		ItemsResponseDto dto = result.getResponseBody();
    			
    			assertNotNull(dto);
    			assertEquals(1, dto.getAmount());
    			assertEquals(3, dto.getTotalAmount());
    			
    			assertEquals(item3Id, dto.getItems().get(0).getId());
    			assertEquals("Item 3", dto.getItems().get(0).getName());
    			assertNotNull(dto.getItems().get(0).getCreateTime());
    		});
	}
	
	@Test
	void testGetItem_NotFound() {
		webClient
			.mutateWith(SecurityMockServerConfigurers.mockJwt())
        	.get().uri("/items/2d579439-1d07-4411-9be1-c2f466244f5e")
        	.exchange()
        	.expectStatus().isNotFound();
	}
	
	@Test
	void testAddItem() {
		ItemDto input = new ItemDto("Test item", "Test item description");
		
		webClient
			.mutateWith(SecurityMockServerConfigurers.mockJwt())
        	.post().uri("/items").bodyValue(input)
        	.exchange()
        	.expectStatus().isCreated()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBody(ItemResponseDto.class)
        	.consumeWith(result -> {
    			ItemResponseDto dto = result.getResponseBody();
    			
    			assertNotNull(dto);
    			assertNotNull(dto.getId());
    			assertEquals("Test item", dto.getName());
    			assertEquals("Test item description", dto.getDescription());
    			assertNotNull(dto.getCreateTime());
    			assertEquals("user", dto.getCreatedBy());
    		});
	}
	
	@Test
	void testUpdateItem() {
		ItemDto input = new ItemDto("Item 1", "New description for Item 1");
		
		webClient
			.mutateWith(SecurityMockServerConfigurers.mockJwt())
        	.put().uri("/items/" + item1Id).bodyValue(input)
        	.exchange()
        	.expectStatus().isOk()
        	.expectHeader().contentType(MediaType.APPLICATION_JSON)
        	.expectBody(ItemResponseDto.class)
        	.consumeWith(result -> {
    			ItemResponseDto dto = result.getResponseBody();
    			
    			assertNotNull(dto);
    			assertNotNull(dto.getId());
    			assertEquals("Item 1", dto.getName());
    			assertEquals("New description for Item 1", dto.getDescription());
    			assertNotNull(dto.getUpdateTime());
    			assertEquals("user", dto.getUpdatedBy());
    		});
	}
	
	@Test
	void testUpdateItem_NotFound() {
		ItemDto input = new ItemDto("Item 9", "New description for Item 9");
		
		webClient
			.mutateWith(SecurityMockServerConfigurers.mockJwt())
        	.put().uri("/items/2d579439-1d07-4411-9be1-c2f466244f5e").bodyValue(input)
        	.exchange()
        	.expectStatus().isNotFound();
	}
	
	@Test
	void testDeleteItem() {
		assertNotNull(itemRepository.findByItemId(item2Id).block());
		
		webClient
			.mutateWith(SecurityMockServerConfigurers.mockJwt())
        	.delete().uri("/items/" + item2Id)
        	.exchange()
        	.expectStatus().isNoContent()
        	.expectBody().isEmpty();
		
		assertNull(itemRepository.findByItemId(item2Id).block());
	}
}
