package com.tictactoe.repository;

import com.tictactoe.entity.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Long> {
    List<MatchHistory> findByUser_IdOrderByPlayedDateDesc(String userId);
    List<MatchHistory> findByUser_IdAndResultOrderByPlayedDateDesc(String userId, String result);
}
