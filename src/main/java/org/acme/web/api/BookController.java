package org.acme.web.api;

import java.util.List;

import jakarta.validation.Valid;

import org.acme.web.dto.request.CreateBookRequest;
import org.acme.web.dto.request.UpdateBookRequest;
import org.acme.web.dto.response.BookResponse;
import org.acme.web.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/books", produces = "application/json")
@Tag(name = "Books", description = "Book management API")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve a list of all books")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a book by its ID")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("id") @NonNull Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create a new book", description = "Create a new book")
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody @NonNull CreateBookRequest request) {
        BookResponse response = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    @Operation(summary = "Update book", description = "Update an existing book")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable("id") @NonNull Long id,
            @Valid @RequestBody @NonNull UpdateBookRequest request) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Delete a book by ID")
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") @NonNull Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
