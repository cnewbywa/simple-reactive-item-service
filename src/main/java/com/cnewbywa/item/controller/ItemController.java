package com.cnewbywa.item.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cnewbywa.item.model.ItemDto;
import com.cnewbywa.item.model.ItemListResponseDto;
import com.cnewbywa.item.model.ItemResponseDto;
import com.cnewbywa.item.model.ItemsResponseDto;
import com.cnewbywa.item.service.ItemService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

	private ItemService itemService;
	
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
	
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "bearerAuth")
	public Mono<ItemResponseDto> getItem(@PathVariable String id) {
		return itemService.getItem(id);
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Flux<ItemListResponseDto> getItems(@RequestParam(value = "sort", defaultValue = "name,desc") String[] sort) {
		return itemService.getItems(Sort.by(getSortOrders(sort)));
	}
	
	@GetMapping(path = "/paging", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Mono<ItemsResponseDto> getItemsWithPaging(@DefaultValue("0") @RequestParam("page") int pageIndex, 
    		@DefaultValue("100") @RequestParam("size") int pageSize, 
    		@DefaultValue("name,desc") @RequestParam("sort") String[] sort) {
		
		return itemService.getItemsWithPaging(PageRequest.of(pageIndex, pageSize, Sort.by(getSortOrders(sort))));
	}
	
	@GetMapping(path = "/skip", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Mono<ItemsResponseDto> getItemsWithSkipAndTake(@DefaultValue("0") @RequestParam("page") int pageIndex, 
    		@DefaultValue("100") @RequestParam("size") int pageSize, 
    		@DefaultValue("name,desc") @RequestParam("sort") String[] sort) {
		
		return itemService.getItemsWithSkipAndTake(PageRequest.of(pageIndex, pageSize, Sort.by(getSortOrders(sort))));
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "bearerAuth")
	public Mono<ItemResponseDto> addItem(Authentication authentication, @RequestBody @Nonnull ItemDto item) {
		return itemService.addItem(item, getLoggedInUser(authentication));
	}
	
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "bearerAuth")
	public Mono<ItemResponseDto> updateItem(Authentication authentication, @PathVariable String id, @RequestBody @Nonnull ItemDto item) {
		return itemService.updateItem(id, item, getLoggedInUser(authentication));
	}
	
	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	public Mono<Void> deleteItem(@PathVariable String id) {
		return itemService.deleteItem(id);
	}
	
	private String getLoggedInUser(Authentication authentication) {
		if (authentication == null || authentication.getName() == null) {
			log.error("Username cannot be found");
			
			throw new UsernameNotFoundException("User not found");
		}
		
		return authentication.getName();
	}
	
	private List<Order> getSortOrders(String[] sort) {
		List<Order> orders;
		
		if (!sort[0].contains(",")) { // only one sort order
			orders = new ArrayList<>(List.of(new Order(Sort.Direction.fromString(sort[1]), sort[0])));
		} else { // multiple sort orders
			orders = Arrays.asList(sort).stream().map(s -> {
				String[] order = s.split(",");
				
				return new Order(Sort.Direction.fromString(order[1]), order[0]);
			}).toList();
		}
		
		return orders;
	}
}
