package com.uniwork.service;

import com.uniwork.model.dto.*;
import com.uniwork.model.entity.ChatRoom;
import com.uniwork.model.entity.ChatRoomMember;
import com.uniwork.model.entity.Message;
import com.uniwork.model.enumuration.NotificationEntityType;
import com.uniwork.model.enumuration.NotificationType;
import com.uniwork.repository.ChatRoomMemberRepository;
import com.uniwork.repository.ChatRoomRepository;
import com.uniwork.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ChatService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public void sendMessage(ChatMessageDTO dto) {
        boolean isMember = chatRoomMemberRepository.existsByRoomIdAndUserId(dto.getRoomId(), dto.getSenderId());

        if (!isMember) {
            throw new RuntimeException("User not in this room");
        }

        Message message = new Message();
        message.setRoomId(dto.getRoomId());
        message.setSenderId(dto.getSenderId());
        message.setContent(dto.getContent());
        message.setCreatedAt(LocalDateTime.now());

        messageRepository.save(message);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + dto.getRoomId(), message);
    }

    @Transactional
    public Long createDirectChat(Long user1, Long user2) {

        if (user1.equals(user2)) {
            throw new IllegalArgumentException("Cannot create direct chat with yourself");
        }

        return chatRoomMemberRepository.findDirectRoom(user1, user2).orElseGet(() -> {
                    ChatRoom room = new ChatRoom();
                    room.setType("DIRECT");
                    room.setCreatedAt(LocalDateTime.now());
                    chatRoomRepository.save(room);

                    chatRoomMemberRepository.saveAll(List.of(ChatRoomMember.builder()
                                    .roomId(room.getId())
                                    .userId(user1)
                                    .build(),
                            ChatRoomMember.builder()
                                    .roomId(room.getId())
                                    .userId(user2)
                                    .build()));
                    return room.getId();
                });
    }


    @Transactional
    public Long createGroupChat(CreateGroupDTO dto, Long creatorId) {

        if (dto.getMembers() == null || dto.getMembers().isEmpty()) {
            throw new IllegalArgumentException("Group must have members");
        }

        Set<Long> members = new HashSet<>(dto.getMembers());
        members.add(creatorId);

        if (members.size() < 2) {
            throw new IllegalArgumentException("Group must have at least 2 members");
        }

        ChatRoom room = new ChatRoom();
        room.setType("GROUP");
        room.setName(dto.getName());
        room.setCreatedAt(LocalDateTime.now());
        chatRoomRepository.save(room);

        List<ChatRoomMember> entities = members.stream()
                .map(userId -> ChatRoomMember.builder()
                        .roomId(room.getId())
                        .userId(userId)
                        .build())
                .toList();

        chatRoomMemberRepository.saveAll(entities);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .type(NotificationType.GROUP_ADDED)
                .entityType(NotificationEntityType.CHAT_ROOM)
                .entityId(room.getId())
                .createdAt(LocalDateTime.now())
                .build();

        members.forEach(userId -> {
            if (!userId.equals(creatorId)) {
                notificationService.sendNotification(userId, notificationDTO);
            }
        });

        return room.getId();
    }

    public List<ChatRoomDTO> getChatRoomsByUser(Long userId) {

        List<ChatRoom> rooms = chatRoomRepository.findAllByUserId(userId);

        return rooms.stream()
                .map(room -> ChatRoomDTO.builder()
                        .roomId(room.getId())
                        .type(room.getType())
                        .name(room.getName())
                        .createdAt(room.getCreatedAt())
                        .build())
                .toList();
    }

    public Page<ChatMessageResponseDTO> getChatHistoryByRoomId(
            Long roomId,
            Long userId,
            Pageable pageable
    ) {
        boolean isMember =
                chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, userId);

        if (!isMember) {
            throw new RuntimeException("User not in this room");
        }

        return messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .map(message -> ChatMessageResponseDTO.from(message));
    }


}
