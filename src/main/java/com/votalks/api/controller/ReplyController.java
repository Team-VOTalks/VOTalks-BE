package com.votalks.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.reply.ReplyCreateDto;
import com.votalks.api.service.ReplyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyController {
	private final ReplyService replyService;

	@PostMapping("/votes/{vote-id}/comments/{comment-id}")
	public HttpHeaders create(
		@RequestBody @Valid ReplyCreateDto dto,
		@PathVariable(name = "vote-id") Long voteId,
		@PathVariable(name = "comment-id") Long commentId,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		return replyService.create(dto, voteId, commentId, request, response);
	}
}
