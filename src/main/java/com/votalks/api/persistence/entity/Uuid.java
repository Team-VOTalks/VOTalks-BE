package com.votalks.api.persistence.entity;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "uuid")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uuid {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	private Uuid(UUID id) {
		this.id = id;
	}

	public static Uuid create(UUID uuid) {
		return new Uuid(uuid);
	}

	public static UUID fromString(String uuid) {
		return UUID.fromString(
			uuid.replaceFirst(
				"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
				"$1-$2-$3-$4-$5"));
	}
}


