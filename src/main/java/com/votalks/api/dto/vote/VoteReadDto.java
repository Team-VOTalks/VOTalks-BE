package com.votalks.api.dto.vote;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;

@Builder
public record VoteReadDto(
	String title,
	String category,
	LocalDateTime createAt,
	int totalVoteCount,
	String description,
	Map<String, Integer> voteOptionsWithCount,
	int totalCommentCount
) {
}
