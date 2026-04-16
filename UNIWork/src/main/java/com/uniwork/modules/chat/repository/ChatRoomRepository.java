package com.uniwork.modules.chat.repository;

import com.uniwork.modules.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        select r
        from ChatRoom r
        join ChatRoomMember m on m.roomId = r.id
        where m.userId = :userId
        order by r.createdAt desc
    """)
    List<ChatRoom> findAllByUserId(Long userId);
}
