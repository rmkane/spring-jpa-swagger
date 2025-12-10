package org.acme.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Request to create a new user", example = """
        {
          "username": "johndoe",
          "email": "john.doe@example.com",
          "firstName": "John",
          "lastName": "Doe"
        }
        """)
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Schema(description = "Unique username", example = "johndoe")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;
}
