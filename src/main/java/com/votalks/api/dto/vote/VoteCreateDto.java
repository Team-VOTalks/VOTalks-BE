package com.votalks.api.dto.vote;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoteCreateDto(
	String uuid,
	@NotBlank @Length(min = 4, message = "투표 제목은 최소 4자 이상이어야 합니다.")
	String title,
	@NotBlank(message = "카테고리는 필수로 입력하셔야 합니다.")
	String category,
	@Length(max = 300, message = "투표 설명은 최대 300자까지 가능합니다.")
	String description,
	@NotNull(message = "투표 선택지는 필수로 입력하셔야 합니다.")
	List<String> voteOptions
) {
}
