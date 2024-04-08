package com.votalks.api.dto.vote;

public record VoteOptionWithCountDto(
	Long id,
	String title,
	int count
) {
}
