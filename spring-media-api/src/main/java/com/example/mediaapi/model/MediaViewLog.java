package com.example.mediaapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "media_view_logs", indexes = {
        @Index(name = "idx_media_time", columnList = "media_id, timestamp")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MediaViewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "media_id")
    private MediaAsset media;

    @Column(nullable = false, name = "viewed_by_ip")
    private String viewedByIp;

    @Column(nullable = false)
    private Instant timestamp;
}
