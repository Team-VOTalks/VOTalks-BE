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
	CREATE("생성", "create"),
	LIKE("좋아요", "like"),
	DISLIKE("싫어요", "dislike");
	private final String name;
	private final String value;

	private static final Map<String, LikeType> names =
		Collections.unmodifiableMap(Stream.of(values())
			.collect(Collectors.toMap(LikeType::getValue, Function.identity())));

	public boolean isLike() {
		return this.equals(LIKE);
	}

	public boolean isDislike() {
		return this.equals(DISLIKE);
	}

	public static LikeType from(String value) {
		return names.get(value);
	}
}
