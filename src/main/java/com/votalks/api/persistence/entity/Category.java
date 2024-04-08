package com.votalks.api.persistence.entity;

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

	public static boolean contains(String category) {
		for (Category c : Category.values()) {
			if (c.name().equals(category)) {
				return true;
			}
		}
		return false;
	}
}
