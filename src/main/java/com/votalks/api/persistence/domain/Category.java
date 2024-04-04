package com.votalks.api.persistence.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
	CULTURE("문화"),
	TECH("기술"),
	DAILY("일상");

	private final String name;
}
