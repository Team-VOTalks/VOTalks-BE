package com.votalks.api.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.like.LikeCreateDto;
import com.votalks.api.service.LikeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {
	private final LikeService likeService;

	@PostMapping("/votes/{vote-id}/comments/{comment-id}/like-type")
	public void like(
		@PathVariable(name = "vote-id") Long id,
		@PathVariable(name = "comment-id") Long commentId,
		@RequestBody LikeCreateDto dto,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		likeService.like(id, commentId, dto, request, response);
	}

	@PostMapping("/votes/{vote-id}/comments/{comment-id}/replies/{reply-id}/like-type")
	public void like(
		@PathVariable(name = "vote-id") Long voteId,
		@PathVariable(name = "comment-id") Long commentId,
		@PathVariable(name = "reply-id") Long replyId,
		@RequestBody LikeCreateDto dto,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		likeService.like(voteId, commentId, replyId, dto, request, response);
	}
}
