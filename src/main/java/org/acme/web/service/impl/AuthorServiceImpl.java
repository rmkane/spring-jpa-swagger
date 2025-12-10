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
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        return authorMapper.toResponse(author);
    }

    @Override
    @SuppressWarnings("null")
    public AuthorResponse create(@NonNull CreateAuthorRequest request) {
        Author author = authorMapper.toEntity(request);
        Author saved = authorRepository.save(author);
        return authorMapper.toResponse(saved);
    }

    @Override
    @SuppressWarnings("null")
    public AuthorResponse update(@NonNull Long id, @NonNull UpdateAuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        authorMapper.updateEntity(request, author);
        Author updated = authorRepository.save(author);
        return authorMapper.toResponse(updated);
    }

    @Override
    public void delete(@NonNull Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author", id);
        }
        authorRepository.deleteById(id);
    }
}
