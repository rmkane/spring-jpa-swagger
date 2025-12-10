package org.acme.web.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Request to create a new author", example = """
        {
          "firstName": "Jane",
          "lastName": "Doe",
          "bio": "Award-winning author of contemporary fiction"
        }
        """)
public class CreateAuthorRequest {
    @NotBlank(message = "First name is required")
    @Schema(description = "Author's first name", example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Author's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Author's biography", example = "Award-winning author of contemporary fiction")
    private String bio;
}
