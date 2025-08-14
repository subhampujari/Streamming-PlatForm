package com.example.mediaapi.controller;

import com.example.mediaapi.model.MediaAsset;
import com.example.mediaapi.security.StreamTokenService;
import com.example.mediaapi.service.MediaService;
import com.example.mediaapi.service.MediaViewLogService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StreamController {

    private final StreamTokenService streamTokenService;
    private final MediaService mediaService;
    private final MediaViewLogService logService;

    public StreamController(StreamTokenService streamTokenService, MediaService mediaService, MediaViewLogService logService) {
        this.streamTokenService = streamTokenService;
        this.mediaService = mediaService;
        this.logService = logService;
    }

    @GetMapping("/stream")
    public ResponseEntity<?> stream(@RequestParam("token") String token, HttpServletRequest request) {
        try {
            Claims claims = streamTokenService.parse(token);
            if (!"stream".equals(claims.get("type", String.class))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token type");
            }
            Long mediaId = claims.get("mediaId", Long.class);
            String ip = claims.get("ip", String.class);
            if (ip != null && !ip.equals(request.getRemoteAddr())) {
                // Optional: lock token to IP that requested it
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not valid for this IP");
            }
            MediaAsset media = mediaService.getById(mediaId);
            // Log the view
            logService.logView(media, request.getRemoteAddr());
            // Redirect to the actual file URL (could be S3/CDN)
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", media.getFileUrl())
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
