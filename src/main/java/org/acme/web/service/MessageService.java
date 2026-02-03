package org.acme.web.service;

import java.util.List;

import org.acme.web.dto.request.CreateMessageRequest;
import org.acme.web.dto.response.MessageResponse;
import org.springframework.lang.NonNull;

public interface MessageService {

    MessageResponse upload(@NonNull CreateMessageRequest request);

    MessageResponse findById(@NonNull Long id);

    MessageResponse findByMsgId(@NonNull String msgId);

    List<MessageResponse> findAll();
}
