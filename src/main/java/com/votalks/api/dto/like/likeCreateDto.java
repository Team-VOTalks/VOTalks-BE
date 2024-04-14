package com.votalks.api.dto.like;

import jakarta.validation.constraints.NotBlank;

public record likeCreateDto(
	@NotBlank
	String likeType
) {
}
