package com.uniwork.modules.chat.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.chat.dto.*;
import com.uniwork.modules.chat.entity.ChatRoom;
import com.uniwork.common.response.PageMetadata;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chats")
@Slf4j
//@PreAuthorize("hasAuthority('PERM_MANAGE_CHATS')")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/send")
    public void sendMessage(ChatMessageDTO message, Principal principal) {
        // ✅ Dùng userId từ authenticated principal, không tin FE
        Long authenticatedUserId = Long.parseLong(principal.getName());
        message.setSenderId(authenticatedUserId);  // Override FE value

        chatService.sendMessage(message);
    }


    @GetMapping("")
    public ResponseEntity getAllMessages(@RequestAttribute Payload payload) {
        List<ChatRoomDTO> chatRoomDTOList = chatService.getChatRoomsByUser(payload.getUserId());
        return ResponseFactory.success(chatRoomDTOList);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId, Pageable pageable, @RequestAttribute Payload payload) {
        Page<ChatMessageResponseDTO> page = chatService.getChatHistoryByRoomId(roomId, payload.getUserId(), pageable);

        PageMetadata metadata = PageMetadata.of(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );

        return ResponseFactory.makePagination(page.getContent(), metadata);
    }

    @PostMapping("/direct")
    public ResponseEntity createDirectChat(@RequestParam Long user1,
                                            @RequestParam Long user2) {
        Long roomId = chatService.createDirectChat(user1, user2);
        return ResponseFactory.success(roomId);
    }

    @PostMapping("/group")
    public ResponseEntity createGroupChat(@RequestAttribute Payload payload, @RequestBody CreateGroupDTO dto) {
        Long roomId = chatService.createGroupChat(dto, payload.getUserId());
        return ResponseFactory.success(roomId);
    }

    @PutMapping("/{roomId}/rename")
    public ResponseEntity renameGroupChat(@PathVariable Long roomId, @RequestAttribute Payload payload, @RequestParam String newName) {
        ChatRoom chatRoom = chatService.renameGroupChat(payload.getUserId(), roomId, newName);
        return ResponseFactory.success(chatRoom);
    }

    @PostMapping("/{roomId}/members")
    public ResponseEntity addMemberToGroup(@PathVariable Long roomId,
                                            @RequestParam Long memberId,
                                            @RequestAttribute Payload payload) {
        chatService.addMemberToGroup(payload.getUserId(), roomId, memberId);
        return ResponseFactory.success("Member added successfully");
    }

    @DeleteMapping("/{roomId}/members/{userId}")
    public ResponseEntity removeMemberFromGroup(@PathVariable Long roomId,
                                                 @PathVariable Long userId,
                                                 @RequestAttribute Payload payload) {
        chatService.removeMemberFromGroup(payload.getUserId(), roomId, userId);
        return ResponseFactory.success("Member removed successfully");
    }

    // ========= ChatController.java =========
    @MessageMapping("/typing")
    public void handleTyping(TypingEvent event, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        event.setUserId(userId);

        chatService.typeMessage(event);

    }

}

