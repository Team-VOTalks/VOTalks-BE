package com.votalks.api.dto.reply;

import jakarta.validation.constraints.NotBlank;

public record ReplyCreateDto(
	@NotBlank
	String content
) {
}
