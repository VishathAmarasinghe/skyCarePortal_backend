package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Message;
import com.skycareSolutions.skyCarePortal.model.MessageSeen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageSeenRepository extends JpaRepository<MessageSeen,Long> {
    List<MessageSeen> findByMessage(Message message);

    @Query("SELECT ms FROM MessageSeen ms WHERE ms.userId = :userId AND ms.readStatus = false")
    List<MessageSeen> findUnseenMessagesByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE MessageSeen ms SET ms.readStatus = true, ms.seenAt = CURRENT_TIMESTAMP WHERE ms.chat.id = :chatId AND ms.userId = :userId AND ms.readStatus = false")
    void markMessagesAsSeen(@Param("chatId") Long chatId, @Param("userId") String userId);




}
