package com.tictactoe.repository;

import com.tictactoe.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUser_Id(String userId);
    boolean existsByUser_IdAndFriend_Id(String userId, String friendId);
    Optional<Friend> findByUser_IdAndFriend_Id(String userId, String friendId);
    void deleteByUser_IdAndFriend_Id(String userId, String friendId);
}
