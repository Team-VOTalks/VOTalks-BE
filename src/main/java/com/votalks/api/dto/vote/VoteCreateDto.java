package com.votalks.api.dto.vote;

import java.util.List;

public record VoteCreateDto(
	String uuid,
	String title,
	String category,
	String description,
	List<String> voteOptions,
	int selectCount
) {
}
