package com.example.mediaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MediaDtos {

    public enum Type { VIDEO, AUDIO }

    public record CreateMediaRequest(
            @NotBlank String title,
            @NotNull Type type,
            @NotBlank String fileUrl
    ) {}

    public record MediaResponse(
            Long id, String title, String type, String fileUrl, String createdAt
    ) {}

    public record StreamUrlResponse(String url, long expiresAtEpochMs) {}
}
