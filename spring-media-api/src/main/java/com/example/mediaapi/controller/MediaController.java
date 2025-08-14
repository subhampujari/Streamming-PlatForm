package com.example.mediaapi.controller;

import com.example.mediaapi.dto.MediaDtos.*;
import com.example.mediaapi.model.MediaAsset;
import com.example.mediaapi.security.StreamTokenService;
import com.example.mediaapi.service.MediaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;
    private final StreamTokenService streamTokenService;

    public MediaController(MediaService mediaService, StreamTokenService streamTokenService) {
        this.mediaService = mediaService;
        this.streamTokenService = streamTokenService;
    }

    @PostMapping
    public ResponseEntity<MediaResponse> create(@Valid @RequestBody CreateMediaRequest req,
                                                @AuthenticationPrincipal UserDetails user) {
        MediaAsset.MediaType type = req.type().name().equalsIgnoreCase("VIDEO")
                ? MediaAsset.MediaType.VIDEO : MediaAsset.MediaType.AUDIO;
        MediaAsset saved = mediaService.create(req.title(), type, req.fileUrl());
        return ResponseEntity.ok(new MediaResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getType().name(),
                saved.getFileUrl(),
                saved.getCreatedAt().toString()
        ));
    }

    @GetMapping("/{id}/stream-url")
    public ResponseEntity<StreamUrlResponse> getStreamUrl(@PathVariable Long id, HttpServletRequest request) {
        MediaAsset m = mediaService.getById(id);
        String ip = request.getRemoteAddr();
        String token = streamTokenService.generateStreamToken(m.getId(), ip);
        long expiresAt = Instant.now().toEpochMilli() + streamTokenService.getExpirationMs();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + "/stream?token=" + token;
        return ResponseEntity.ok(new StreamUrlResponse(url, expiresAt));
    }
}
