package com.example.skillswap.repository;

import com.example.skillswap.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByMatchMatchIdOrderBySentAtAsc(Long matchId);
}
