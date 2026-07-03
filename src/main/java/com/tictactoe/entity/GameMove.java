package com.tictactoe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_moves")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GameMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @Column(name = "move_index", nullable = false)
    private int moveIndex; // Grid position 0-8

    @Column(name = "symbol", nullable = false, length = 1)
    private String symbol; // 'X' or 'O'

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }
}
