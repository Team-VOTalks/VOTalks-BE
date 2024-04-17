package com.votalks.api.dto.comment;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommentReadDto(
	Long commentId,
	int userNumber,
	String content,
	LocalDateTime createAt,
	int likeCount,
	int dislikeCount,
	int totalReplyCount, //적용해줘야함
	String likeType
) {
}
