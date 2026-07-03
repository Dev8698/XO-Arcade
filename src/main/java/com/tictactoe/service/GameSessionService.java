package com.tictactoe.service;

import com.tictactoe.entity.GameSession;

public interface GameSessionService {
    GameSession createGameSession(String creatorId);
    GameSession joinGameSession(String playerId, String gameCode);
    GameSession getGameSession(String id);
    void cancelGameSession(String id, String playerId);
    void leaveGameSession(String id, String playerId);
    
    // Friends challenges
    void challengeFriend(String challengerId, String friendId);
    GameSession acceptChallenge(Long notificationId);
    void rejectChallenge(Long notificationId);
}
