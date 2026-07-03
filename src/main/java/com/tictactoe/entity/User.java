package com.tictactoe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @Column(name = "id", nullable = false, length = 255)
    private String id; // Matches Supabase Auth user UUID

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "wins", nullable = false)
    @Builder.Default
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    @Builder.Default
    private int losses = 0;

    @Column(name = "draws", nullable = false)
    @Builder.Default
    private int draws = 0;

    @Column(name = "games_played", nullable = false)
    @Builder.Default
    private int gamesPlayed = 0;

    @Column(name = "online", nullable = false)
    @Builder.Default
    private boolean online = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.avatar == null) {
            // Default robohash avatar using the username
            this.avatar = "https://robohash.org/" + this.username + ".png?set=set4";
        }
    }
    
    public double getWinPercentage() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return Math.round(((double) wins / gamesPlayed) * 100.0 * 10.0) / 10.0;
    }
}
