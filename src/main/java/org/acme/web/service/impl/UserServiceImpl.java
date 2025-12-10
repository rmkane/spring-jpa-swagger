package org.acme.web.service.impl;

import java.util.List;

import org.acme.web.dto.request.CreateUserRequest;
import org.acme.web.dto.request.UpdateUserRequest;
import org.acme.web.dto.response.UserResponse;
import org.acme.web.entity.User;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.mapper.UserMapper;
import org.acme.web.repository.UserRepository;
import org.acme.web.service.UserService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public UserResponse findById(@NonNull Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse create(@NonNull CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @SuppressWarnings("null")
    public UserResponse update(@NonNull Long id, @NonNull UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userMapper.updateEntity(request, user);
        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Override
    public void delete(@NonNull Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}
