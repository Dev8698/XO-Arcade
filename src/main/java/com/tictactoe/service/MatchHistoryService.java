package com.tictactoe.service;

import com.tictactoe.entity.MatchHistory;
import java.util.List;

public interface MatchHistoryService {
    List<MatchHistory> getMatchHistory(String userId);
    List<MatchHistory> getMatchHistoryFiltered(String userId, String filter);
}
