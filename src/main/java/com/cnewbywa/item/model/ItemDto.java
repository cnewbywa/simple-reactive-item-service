package com.cnewbywa.item.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ItemDto implements Serializable {

	private static final long serialVersionUID = -8512043076502599275L;
	
	private String name;
	private String description;
}
