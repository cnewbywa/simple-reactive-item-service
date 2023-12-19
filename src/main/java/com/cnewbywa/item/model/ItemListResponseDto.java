package com.cnewbywa.item.model;

import java.io.Serializable;
import java.time.Instant;

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
public class ItemListResponseDto implements Serializable {
	
	private static final long serialVersionUID = 2927572709744069183L;
	
	private String id;
	private String name;
	private Instant createTime;
}
