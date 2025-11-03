package com.example.skillswap.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.skillswap.model.Swipe;
import com.example.skillswap.model.Match;
import com.example.skillswap.repository.SwipeRepository;
import com.example.skillswap.repository.UserRepository;
import com.example.skillswap.repository.MatchRepository;

@RestController
@RequestMapping("/api/swipes")
public class SwipeController {

    private final SwipeRepository swipeRepo;
    private final UserRepository userRepo;
    private final MatchRepository matchRepo;

    public SwipeController(SwipeRepository swipeRepo, UserRepository userRepo, MatchRepository matchRepo) {
        this.swipeRepo = swipeRepo;
        this.userRepo = userRepo;
        this.matchRepo = matchRepo;
    }

    // Get all swipes
    @GetMapping
    public List<Swipe> getAllSwipes() {
        return swipeRepo.findAll();
    }

    // Add a swipe (with validation and auto-match)
    @PostMapping
    public Swipe addSwipe(@RequestBody Swipe swipe) {
        // Ensure both users exist
        swipe.setSwiper(userRepo.findById(swipe.getSwiper().getUserId())
                .orElseThrow(() -> new RuntimeException("Swiper user not found")));
        swipe.setSwipee(userRepo.findById(swipe.getSwipee().getUserId())
                .orElseThrow(() -> new RuntimeException("Swipee user not found")));

        Swipe savedSwipe = swipeRepo.save(swipe);

        // --- AUTO-MATCH LOGIC ---
        if (Boolean.TRUE.equals(swipe.getIsLike())) {
            swipeRepo.findBySwiperUserIdAndSwipeeUserId(
                    swipe.getSwipee().getUserId(),
                    swipe.getSwiper().getUserId()
            ).ifPresent(reverseSwipe -> {
                if (Boolean.TRUE.equals(reverseSwipe.getIsLike())) {
                    boolean matchExists = matchRepo
                            .findExistingMatch(swipe.getSwiper().getUserId(), swipe.getSwipee().getUserId())
                            .isPresent();

                    if (!matchExists) {
                        Match newMatch = new Match();
                        newMatch.setUser1(swipe.getSwiper());
                        newMatch.setUser2(swipe.getSwipee());
                        matchRepo.save(newMatch);
                    }
                }
            });
        }

        return savedSwipe;
    }

    // Get all swipes made by a specific user
    @GetMapping("/user/{swiperId}")
    public List<Swipe> getSwipesByUser(@PathVariable Long swiperId) {
        return swipeRepo.findBySwiperUserId(swiperId);
    }
}
