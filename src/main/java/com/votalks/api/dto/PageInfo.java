package com.votalks.api.dto;

import org.springframework.data.domain.Page;

public record PageInfo(
	int pageIndex,
	int totalPageLength,
	boolean done
) {
	public <T> PageInfo(Page<T> page) {
		this(page.getNumber(), page.getTotalPages(), page.isLast());
	}
}
