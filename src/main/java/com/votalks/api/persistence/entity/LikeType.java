package com.votalks.api.persistence.entity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LikeType {
	CREATE("생성"),
	LIKE("좋아요"),
	DISLIKE("싫어요");
	private final String name;

	private static final Map<String, LikeType> names =
		Collections.unmodifiableMap(Stream.of(values())
			.collect(Collectors.toMap(LikeType::getName, Function.identity())));
}
