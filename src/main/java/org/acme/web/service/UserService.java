package org.acme.web.service;

import java.util.List;

import org.acme.web.dto.request.CreateUserRequest;
import org.acme.web.dto.request.UpdateUserRequest;
import org.acme.web.dto.response.UserResponse;
import org.springframework.lang.NonNull;

public interface UserService {
    List<UserResponse> findAll();

    UserResponse findById(@NonNull Long id);

    UserResponse create(@NonNull CreateUserRequest request);

    UserResponse update(@NonNull Long id, @NonNull UpdateUserRequest request);

    void delete(@NonNull Long id);
}
