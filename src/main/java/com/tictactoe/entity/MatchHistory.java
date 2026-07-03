package com.tictactoe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false, length = 255)
    private String gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Player whose perspective this row represents

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_id", nullable = false)
    private User opponent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner; // Null if draw

    @Column(name = "duration_seconds")
    @Builder.Default
    private int durationSeconds = 0;

    @Column(name = "moves_count")
    @Builder.Default
    private int movesCount = 0;

    @Column(name = "result", nullable = false, length = 50)
    private String result; // WIN, LOSS, DRAW

    @Column(name = "played_date", nullable = false)
    private LocalDateTime playedDate;

    @PrePersist
    protected void onCreate() {
        this.playedDate = LocalDateTime.now();
    }
}
