package org.acme.web.dto.request;

import jakarta.validation.constraints.Email;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Request to update an existing user", example = """
        {
          "email": "john.smith@example.com",
          "firstName": "John",
          "lastName": "Smith"
        }
        """)
public class UpdateUserRequest {
    @Email(message = "Email must be valid")
    @Schema(description = "User's email address", example = "john.smith@example.com")
    private String email;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Smith")
    private String lastName;
}
