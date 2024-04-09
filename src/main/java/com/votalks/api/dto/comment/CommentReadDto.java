package com.votalks.api.dto.comment;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommentReadDto(
	int userNumber,
	String content,
	LocalDateTime createAt,
	int likeCount,
	int dislikeCount,
	int totalReplyCount
) {
}
