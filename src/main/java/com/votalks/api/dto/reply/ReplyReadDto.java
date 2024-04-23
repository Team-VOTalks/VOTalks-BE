package com.votalks.api.dto.reply;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ReplyReadDto(
	Long replyId,
	int userIndex,
	String content,
	LocalDateTime createAt,
	int likeCount,
	int dislikeCount,
	String likeType
) {
}
