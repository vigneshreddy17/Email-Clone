package com.projects.emailclone.Repository;

import com.projects.emailclone.Model.MessageModel;
import com.projects.emailclone.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepo extends JpaRepository<MessageModel, Long> {
    @Query("SELECT m.sender, m.message FROM MessageModel m WHERE m.sender != :sender")
    Optional<List<Object[]>> findBySender(@Param("sender") String sender);

    @Query("SELECT m.sender, m.message from MessageModel m where m.recipient = :recipient AND m.is_read = false")
    Optional<List<Object[]>> getUnreadMessages(@Param("recipient") String recipient);

    @Modifying
    @Query("UPDATE MessageModel m SET m.is_read = true WHERE m.recipient = :recipient")
    void markAsRead(@Param("recipient") String recipient);
}
