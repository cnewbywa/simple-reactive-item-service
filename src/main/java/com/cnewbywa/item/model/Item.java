package com.cnewbywa.item.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document
public class Item {

	@Id
	private String id;
	@Builder.Default
	private String itemId = UUID.randomUUID().toString();
	@Size(min = 3, max = 50)
	@NotBlank
	private String name;
	@Size(min = 3, max = 500)
	private String description;
	@CreatedDate
	private Instant createTime;
	private String createdBy;
	@LastModifiedDate
	private Instant updateTime;
	private String updatedBy;
	@Version
	private long version; 
}
