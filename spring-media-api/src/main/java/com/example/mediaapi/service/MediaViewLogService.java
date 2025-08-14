package com.example.mediaapi.service;

import com.example.mediaapi.model.MediaAsset;
import com.example.mediaapi.model.MediaViewLog;
import com.example.mediaapi.repository.MediaViewLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MediaViewLogService {
    private final MediaViewLogRepository repo;

    public MediaViewLogService(MediaViewLogRepository repo) {
        this.repo = repo;
    }

    public void logView(MediaAsset media, String ip) {
        MediaViewLog log = MediaViewLog.builder()
                .media(media)
                .viewedByIp(ip)
                .timestamp(Instant.now())
                .build();
        repo.save(log);
    }
}
