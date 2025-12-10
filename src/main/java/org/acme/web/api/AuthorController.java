package org.acme.web.api;

import java.util.List;

import jakarta.validation.Valid;

import org.acme.web.dto.request.CreateAuthorRequest;
import org.acme.web.dto.request.UpdateAuthorRequest;
import org.acme.web.dto.response.AuthorResponse;
import org.acme.web.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/authors", produces = "application/json")
@Tag(name = "Authors", description = "Author management API")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @Operation(summary = "Get all authors", description = "Retrieve a list of all authors")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieve an author by their ID")
    @ApiResponse(responseCode = "200", description = "Author found")
    @ApiResponse(responseCode = "404", description = "Author not found")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable("id") @NonNull Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create a new author", description = "Create a new author")
    @ApiResponse(responseCode = "201", description = "Author created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody @NonNull CreateAuthorRequest request) {
        AuthorResponse response = authorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    @Operation(summary = "Update author", description = "Update an existing author")
    @ApiResponse(responseCode = "200", description = "Author updated successfully")
    @ApiResponse(responseCode = "404", description = "Author not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable("id") @NonNull Long id,
            @Valid @RequestBody @NonNull UpdateAuthorRequest request) {
        return ResponseEntity.ok(authorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete author", description = "Delete an author by ID")
    @ApiResponse(responseCode = "204", description = "Author deleted successfully")
    @ApiResponse(responseCode = "404", description = "Author not found")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("id") @NonNull Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
