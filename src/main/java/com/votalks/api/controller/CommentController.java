package com.votalks.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.votalks.api.dto.comment.CommentCreateDto;
import com.votalks.api.dto.comment.CommentLikeDto;
import com.votalks.api.dto.comment.CommentReadDto;
import com.votalks.api.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments/{id}")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping
	public void create(@RequestBody @Valid CommentCreateDto dto, @PathVariable Long id) {
		commentService.create(dto, id);
	}

	@PostMapping("/comment/{commentId}/like")
	public void like(
		@PathVariable Long id,
		@PathVariable Long commentId,
		@RequestBody CommentLikeDto dto
	) {
		commentService.like(id, commentId, dto);
	}

	@GetMapping
	public Page<CommentReadDto> read(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@PathVariable Long id) {
		return commentService.read(id, page, size);
	}
}
