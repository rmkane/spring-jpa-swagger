package org.acme.web.service;

import java.util.List;

import org.acme.web.dto.request.CreateAuthorRequest;
import org.acme.web.dto.request.UpdateAuthorRequest;
import org.acme.web.dto.response.AuthorResponse;
import org.springframework.lang.NonNull;

public interface AuthorService {
    List<AuthorResponse> findAll();

    AuthorResponse findById(@NonNull Long id);

    AuthorResponse create(@NonNull CreateAuthorRequest request);

    AuthorResponse update(@NonNull Long id, @NonNull UpdateAuthorRequest request);

    void delete(@NonNull Long id);
}
