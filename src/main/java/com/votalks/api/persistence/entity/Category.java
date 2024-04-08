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
	DEV("개발"),
	DATE("연애"),
	DAILY("일상"),
	COMPANY("회사"),
	FRIEND("친구"),
	FAMILY("가족");
	private final String name;

	private static final Map<String, Category> names =
		Collections.unmodifiableMap(Stream.of(values())
			.collect(Collectors.toMap(Category::getName, Function.identity())));

	public static boolean contains(String category) {
		return names.containsKey(category);
	}
}
