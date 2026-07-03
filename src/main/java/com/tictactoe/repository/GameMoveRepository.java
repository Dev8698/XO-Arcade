package com.tictactoe.repository;

import com.tictactoe.entity.GameMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameMoveRepository extends JpaRepository<GameMove, Long> {
    List<GameMove> findByGameSession_IdOrderByCreatedTimeAsc(String gameSessionId);
}
