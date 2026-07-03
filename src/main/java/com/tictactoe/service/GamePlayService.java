package com.tictactoe.service;

import com.tictactoe.entity.GameSession;

public interface GamePlayService {
    GameSession makeMove(String gameId, String playerId, int cellIndex);
}
