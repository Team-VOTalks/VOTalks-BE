package com.votalks.api.dto.vote;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoteCreateDto(
	String uuid,
	@NotBlank @Length(min = 4, message = "투표 제목은 최소 5자 이상이어야 합니다.")
	String title,
	@NotBlank
	String category,
	@Length(max = 300, message = "투표 설명은 최대 200자까지 가능합니다.")
	String description,
	@NotNull
	List<String> voteOptions,
	@Min(value = 1, message = "투표 선택의 수는 최소 1 이상이어야 합니다.")
	int selectCount
) {
}
