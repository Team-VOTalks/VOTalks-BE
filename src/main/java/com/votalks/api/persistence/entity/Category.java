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
public enum Category {
	DEV("개발", "dev"),
	DATE("연애", "date"),
	DAILY("일상", "daily"),
	COMPANY("회사", "company"),
	FRIEND("친구", "friend"),
	FAMILY("가족", "family");
	private final String name;
	private final String value;

	private static final Map<String, Category> names =
		Collections.unmodifiableMap(Stream.of(values())
			.collect(Collectors.toMap(Category::getName, Function.identity())));

	private static final Map<String, Category> valuesMap = Collections.unmodifiableMap(Stream.of(values())
		.collect(Collectors.toMap(Category::getValue, Function.identity())));

	public static boolean contains(String category) {
		return names.containsKey(category);
	}

	public static Category fromValue(String value) {
		return valuesMap.get(value);
	}
}
