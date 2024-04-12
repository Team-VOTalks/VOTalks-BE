package com.votalks.api.dto.vote;

import jakarta.validation.constraints.NotNull;

public record VoteTakeDto(
	@NotNull
	Long voteOptionId
) {
}
