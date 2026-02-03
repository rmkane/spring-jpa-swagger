package org.acme.web.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.validation.Valid;

import org.acme.web.dto.request.CreateMessageRequest;
import org.acme.web.dto.response.MessageResponse;
import org.acme.web.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api/messages", produces = { MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE })
@Tag(name = "Messages", description = "Message management API (JSON or XML)")
@Slf4j
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    @Operation(summary = "Get all messages", description = "Retrieve a list of all messages")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        return ResponseEntity.ok(messageService.findAll());
    }

    // {*msgId} captures all path segments after /msg-id/ so both work:
    // - /api/messages/msg-id/2025-01-13/NOTICE/1 (literal slashes)
    // - /api/messages/msg-id/2025-01-13%2FNOTICE%2F1 (encoded, e.g. from Swagger)
    @GetMapping("/msg-id/{*msgId}")
    @Operation(summary = "Get message by MSG_ID", description = "Retrieve a message by its business key (e.g. 2025-02-02/NEWS/42). Slashes in msgId are allowed.")
    @ApiResponse(responseCode = "200", description = "Message found", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)),
            @Content(mediaType = MediaType.APPLICATION_XML_VALUE, schema = @Schema(implementation = MessageResponse.class))
    })
    @ApiResponse(responseCode = "404", description = "Message not found")
    public ResponseEntity<MessageResponse> getMessageByMsgId(@PathVariable("msgId") @NonNull String msgId) {
        String decodedId = decodeWildcardPathVariable(msgId);
        return ResponseEntity.ok(messageService.findByMsgId(decodedId));
    }

    private String decodeWildcardPathVariable(String pathVar) {
        return URLDecoder.decode(pathVar.replaceAll("^/+", ""), StandardCharsets.UTF_8);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get message by ID", description = "Retrieve a message by its numeric ID")
    @ApiResponse(responseCode = "200", description = "Message found", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)),
            @Content(mediaType = MediaType.APPLICATION_XML_VALUE, schema = @Schema(implementation = MessageResponse.class))
    })
    @ApiResponse(responseCode = "404", description = "Message not found")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable("id") @NonNull Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @Operation(summary = "Upload (upsert) message", description = "Create or update a message by uniqueness (date, issue, type). Accepts JSON or XML.")
    @ApiResponse(responseCode = "200", description = "Message created or updated", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)),
            @Content(mediaType = MediaType.APPLICATION_XML_VALUE, schema = @Schema(implementation = MessageResponse.class))
    })
    @ApiResponse(responseCode = "201", description = "Message created (when new row inserted)")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<MessageResponse> uploadMessage(@Valid @RequestBody @NonNull CreateMessageRequest request) {
        MessageResponse response = messageService.upload(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
