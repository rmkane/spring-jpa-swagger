package org.acme.web.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Request to create a new book", example = """
        {
          "title": "The Great Novel",
          "isbn": "978-0-123456-78-9",
          "publicationYear": 2023,
          "authorIds": [1, 2]
        }
        """)
public class CreateBookRequest {
    @NotBlank(message = "Title is required")
    @Schema(description = "Book title", example = "The Great Novel")
    private String title;

    @Schema(description = "International Standard Book Number", example = "978-0-123456-78-9")
    private String isbn;

    @Schema(description = "Year the book was published", example = "2023")
    private Integer publicationYear;

    @Schema(description = "Set of author IDs associated with this book", example = "[1, 2]")
    private Set<Long> authorIds;
}
