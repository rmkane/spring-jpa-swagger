package org.acme.web.service;

import java.util.List;

import org.acme.web.dto.request.CreateBookRequest;
import org.acme.web.dto.request.UpdateBookRequest;
import org.acme.web.dto.response.BookResponse;
import org.springframework.lang.NonNull;

public interface BookService {
    List<BookResponse> findAll();

    BookResponse findById(@NonNull Long id);

    BookResponse create(@NonNull CreateBookRequest request);

    BookResponse update(@NonNull Long id, @NonNull UpdateBookRequest request);

    void delete(@NonNull Long id);
}
