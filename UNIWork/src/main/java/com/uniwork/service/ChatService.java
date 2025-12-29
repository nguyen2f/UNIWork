package com.uniwork.service;

import com.uniwork.model.dto.ChatMessageDTO;
import com.uniwork.model.dto.ChatMessageResponseDTO;
import com.uniwork.model.dto.ChatRoomDTO;
import com.uniwork.model.dto.CreateGroupDTO;
import com.uniwork.model.entity.ChatRoom;
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
}
