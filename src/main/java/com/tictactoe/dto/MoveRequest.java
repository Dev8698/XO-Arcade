package com.tictactoe.dto;

import lombok.Data;

@Data
public class MoveRequest {
    private String gameId;
    private String playerId;
    private int cellIndex;
}
