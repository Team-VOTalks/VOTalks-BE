package com.votalks.api.dto.voteOption;

import lombok.Builder;

@Builder
public record VoteOptionReadDto(
	Long id,
	String title,
	int count,
	boolean isCheck
) {
}
