package com.example.skillswap.repository;

import com.example.skillswap.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE " +
           "(m.user1.userId = :u1 AND m.user2.userId = :u2) OR " +
           "(m.user1.userId = :u2 AND m.user2.userId = :u1)")
    Optional<Match> findExistingMatch(@Param("u1") Long user1, @Param("u2") Long user2);
}
