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
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        return bookMapper.toResponse(book);
    }

    @Override
    @SuppressWarnings("null")
    public BookResponse create(@NonNull CreateBookRequest request) {
        Book book = bookMapper.toEntity(request);

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            Set<Author> authors = new HashSet<>(authorRepository.findAllById(request.getAuthorIds()));
            if (authors.size() != request.getAuthorIds().size()) {
                throw new ResourceNotFoundException("One or more authors not found");
            }
            book.setAuthors(authors);
        }

        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Override
    @SuppressWarnings("null")
    public BookResponse update(@NonNull Long id, @NonNull UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));

        bookMapper.updateEntity(request, book);

        if (request.getAuthorIds() != null) {
            Set<Author> authors = new HashSet<>(authorRepository.findAllById(request.getAuthorIds()));
            if (authors.size() != request.getAuthorIds().size()) {
                throw new ResourceNotFoundException("One or more authors not found");
            }
            book.setAuthors(authors);
        }

        Book updated = bookRepository.save(book);
        return bookMapper.toResponse(updated);
    }

    @Override
    public void delete(@NonNull Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book", id);
        }
        bookRepository.deleteById(id);
    }
}
