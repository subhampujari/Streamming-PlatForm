package com.example.mediaapi.service;

import com.example.mediaapi.model.MediaAsset;
import com.example.mediaapi.repository.MediaAssetRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MediaService {
    private final MediaAssetRepository repo;

    public MediaService(MediaAssetRepository repo) {
        this.repo = repo;
    }

    public MediaAsset create(String title, MediaAsset.MediaType type, String fileUrl) {
        MediaAsset asset = MediaAsset.builder()
                .title(title)
                .type(type)
                .fileUrl(fileUrl)
                .createdAt(Instant.now())
                .build();
        return repo.save(asset);
    }

    public MediaAsset getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Media not found"));
    }
}
