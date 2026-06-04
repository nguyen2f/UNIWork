package com.uniwork.modules.chat.service;

import com.uniwork.modules.chat.dto.ChatMessageDTO;
import com.uniwork.modules.chat.dto.ChatMessageResponseDTO;
import com.uniwork.modules.chat.dto.ChatRoomDTO;
import com.uniwork.modules.chat.dto.ChatRoomRenameEvent;
import com.uniwork.modules.chat.dto.CreateGroupDTO;
import com.uniwork.modules.chat.entity.ChatRoom;
import com.uniwork.modules.chat.entity.ChatRoomMember;
import com.uniwork.modules.chat.entity.Message;
import com.uniwork.modules.chat.projection.MessageProjection;
import com.uniwork.enums.ChatRoomType;
import com.uniwork.enums.NotificationEntityType;
import com.uniwork.enums.NotificationType;
import com.uniwork.modules.chat.repository.ChatRoomMemberRepository;
import com.uniwork.modules.chat.repository.ChatRoomRepository;
import com.uniwork.modules.chat.repository.MessageRepository;
import com.uniwork.modules.user.repository.UserRepository;
import com.uniwork.modules.notification.dto.NotificationDTO;
import com.uniwork.modules.notification.service.NotificationService;
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
public class ChatServiceImpl implements ChatService {

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
    @Autowired
    private UserRepository userRepository;

    public void sendMessage(ChatMessageDTO dto) {
        Long senderId = dto.getSenderId();
        Long roomId = dto.getRoomId();
        boolean isMember = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, senderId);

        if (!isMember) {
            throw new RuntimeException("User not in this room");
        }

        Message message = new Message();
        message.setRoomId(dto.getRoomId());
        message.setSenderId(dto.getSenderId());
        message.setContent(dto.getContent());
        message.setCreatedAt(LocalDateTime.now());
        messageRepository.save(message);

        String senderName = userRepository.findUserNameByUserId(senderId);

        ChatMessageResponseDTO response =
                ChatMessageResponseDTO.builder()
                        .messageId(message.getId())
                        .senderId(senderId)
                        .senderName(senderName)
                        .content(message.getContent())
                        .createdAt(message.getCreatedAt())
                        .build();

        simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId, response);

        // Send notification to other members
        List<ChatRoomMember> members = chatRoomMemberRepository.findByRoomId(roomId);
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .entityType(NotificationEntityType.CHAT_ROOM)
                .type(NotificationType.MESSAGE)
                .entityId(roomId)
                .title(senderName + " sent a message")
                .createdAt(LocalDateTime.now())
                .build();

        members.forEach(member -> {
            if (!member.getUserId().equals(senderId)) {
                notificationService.sendNotification(member.getUserId(), notificationDTO);
            }
        });
    }

    @Transactional
    public Long createDirectChat(Long user1, Long user2) {

        if (user1.equals(user2)) {
            throw new IllegalArgumentException("Cannot create direct chat with yourself");
        }

        return chatRoomMemberRepository.findDirectRoom(user1, user2).orElseGet(() -> {
            ChatRoom room = new ChatRoom();
            room.setType(ChatRoomType.DIRECT);
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
        room.setType(ChatRoomType.GROUP);
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
                .title("You have been added to group: " + dto.getName())
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
                .map(room -> {
                    ChatRoomDTO.ChatRoomDTOBuilder builder = ChatRoomDTO.builder()
                            .roomId(room.getId())
                            .type(room.getType())
                            .createdAt(room.getCreatedAt());

                    if (room.getType() == ChatRoomType.GROUP) {
                        builder.name(room.getName());
                    }
                    if (room.getType() == ChatRoomType.DIRECT) {
                        String otherName = userRepository.getUserNamesInChatRoom(room.getId(), userId);
                        builder.name(otherName);
                    }

                    // Fetch last message preview
                    MessageProjection lastMsg = messageRepository.findLastMessageByRoomId(room.getId());
                    if (lastMsg != null) {
                        builder.lastMessage(lastMsg.getContent());
                        builder.lastMessageTime(lastMsg.getCreatedAt());
                        builder.lastMessageSenderId(lastMsg.getSenderId());
                        builder.lastMessageSenderName(lastMsg.getSenderName());
                    }

                    return builder.build();
                })
                .toList();

    }

    public Page<ChatMessageResponseDTO> getChatHistoryByRoomId(Long roomId, Long userId, Pageable pageable) {
        boolean isMember = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, userId);

        if (!isMember) {
            throw new RuntimeException("User not in this room");
        }

        return messageRepository.findMessagesWithSenderName(roomId, pageable).map(ChatMessageResponseDTO::from);
    }

    @Transactional
    public ChatRoom renameGroupChat(Long userId, Long roomId, String newName) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        room.setName(newName);
        chatRoomRepository.save(room);

        ChatRoomRenameEvent event = ChatRoomRenameEvent.builder()
                .roomId(roomId)
                .newName(newName)
                .updatedBy(userId)
                .updatedAt(LocalDateTime.now())
                .build();

        simpMessagingTemplate.convertAndSend("/topic/chat-room/" + roomId + "/rename", event);

        return room;
    }

    @Transactional
    public void addMemberToGroup(Long userId, Long roomId, Long newMemberId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (room.getType() != ChatRoomType.GROUP) {
            throw new RuntimeException("Cannot add members to a direct chat");
        }

        if (chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, newMemberId)) {
            throw new RuntimeException("User is already a member of this room");
        }

        ChatRoomMember member = ChatRoomMember.builder()
                .roomId(roomId)
                .userId(newMemberId)
                .build();
        chatRoomMemberRepository.save(member);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .type(NotificationType.GROUP_ADDED)
                .entityType(NotificationEntityType.CHAT_ROOM)
                .entityId(roomId)
                .title("You have been added to group: " + room.getName())
                .createdAt(LocalDateTime.now())
                .build();
        notificationService.sendNotification(newMemberId, notificationDTO);
    }

    @Transactional
    public void removeMemberFromGroup(Long userId, Long roomId, Long targetUserId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (room.getType() != ChatRoomType.GROUP) {
            throw new RuntimeException("Cannot remove members from a direct chat");
        }

        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndUserId(roomId, targetUserId);
        if (member == null) {
            throw new RuntimeException("User is not a member of this room");
        }

        member.setIsDeleted(true);
        chatRoomMemberRepository.save(member);
    }


}
