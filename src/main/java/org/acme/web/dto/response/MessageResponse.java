package org.acme.web.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.acme.web.entity.MessageStatus;
import org.acme.web.entity.MessageType;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "message")
public class MessageResponse {

    private Long id;
    private String msgId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private MessageType messageType;
    private Long issue;
    private MessageStatus status;
    private LocalDate effectiveStart;
    private LocalDate effectiveEnd;
    private Long createdById;
    private Long updatedById;
    private LocalDateTime updatedAt;
}
