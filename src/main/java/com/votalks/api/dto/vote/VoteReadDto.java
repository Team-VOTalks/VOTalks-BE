package com.votalks.api.dto.vote;

import java.time.LocalDateTime;
import java.util.List;

import com.votalks.api.dto.voteOption.VoteOptionReadDto;

import lombok.Builder;

@Builder
public record VoteReadDto(
	Long voteId,
	String title,
	String category,
	LocalDateTime createAt,
	int totalVoteCount,
	String description,
	List<VoteOptionReadDto> voteOptionWithCounts,
	int totalCommentCount
) {
}
