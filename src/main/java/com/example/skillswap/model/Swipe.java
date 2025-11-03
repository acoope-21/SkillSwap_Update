package com.example.skillswap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "swipe")
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swipe_id")
    private Long swipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiper_id", referencedColumnName = "user_id")
    private User swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swipee_id", referencedColumnName = "user_id")
    private User swipee;

    @Column(name = "is_like")
    private Boolean isLike;

    @Column(name = "swiped_at")
    private LocalDateTime swipedAt = LocalDateTime.now();

    public Swipe() {}

    // Getters & Setters
    public Long getSwipeId() { return swipeId; }
    public void setSwipeId(Long swipeId) { this.swipeId = swipeId; }

    public User getSwiper() { return swiper; }
    public void setSwiper(User swiper) { this.swiper = swiper; }

    public User getSwipee() { return swipee; }
    public void setSwipee(User swipee) { this.swipee = swipee; }

    public Boolean getIsLike() { return isLike; }
    public void setIsLike(Boolean isLike) { this.isLike = isLike; }

    public LocalDateTime getSwipedAt() { return swipedAt; }
    public void setSwipedAt(LocalDateTime swipedAt) { this.swipedAt = swipedAt; }
}
