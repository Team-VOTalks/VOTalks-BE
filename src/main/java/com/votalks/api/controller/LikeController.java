package com.votalks.api.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.comment.CommentLikeDto;
import com.votalks.api.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {
	private final LikeService likeService;

	@PostMapping("/vote/{vote-id}/comment/{comment-id}/like-type")
	public void like(
		@PathVariable(name = "vote-id") Long id,
		@PathVariable(name = "comment-id") Long commentId,
		@RequestBody CommentLikeDto dto
	) {
		likeService.like(id, commentId, dto);
	}
}
