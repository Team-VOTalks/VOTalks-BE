package com.votalks.api.dto;

public record PageInfo(
	int pageIndex,
	int totalPageLength,
	boolean done
) {
}
