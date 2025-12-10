package org.acme.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Request to update an existing author", example = """
        {
          "firstName": "Jane",
          "lastName": "Smith",
          "bio": "Updated biography"
        }
        """)
public class UpdateAuthorRequest {
    @Schema(description = "Author's first name", example = "Jane")
    private String firstName;

    @Schema(description = "Author's last name", example = "Smith")
    private String lastName;

    @Schema(description = "Author's biography", example = "Updated biography")
    private String bio;
}
