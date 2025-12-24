package com.uniwork.repository;

import com.uniwork.model.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    List<ChatRoomMember> findByUserId(Long userId);

    @Query("""
            select m.roomId
                from ChatRoomMember m
                join ChatRoom r on r.id = m.roomId
                where r.type = 'DIRECT'
                  and m.userId in (:user1, :user2)
                group by m.roomId
                having count(m.userId) = 2
            """)
    Optional<Long> findDirectRoom(Long user1, Long user2);


}
