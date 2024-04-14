package com.votalks.global.error.model;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {
	//FailNotFound 404 error
	FAIL_NOT_VOTE_OPTION_FOUND("해당 투표 내용을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	FAIL_NOT_VOTE_FOUND("투표를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	FAIL_NOT_COMMENT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	FAIL_NOT_REPLY_FOUND("답글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
	VOTE_LIMIT_REACHED("투표 한도에 도달했습니다", HttpStatus.BAD_REQUEST),
	FAIL_ALREADY_VOTE("이미 투표를 하셨습니다.", HttpStatus.BAD_REQUEST);

	private String message;
	private HttpStatus statusCode;
}
