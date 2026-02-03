package org.acme.web.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.acme.web.dto.request.CreateMessageRequest;
import org.acme.web.dto.response.MessageResponse;
import org.acme.web.entity.Message;
import org.acme.web.entity.MessageStatus;
import org.acme.web.entity.MessageType;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.mapper.MessageMapper;
import org.acme.web.repository.MessageRepository;
import org.acme.web.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Message testMessage;
    private MessageResponse testMessageResponse;
    private CreateMessageRequest createRequest;

    @BeforeEach
    void setUp() {
        testMessage = Message.builder()
                .id(1L)
                .msgId("2025-02-02/NEWS/42")
                .subject("Weekly digest")
                .message("Summary of updates.")
                .createdAt(LocalDateTime.of(2025, 2, 2, 14, 30, 0))
                .messageType(MessageType.NEWS)
                .issue(42L)
                .status(MessageStatus.PUBLISHED)
                .effectiveStart(LocalDate.of(2025, 2, 2))
                .effectiveEnd(LocalDate.of(2025, 2, 9))
                .build();

        testMessageResponse = new MessageResponse();
        testMessageResponse.setId(1L);
        testMessageResponse.setMsgId("2025-02-02/NEWS/42");
        testMessageResponse.setTitle("Weekly digest");
        testMessageResponse.setMessage("Summary of updates.");
        testMessageResponse.setMessageType(MessageType.NEWS);
        testMessageResponse.setIssue(42L);
        testMessageResponse.setStatus(MessageStatus.PUBLISHED);

        createRequest = new CreateMessageRequest();
        createRequest.setTitle("Weekly digest");
        createRequest.setMessage("Summary of updates.");
        createRequest.setCreatedAt(LocalDateTime.of(2025, 2, 2, 14, 30, 0));
        createRequest.setMessageType(MessageType.NEWS);
        createRequest.setIssue(42L);
        createRequest.setStatus(MessageStatus.PUBLISHED);
        createRequest.setEffectiveStart(LocalDate.of(2025, 2, 2));
        createRequest.setEffectiveEnd(LocalDate.of(2025, 2, 9));
    }

    @Test
    void testUpload() {
        when(messageRepository.callInsertMessage(
                eq("Weekly digest"),
                eq("Summary of updates."),
                any(),
                eq("NEWS"),
                eq(42L),
                eq("PUBLISHED"),
                any(),
                any(),
                eq(null),
                eq(null)))
                .thenReturn(1L);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));
        when(messageMapper.toResponse(testMessage)).thenReturn(testMessageResponse);

        MessageResponse result = messageService.upload(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMsgId()).isEqualTo("2025-02-02/NEWS/42");
        assertThat(result.getTitle()).isEqualTo("Weekly digest");
        verify(messageRepository).callInsertMessage(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(messageRepository).findById(1L);
        verify(messageMapper).toResponse(testMessage);
    }

    @Test
    void testFindById() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));
        when(messageMapper.toResponse(testMessage)).thenReturn(testMessageResponse);

        MessageResponse result = messageService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Weekly digest");
        verify(messageRepository).findById(1L);
        verify(messageMapper).toResponse(testMessage);
    }

    @Test
    void testFindByIdNotFound() {
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Message")
                .hasMessageContaining("999");

        verify(messageRepository).findById(999L);
        verify(messageMapper, never()).toResponse(any());
    }

    @Test
    void testFindByMsgId() {
        when(messageRepository.findByMsgId("2025-02-02/NEWS/42")).thenReturn(Optional.of(testMessage));
        when(messageMapper.toResponse(testMessage)).thenReturn(testMessageResponse);

        MessageResponse result = messageService.findByMsgId("2025-02-02/NEWS/42");

        assertThat(result).isNotNull();
        assertThat(result.getMsgId()).isEqualTo("2025-02-02/NEWS/42");
        verify(messageRepository).findByMsgId("2025-02-02/NEWS/42");
        verify(messageMapper).toResponse(testMessage);
    }

    @Test
    void testFindByMsgIdNotFound() {
        when(messageRepository.findByMsgId("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.findByMsgId("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Message")
                .hasMessageContaining("unknown");

        verify(messageRepository).findByMsgId("unknown");
        verify(messageMapper, never()).toResponse(any());
    }

    @Test
    void testFindAll() {
        when(messageRepository.findAll()).thenReturn(List.of(testMessage));
        when(messageMapper.toResponse(testMessage)).thenReturn(testMessageResponse);

        List<MessageResponse> result = messageService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Weekly digest");
        verify(messageRepository).findAll();
        verify(messageMapper).toResponse(testMessage);
    }
}
