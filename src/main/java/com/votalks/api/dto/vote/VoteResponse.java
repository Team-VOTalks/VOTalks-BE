package com.votalks.api.dto.vote;

import org.springframework.http.HttpHeaders;

import lombok.Builder;

@Builder
public record VoteResponse<T>(
	HttpHeaders httpHeaders,
	T data
) {
}
