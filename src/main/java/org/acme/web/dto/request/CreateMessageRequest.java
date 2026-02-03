package org.acme.web.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.acme.web.entity.MessageStatus;
import org.acme.web.entity.MessageType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "message")
@Schema(name = "message", description = "Request to create or upsert a message", example = """
        {
          "title": "Weekly digest",
          "message": "Summary of updates.",
          "createdAt": "2025-02-02T14:30:00",
          "messageType": "NEWS",
          "issue": 42,
          "status": "PUBLISHED",
          "effectiveStart": "2025-02-02",
          "effectiveEnd": "2025-02-09",
          "createdBy": 1
        }
        """)
public class CreateMessageRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Message title (maps to subject)", example = "Weekly digest", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Message body is required")
    @Schema(description = "Message body", example = "Summary of updates.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @NotNull(message = "Created at is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Time the message was created", example = "2025-02-02 14:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @NotNull(message = "Message type is required")
    @Schema(description = "Message type", example = "NEWS", requiredMode = Schema.RequiredMode.REQUIRED)
    private MessageType messageType;

    @NotNull(message = "Issue is required")
    @Schema(description = "Ever-incrementing issue number (externally computed)", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long issue;

    @NotNull(message = "Status is required")
    @Schema(description = "Message status", example = "PUBLISHED", requiredMode = Schema.RequiredMode.REQUIRED)
    private MessageStatus status;

    @NotNull(message = "Effective start date is required")
    @Schema(description = "When the message becomes active", example = "2025-02-02", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate effectiveStart;

    @Schema(description = "When the message stops being active (nullable)", example = "2025-02-09")
    private LocalDate effectiveEnd;

    @Schema(description = "User ID for audit (optional)", example = "1")
    private Long createdBy;
}
