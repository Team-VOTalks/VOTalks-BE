package com.votalks.global.common.util;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
	LATEST("latest", "createdAt", Sort.Direction.DESC),
	OLDEST("oldest", "createdAt", Sort.Direction.ASC),
	POPULAR("popular", "popularScore", Sort.Direction.DESC),
	MOST_COMMENTED("mostCommented", "totalReplyCount", Sort.Direction.DESC);

	private final String value;
	private final String field;
	private final Sort.Direction direction;

	public Sort getSort() {
		return Sort.by(new Sort.Order(direction, field));
	}

	private static final Map<String, SortType> names =
		Collections.unmodifiableMap(Stream.of(values())
			.collect(Collectors.toMap(SortType::getValue, Function.identity())));

	public static SortType from(String sortType) {
		return names.get(sortType);
	}
}



