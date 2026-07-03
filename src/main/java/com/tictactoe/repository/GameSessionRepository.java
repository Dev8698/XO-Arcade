package com.tictactoe.repository;

import com.tictactoe.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, String> {
    Optional<GameSession> findByGameCode(String gameCode);
    Optional<GameSession> findByGameCodeAndStatus(String gameCode, String status);
}
