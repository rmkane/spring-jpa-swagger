package org.acme.web.dto.request;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Request to update an existing book", example = """
        {
          "title": "Updated Book Title",
          "isbn": "978-0-987654-32-1",
          "publicationYear": 2024,
          "authorIds": [1]
        }
        """)
public class UpdateBookRequest {
    @Schema(description = "Book title", example = "Updated Book Title")
    private String title;

    @Schema(description = "International Standard Book Number", example = "978-0-987654-32-1")
    private String isbn;

    @Schema(description = "Year the book was published", example = "2024")
    private Integer publicationYear;

    @Schema(description = "Set of author IDs associated with this book", example = "[1]")
    private Set<Long> authorIds;
}
