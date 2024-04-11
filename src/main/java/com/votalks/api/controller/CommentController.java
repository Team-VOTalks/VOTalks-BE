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
import com.votalks.api.dto.comment.CommentReadDto;
import com.votalks.api.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/votes/{vote-id}/comment")
	public void create(@RequestBody @Valid CommentCreateDto dto, @PathVariable(name = "vote-id") Long id) {
		commentService.create(dto, id);
	}

	@GetMapping("/votes/{vote-id}/comment")
	public Page<CommentReadDto> read(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@PathVariable(name = "vote-id") Long id) {
		return commentService.read(id, page, size);
	}
}
