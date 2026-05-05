package com.uniwork.modules.chat.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.chat.dto.ChatMessageDTO;
import com.uniwork.modules.chat.dto.ChatMessageResponseDTO;
import com.uniwork.modules.chat.dto.ChatRoomDTO;
import com.uniwork.modules.chat.dto.CreateGroupDTO;
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

import java.util.List;

@RestController
@RequestMapping("/chats")
@Slf4j
@PreAuthorize("hasAuthority('PERM_MANAGE_CHATS')")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/send")
    public void sendMessage(ChatMessageDTO message) {
        log.info("send message");
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
    public Long createDirectChat(@RequestParam Long user1,
                                 @RequestParam Long user2) {
        return chatService.createDirectChat(user1, user2);
    }

    @PostMapping("/group")
    public Long createGroupChat(@RequestAttribute Payload payload, @RequestBody CreateGroupDTO dto) {
        return chatService.createGroupChat(dto, payload.getUserId());
    }

    @PutMapping("/{roomId}/rename")
    public ResponseEntity renameGroupChat(@PathVariable Long roomId, @RequestAttribute Payload payload, @RequestParam String newName) {
        ChatRoom chatRoom = chatService.renameGroupChat(payload.getUserId(), roomId, newName);
        return ResponseFactory.success(chatRoom);
    }

}

