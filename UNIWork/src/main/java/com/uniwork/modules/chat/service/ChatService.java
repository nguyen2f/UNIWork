package com.uniwork.modules.chat.service;

import com.uniwork.modules.chat.dto.*;
import com.uniwork.modules.chat.entity.ChatRoom;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    void sendMessage(ChatMessageDTO dto);

    @Transactional
    Long createDirectChat(Long user1, Long user2);

    @Transactional
    Long createGroupChat(CreateGroupDTO dto, Long creatorId);

    List<ChatRoomDTO> getChatRoomsByUser(Long userId);

    Page<ChatMessageResponseDTO> getChatHistoryByRoomId(Long roomId, Long userId, Pageable pageable);

    @Transactional
    ChatRoom renameGroupChat(Long userId, Long roomId, String newName);

    @Transactional
    void addMemberToGroup(Long userId, Long roomId, Long newMemberId);

    @Transactional
    void removeMemberFromGroup(Long userId, Long roomId, Long targetUserId);

    void typeMessage(TypingEvent event);
}
