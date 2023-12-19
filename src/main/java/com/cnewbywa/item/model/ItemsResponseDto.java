package com.cnewbywa.item.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemsResponseDto implements Serializable {

	private static final long serialVersionUID = -5167412405072446366L;
	
	private List<ItemListResponseDto> items;
	private long amount;
	private long totalAmount;
}
