package com.votalks.api.dto;

import java.util.List;

public record PageResponse<T>(
	List<T> content,
	PageInfo pageInfo
) {
}
