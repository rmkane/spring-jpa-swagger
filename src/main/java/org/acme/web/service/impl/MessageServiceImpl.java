package org.acme.web.service.impl;

import java.util.List;

import org.acme.web.dto.request.CreateMessageRequest;
import org.acme.web.dto.response.MessageResponse;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.mapper.MessageMapper;
import org.acme.web.repository.MessageRepository;
import org.acme.web.service.MessageService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    @SuppressWarnings("null")
    public MessageResponse upload(@NonNull CreateMessageRequest request) {
        log.info("Uploading message: {} (type={}, issue={})", request.getTitle(), request.getMessageType(),
                request.getIssue());
        Long id = messageRepository.callInsertMessage(
                request.getTitle(),
                request.getMessage(),
                request.getCreatedAt(),
                request.getMessageType().name(),
                request.getIssue(),
                request.getStatus().name(),
                request.getEffectiveStart(),
                request.getEffectiveEnd(),
                request.getCreatedBy(),
                1L);
        MessageResponse response = findById(id);
        log.info("Uploaded message id={}, msgId={}", response.getId(), response.getMsgId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public MessageResponse findById(@NonNull Long id) {
        log.debug("Finding message by id: {}", id);
        return messageRepository.findById(id)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Message", id));
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public MessageResponse findByMsgId(@NonNull String msgId) {
        log.debug("Finding message by msgId: {}", msgId);
        return messageRepository.findByMsgId(msgId)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Message", msgId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> findAll() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponse)
                .toList();
    }
}
