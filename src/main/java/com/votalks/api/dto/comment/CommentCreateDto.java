package com.votalks.api.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDto(
	@NotBlank
	String content,
	String uuid
) {
}
