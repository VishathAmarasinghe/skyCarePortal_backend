package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ChatDTO;
import com.skycareSolutions.skyCarePortal.dto.ChatMessageDTO;
import com.skycareSolutions.skyCarePortal.dto.ChatUserDTO;
import com.skycareSolutions.skyCarePortal.dto.GroupChatRequestDTO;
import com.skycareSolutions.skyCarePortal.model.Chat;
import com.skycareSolutions.skyCarePortal.model.Message;
import com.skycareSolutions.skyCarePortal.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage") // Clients send messages here
    @SendTo("/topic/messages") // Clients receive messages from this topic
    public Message sendMessage(ChatMessageDTO message) {
        return chatService.saveMessage(message);
    }

    @PostMapping("/private")
    public ResponseEntity<String> createPrivateChat(
            @RequestParam String user1,
            @RequestParam String user2) {
        try {
            String responseMessage = chatService.createPrivateChat(user1, user2);
            return ResponseEntity.ok(responseMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/group")
    public ResponseEntity<Chat> createPublicChat(@RequestBody GroupChatRequestDTO groupChatRequest) {
        Chat chat = chatService.createPublicChat(groupChatRequest);
        return new ResponseEntity<>(chat, HttpStatus.CREATED);
    }

    @GetMapping("/accessibleUsers/{userId}")
    public ResponseEntity<List<ChatUserDTO>> getAllAccessibleUsers(@PathVariable String userId){
        return new ResponseEntity<>(chatService.getAcceptedChatUsersForSelectedUser(userId),HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<List<ChatDTO>> getChats(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getListOfChatsByUser(userId));
    }
}
