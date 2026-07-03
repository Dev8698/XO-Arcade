package com.tictactoe.service;

import com.tictactoe.entity.MatchHistory;
import com.tictactoe.repository.MatchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatchHistoryServiceImpl implements MatchHistoryService {

    private final MatchHistoryRepository matchHistoryRepository;

    @Autowired
    public MatchHistoryServiceImpl(MatchHistoryRepository matchHistoryRepository) {
        this.matchHistoryRepository = matchHistoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchHistory> getMatchHistory(String userId) {
        return matchHistoryRepository.findByUser_IdOrderByPlayedDateDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchHistory> getMatchHistoryFiltered(String userId, String filter) {
        if (filter == null || filter.trim().isEmpty() || "ALL".equalsIgnoreCase(filter)) {
            return getMatchHistory(userId);
        }
        return matchHistoryRepository.findByUser_IdAndResultOrderByPlayedDateDesc(userId, filter.toUpperCase().trim());
    }
}
