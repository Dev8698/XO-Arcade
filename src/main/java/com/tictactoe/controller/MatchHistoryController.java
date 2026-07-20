package com.tictactoe.controller;

import com.tictactoe.entity.MatchHistory;
import com.tictactoe.security.AuthenticatedUser;
import com.tictactoe.service.MatchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;

    @Autowired
    public MatchHistoryController(MatchHistoryService matchHistoryService) {
        this.matchHistoryService = matchHistoryService;
    }

    @GetMapping("/api/history")
    public ResponseEntity<List<MatchHistory>> getHistoryJson(@AuthenticationPrincipal AuthenticatedUser authUser,
                                                             @RequestParam(required = false, defaultValue = "ALL") String filter) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<MatchHistory> history = matchHistoryService.getMatchHistoryFiltered(authUser.getId(), filter);
        return ResponseEntity.ok(history);
    }
}
