package com.uniwork.repository;

import com.uniwork.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    Page<Message> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);


}
