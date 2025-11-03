package com.example.skillswap.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.skillswap.model.Message;
import com.example.skillswap.repository.MessageRepository;
import com.example.skillswap.repository.MatchRepository;
import com.example.skillswap.repository.UserRepository;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepo;
    private final MatchRepository matchRepo;
    private final UserRepository userRepo;

    public MessageController(MessageRepository messageRepo, MatchRepository matchRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.matchRepo = matchRepo;
        this.userRepo = userRepo;
    }

    // Get all messages for a given match
    @GetMapping("/{matchId}")
    public List<Message> getMessagesByMatch(@PathVariable Long matchId) {
        return messageRepo.findByMatchMatchIdOrderBySentAtAsc(matchId);
    }

    // Send a message (linked to match + sender)
    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        message.setMatch(matchRepo.findById(message.getMatch().getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found")));
        message.setSender(userRepo.findById(message.getSender().getUserId())
                .orElseThrow(() -> new RuntimeException("Sender user not found")));

        return messageRepo.save(message);
    }

    // Mark message as read
    @PutMapping("/{id}/read")
    public Message markAsRead(@PathVariable Long id) {
        Message msg = messageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        msg.setIsRead(true);
        return messageRepo.save(msg);
    }
}
