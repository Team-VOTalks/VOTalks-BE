package com.votalks.api.dto.vote;

import lombok.Builder;

@Builder
public record VoteOptionWithCountDto(
	Long id,
	String title,
	int count,
	boolean isCheck
) {
}
