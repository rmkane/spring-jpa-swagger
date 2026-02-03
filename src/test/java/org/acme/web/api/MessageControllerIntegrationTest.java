package org.acme.web.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.acme.web.dto.request.CreateMessageRequest;
import org.acme.web.dto.response.MessageResponse;
import org.acme.web.entity.MessageStatus;
import org.acme.web.entity.MessageType;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MessageController.class)
class MessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageService messageService;

    private MessageResponse sampleResponse() {
        MessageResponse r = new MessageResponse();
        r.setId(1L);
        r.setMsgId("2025-02-02/NEWS/42");
        r.setTitle("Weekly digest");
        r.setMessage("Summary of updates.");
        r.setCreatedAt(LocalDateTime.of(2025, 2, 2, 14, 30, 0));
        r.setMessageType(MessageType.NEWS);
        r.setIssue(42L);
        r.setStatus(MessageStatus.PUBLISHED);
        r.setEffectiveStart(LocalDate.of(2025, 2, 2));
        r.setEffectiveEnd(LocalDate.of(2025, 2, 9));
        return r;
    }

    private CreateMessageRequest sampleRequest() {
        CreateMessageRequest r = new CreateMessageRequest();
        r.setTitle("Weekly digest");
        r.setMessage("Summary of updates.");
        r.setCreatedAt(LocalDateTime.of(2025, 2, 2, 14, 30, 0));
        r.setMessageType(MessageType.NEWS);
        r.setIssue(42L);
        r.setStatus(MessageStatus.PUBLISHED);
        r.setEffectiveStart(LocalDate.of(2025, 2, 2));
        r.setEffectiveEnd(LocalDate.of(2025, 2, 9));
        return r;
    }

    @Test
    void testGetAllMessages() throws Exception {
        MessageResponse msg = sampleResponse();
        when(messageService.findAll()).thenReturn(List.of(msg));

        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Weekly digest")))
                .andExpect(jsonPath("$[0].msgId", is("2025-02-02/NEWS/42")));
    }

    @Test
    void testGetMessageById() throws Exception {
        MessageResponse msg = sampleResponse();
        when(messageService.findById(1L)).thenReturn(msg);

        mockMvc.perform(get("/api/messages/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Weekly digest")))
                .andExpect(jsonPath("$.msgId", is("2025-02-02/NEWS/42")));
    }

    @Test
    void testGetMessageByIdNotFound() throws Exception {
        when(messageService.findById(999L)).thenThrow(new ResourceNotFoundException("Message", 999L));

        mockMvc.perform(get("/api/messages/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void testGetMessageByMsgId() throws Exception {
        MessageResponse msg = sampleResponse();
        when(messageService.findByMsgId(anyString())).thenReturn(msg);

        mockMvc.perform(get("/api/messages/msg-id/2025-02-02%2FNEWS%2F42")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msgId", is("2025-02-02/NEWS/42")))
                .andExpect(jsonPath("$.title", is("Weekly digest")));
    }

    @Test
    void testGetMessageByMsgIdNotFound() throws Exception {
        when(messageService.findByMsgId("unknown")).thenThrow(new ResourceNotFoundException("Message", "unknown"));

        mockMvc.perform(get("/api/messages/msg-id/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUploadMessageJson() throws Exception {
        CreateMessageRequest request = sampleRequest();
        MessageResponse response = sampleResponse();
        when(messageService.upload(any(CreateMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Weekly digest")))
                .andExpect(jsonPath("$.msgId", is("2025-02-02/NEWS/42")));
    }

    @Test
    void testUploadMessageXml() throws Exception {
        MessageResponse response = sampleResponse();
        when(messageService.upload(any(CreateMessageRequest.class))).thenReturn(response);

        // Use a single-element XML for dates so Jackson can deserialize
        // LocalDateTime/LocalDate
        String xml = """
                <?xml version='1.0' encoding='UTF-8'?>
                <message>
                  <title>Weekly digest</title>
                  <message>Summary of updates.</message>
                  <createdAt>2025-02-02T14:30:00</createdAt>
                  <messageType>NEWS</messageType>
                  <issue>42</issue>
                  <status>PUBLISHED</status>
                  <effectiveStart>2025-02-02</effectiveStart>
                  <effectiveEnd>2025-02-09</effectiveEnd>
                </message>
                """;

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .content(xml))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test
    void testUploadMessageValidationError() throws Exception {
        CreateMessageRequest request = new CreateMessageRequest();
        // Missing required fields: title, message, createdAt, messageType, issue,
        // status, effectiveStart

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }
}
