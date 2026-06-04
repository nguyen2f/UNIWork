package com.uniwork.modules.chat.repository;

import com.uniwork.modules.chat.entity.Message;
import com.uniwork.modules.chat.projection.MessageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    @Query("SELECT m, u.name FROM Message m LEFT JOIN User u ON u.userId = m.senderId WHERE m.roomId = :roomId ORDER BY m.createdAt DESC")
    Page<Message> findByRoomIdOrderByCreatedAtDesc(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT m.id AS id, m.roomId AS roomId, m.senderId AS senderId ,m.content AS content, m.createdAt AS createdAt, u.name AS senderName " +
            "FROM Message m LEFT JOIN User u ON u.userId = m.senderId " +
            "WHERE m.roomId = :roomId ORDER BY m.createdAt DESC")
    Page<MessageProjection> findMessagesWithSenderName(@Param("roomId") Long roomId, Pageable pageable);

    @Query("""
            SELECT m.id AS id, m.roomId AS roomId, m.senderId AS senderId, m.content AS content, m.createdAt AS createdAt, u.name AS senderName
            FROM Message m LEFT JOIN User u ON u.userId = m.senderId
            WHERE m.roomId = :roomId
            ORDER BY m.createdAt DESC
            LIMIT 1
            """)
    MessageProjection findLastMessageByRoomId(@Param("roomId") Long roomId);

}
