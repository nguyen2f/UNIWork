package com.uniwork.repository;

import com.uniwork.model.entity.User;
import com.uniwork.model.projection.ReportUserStatsProjection;
import com.uniwork.model.projection.UserProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByName(String username);

    User findByEmail(String email);

    User findByUserId(Long userId);

    @Query("SELECT COUNT(u) FROM User u")
    Long countAll();

    Long countByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    List<User> findAll();

    List<User> findAllByUserIdIn(List<Long> userIds);

    @Query("""
                SELECT 
                    COUNT(u.userId) AS teamMembers,
                    COUNT(u.userId) FILTER (WHERE u.createdDate BETWEEN :startOfMonth AND :now) AS newMembers
                FROM User u
            """)
    ReportUserStatsProjection getUserStats(@Param("startOfMonth") LocalDateTime startOfMonth,
                                           @Param("now") LocalDateTime now);

    @Query("""
                SELECT 
                    u.userId AS userId, 
                    u.name AS name, 
                    u.phone AS phone, 
                    u.email AS email, 
                    u.bio AS bio, 
                    u.address AS address, 
                    u.department AS department, 
                    u.active AS active,
                    u.systemRole AS systemRole 
                FROM User u
                WHERE u.userId = :userId
            """)
    UserProfileProjection getUserProfile(@Param("userId") Long userId);

    @Query(value = """
            SELECT u.name
            FROM chat_room_member cm
            JOIN users u ON cm.user_id = u.user_id
            WHERE cm.room_id = :roomId
              AND cm.user_id <> :currentUserId
            """, nativeQuery = true)
    String getUserNamesInChatRoom(@Param("roomId") Long roomId, @Param("currentUserId") Long currentUserId);

    @Query("select u.name from User u where u.userId = :userId")
    String findUserNameByUserId(@Param("userId") Long userId);

}
