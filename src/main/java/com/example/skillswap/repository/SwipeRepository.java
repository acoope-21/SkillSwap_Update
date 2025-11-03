package com.example.skillswap.repository;

import com.example.skillswap.model.Swipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    List<Swipe> findBySwiperUserId(Long swiperId);
    Optional<Swipe> findBySwiperUserIdAndSwipeeUserId(Long swiperId, Long swipeeId);
}
