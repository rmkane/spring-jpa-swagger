package org.acme.web.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.acme.web.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Calls the insert_message PostgreSQL function (upsert by created_date, issue,
     * message_type). Returns the id of the inserted or updated row.
     */
    @Query(value = """
            SELECT insert_message(
                :p_subject,
                :p_message,
                :p_created_at,
                CAST(:p_message_type AS message_type_enum),
                :p_issue,
                CAST(:p_status AS message_status_enum),
                :p_effective_start,
                :p_effective_end,
                :p_created_by,
                :p_updated_by
            )
            """, nativeQuery = true)
    Long callInsertMessage(
            @Param("p_subject") String pSubject,
            @Param("p_message") String pMessage,
            @Param("p_created_at") LocalDateTime pCreatedAt,
            @Param("p_message_type") String pMessageType,
            @Param("p_issue") Long pIssue,
            @Param("p_status") String pStatus,
            @Param("p_effective_start") LocalDate pEffectiveStart,
            @Param("p_effective_end") LocalDate pEffectiveEnd,
            @Param("p_created_by") Long pCreatedBy,
            @Param("p_updated_by") Long pUpdatedBy);

    @Override
    @Query("""
            SELECT m
            FROM Message m
            LEFT JOIN FETCH m.createdBy
            LEFT JOIN FETCH m.updatedBy
            """)
    @NonNull
    List<Message> findAll();

    @Override
    @Query("""
            SELECT m
            FROM Message m
            LEFT JOIN FETCH m.createdBy
            LEFT JOIN FETCH m.updatedBy
            WHERE m.id = :id
            """)
    @NonNull
    Optional<Message> findById(@NonNull @Param("id") Long id);

    @Query("""
            SELECT m
            FROM Message m
            LEFT JOIN FETCH m.createdBy
            LEFT JOIN FETCH m.updatedBy
            WHERE m.msgId = :msgId
            """)
    Optional<Message> findByMsgId(@NonNull @Param("msgId") String msgId);
}
