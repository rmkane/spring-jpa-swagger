package org.acme.web.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private Set<Long> authorIds;
    private Long createdById;
    private LocalDateTime createdAt;
    private Long updatedById;
    private LocalDateTime updatedAt;
}
