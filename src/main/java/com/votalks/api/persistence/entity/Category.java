package com.votalks.api.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
	CULTURE("문화"),
	TECH("기술"),
	DAILY("일상");

	private final String name;

	public static boolean contains(String category) {
		for (Category c : Category.values()) {
			if (c.name().equals(category)) {
				return true;
			}
		}
		return false;
	}
}
