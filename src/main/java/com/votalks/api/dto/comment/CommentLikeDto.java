package com.votalks.api.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentLikeDto(
	String uuid,
	@NotBlank
	String likeType
) {
}
