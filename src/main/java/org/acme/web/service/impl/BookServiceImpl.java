package org.acme.web.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acme.web.dto.request.CreateBookRequest;
import org.acme.web.dto.request.UpdateBookRequest;
import org.acme.web.dto.response.BookResponse;
import org.acme.web.entity.Author;
import org.acme.web.entity.Book;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.mapper.BookMapper;
import org.acme.web.repository.AuthorRepository;
import org.acme.web.repository.BookRepository;
import org.acme.web.service.BookService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public BookResponse findById(@NonNull Long id) {
        log.debug("Finding book by id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        log.debug("Found book: {}", book.getTitle());
        return bookMapper.toResponse(book);
    }

    @Override
    @SuppressWarnings("null")
    public BookResponse create(@NonNull CreateBookRequest request) {
        log.info("Creating book: {}", request.getTitle());
        Book book = bookMapper.toEntity(request);
        validateAndSetAuthors(book, request.getAuthorIds());
        Book saved = bookRepository.save(book);
        log.info("Created book with id: {} and {} authors", saved.getId(),
                saved.getAuthors().size());
        return bookMapper.toResponse(saved);
    }

    @Override
    @SuppressWarnings("null")
    public BookResponse update(@NonNull Long id, @NonNull UpdateBookRequest request) {
        log.info("Updating book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));

        bookMapper.updateEntity(request, book);
        validateAndSetAuthors(book, request.getAuthorIds());

        Book updated = bookRepository.save(book);
        log.info("Updated book with id: {}", updated.getId());
        return bookMapper.toResponse(updated);
    }

    @Override
    public void delete(@NonNull Long id) {
        log.info("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book", id);
        }
        bookRepository.deleteById(id);
        log.info("Deleted book with id: {}", id);
    }

    private void validateAndSetAuthors(Book book, Set<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return;
        }
        log.debug("Validating {} author IDs for book", authorIds.size());
        Set<Author> authors = new HashSet<>(authorRepository.findAllById(authorIds));
        if (authors.size() != authorIds.size()) {
            log.warn("Some author IDs were not found. Expected: {}, Found: {}", authorIds.size(),
                    authors.size());
            throw new ResourceNotFoundException("One or more authors not found");
        }
        book.setAuthors(authors);
    }
}
