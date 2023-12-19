package com.cnewbywa.item.model;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ItemDto implements Serializable {

	private static final long serialVersionUID = -8512043076502599275L;
	
	@NotBlank
	@Size(min = 3, max = 50)
	private String name;
	@NotBlank
	@Size(min = 3, max = 500)
	private String description;
}
