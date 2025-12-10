package org.acme.web.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class AuthorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private Set<Long> bookIds;
    private Long createdById;
    private LocalDateTime createdAt;
    private Long updatedById;
    private LocalDateTime updatedAt;
}
