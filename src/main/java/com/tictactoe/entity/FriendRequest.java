package com.tictactoe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_requests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "PENDING"; // PENDING, REJECTED

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }
}
