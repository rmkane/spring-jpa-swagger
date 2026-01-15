package org.acme.web.service.impl;

import java.util.List;

import org.acme.web.dto.request.CreateAuthorRequest;
import org.acme.web.dto.request.UpdateAuthorRequest;
import org.acme.web.dto.response.AuthorResponse;
import org.acme.web.entity.Author;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.mapper.AuthorMapper;
import org.acme.web.repository.AuthorRepository;
import org.acme.web.service.AuthorService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public AuthorResponse findById(@NonNull Long id) {
        log.debug("Finding author by id: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        log.debug("Found author: {} {}", author.getFirstName(), author.getLastName());
        return authorMapper.toResponse(author);
    }

    @Override
    @SuppressWarnings("null")
    public AuthorResponse create(@NonNull CreateAuthorRequest request) {
        log.info("Creating author: {} {}", request.getFirstName(), request.getLastName());
        Author author = authorMapper.toEntity(request);
        Author saved = authorRepository.save(author);
        log.info("Created author with id: {}", saved.getId());
        return authorMapper.toResponse(saved);
    }

    @Override
    @SuppressWarnings("null")
    public AuthorResponse update(@NonNull Long id, @NonNull UpdateAuthorRequest request) {
        log.info("Updating author with id: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        authorMapper.updateEntity(request, author);
        Author updated = authorRepository.save(author);
        log.info("Updated author with id: {}", updated.getId());
        return authorMapper.toResponse(updated);
    }

    @Override
    public void delete(@NonNull Long id) {
        log.info("Deleting author with id: {}", id);
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author", id);
        }
        authorRepository.deleteById(id);
        log.info("Deleted author with id: {}", id);
    }
}
