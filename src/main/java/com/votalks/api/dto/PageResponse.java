package com.votalks.api.dto;

import java.util.List;

public record PageResponse<T>(
	List<T> data,
	PageInfo pageInfo
) {
}
