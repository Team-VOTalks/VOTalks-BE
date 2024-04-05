package com.votalks.api.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
<<<<<<< HEAD
@Table(name = "like")
=======
@Table(name = "comment")
>>>>>>> 64f53192a7ca5b881f668fa6734aa3c3a6b386a8
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "like_count", nullable = false)
	private int likeCount;

	@Column(name = "dislike_count", nullable = false)
	private int dislikeCount;
}
