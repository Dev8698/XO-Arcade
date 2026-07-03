package com.tictactoe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GameSession {

    @Id
    @Column(name = "id", nullable = false, length = 255)
    private String id; // UUID

    @Column(name = "game_code", nullable = false, unique = true, length = 10)
    private String gameCode; // 6 character code

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_x_id")
    private User playerX;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_o_id")
    private User playerO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_turn_id")
    private User currentTurn; // Whose turn it is to make a move

    @Column(name = "board_state", nullable = false, length = 100)
    @Builder.Default
    private String boardState = ",,,,,,,,"; // 9 empty spots separated by commas

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private User winner; // Null if ongoing or draw

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "WAITING"; // WAITING, PLAYING, WON, LOST, DRAW

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        if (this.boardState == null) {
            this.boardState = ",,,,,,,,";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
}
