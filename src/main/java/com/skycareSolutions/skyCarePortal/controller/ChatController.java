package com.skycareSolutions.skyCarePortal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.Chat;
import com.skycareSolutions.skyCarePortal.model.Message;
import com.skycareSolutions.skyCarePortal.service.ChatService;
import com.skycareSolutions.skyCarePortal.utils.UserSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/send-chat/{chatId}")
    @SendTo("/topic/chat-{chatId}")
    public MessageDTO sendMessage(@DestinationVariable long chatId, @Payload String rawMessage) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ChatMessageDTO message = objectMapper.readValue(rawMessage, ChatMessageDTO.class);
            MessageBroadcasterDTO message1 = chatService.saveMessage(message);
            return message1.getMessageData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid JSON format");
        }
    }


    // This method is to handle when a user connects and joins the WebSocket
    @MessageMapping("/join")
    public void join(@Payload String userId, SimpMessageHeaderAccessor headerAccessor) {

        // Store user ID in WebSocket session
        headerAccessor.getSessionAttributes().put("userId", userId);
        UserSessionRegistry.addUserSession(userId, headerAccessor.getSessionId());
    }

    // This method is to handle when a user disconnects and remove their session
    @MessageMapping("/disconnect/{userId}")
    public void disconnect(String userId) {
        // Remove the WebSocket session from the map
        UserSessionRegistry.removeUserSession(userId);
        System.out.println(userId + " disconnected.");
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

    @GetMapping("/messages/{chatId}")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable long chatId){
        return ResponseEntity.ok(chatService.getAllMessagesRelatedToChat(chatId));
    }

    @PostMapping("/documents")
    public ResponseEntity<List<String>> saveAllChatDocuments(@RequestParam(value = "documents",required = true) List<MultipartFile> documents){
        return ResponseEntity.ok(chatService.addChatDocument(documents));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMessage(@RequestParam long messageID){
        return ResponseEntity.ok(chatService.deleteMessage(messageID));
    }

    @GetMapping("/members/{chatId}")
    public ResponseEntity<List<ChatMemberDTO>> getChatMembers(@PathVariable long chatId){
        return ResponseEntity.ok(chatService.getAllChatMembers(chatId));
    }

    @DeleteMapping("/member")
    public ResponseEntity<String> deleteChatMember(@RequestParam long chatID,@RequestParam String userID){
        return ResponseEntity.ok(chatService.deleteChatMember(chatID,userID));
    }

    @PutMapping("{chatId}")
    public ResponseEntity<Chat> updatePublicChat(@PathVariable long chatId, @RequestBody GroupChatRequestDTO groupChatRequest) {
        Chat chat = chatService.updateChat(groupChatRequest,chatId);
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @GetMapping("/seen/{userId}")
    public ResponseEntity<HashMap<Long, Integer>> getUnseenMessages(@PathVariable String userId) {
        HashMap<Long, Integer> unseenMessages = chatService.getUnseenMessagesForUser(userId);
        return ResponseEntity.ok(unseenMessages);
    }

    @PutMapping("/seen")
    public ResponseEntity<String> updateUnseenMessages(@RequestParam String userId,@RequestParam long chatId){
        return ResponseEntity.ok(chatService.updateMessageSeen(userId,chatId));
    }

}
