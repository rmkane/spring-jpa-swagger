package org.acme.web.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.acme.web.dto.request.CreateAuthorRequest;
import org.acme.web.dto.request.UpdateAuthorRequest;
import org.acme.web.dto.response.AuthorResponse;
import org.acme.web.entity.Author;
import org.acme.web.entity.User;
import org.acme.web.exception.ResourceNotFoundException;
import org.acme.web.mapper.AuthorMapper;
import org.acme.web.repository.AuthorRepository;
import org.acme.web.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author testAuthor;
    private AuthorResponse testAuthorResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testAuthor = Author.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .bio("Test biography")
                .createdBy(testUser)
                .updatedBy(testUser)
                .build();

        testAuthorResponse = new AuthorResponse();
        testAuthorResponse.setId(1L);
        testAuthorResponse.setFirstName("John");
        testAuthorResponse.setLastName("Doe");
        testAuthorResponse.setBio("Test biography");
    }

    @Test
    void testFindAll() {
        when(authorRepository.findAll()).thenReturn(List.of(testAuthor));
        when(authorMapper.toResponse(testAuthor)).thenReturn(testAuthorResponse);

        List<AuthorResponse> result = authorService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        verify(authorRepository).findAll();
        verify(authorMapper).toResponse(testAuthor);
    }

    @Test
    void testFindById() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(authorMapper.toResponse(testAuthor)).thenReturn(testAuthorResponse);

        AuthorResponse result = authorService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(authorRepository).findById(1L);
        verify(authorMapper).toResponse(testAuthor);
    }

    @Test
    void testFindByIdNotFound() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author")
                .hasMessageContaining("999");

        verify(authorRepository).findById(999L);
        verify(authorMapper, never()).toResponse(any());
    }

    @Test
    void testCreate() {
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setBio("New author");

        when(authorMapper.toEntity(request)).thenReturn(testAuthor);
        when(authorRepository.save(testAuthor)).thenReturn(testAuthor);
        when(authorMapper.toResponse(testAuthor)).thenReturn(testAuthorResponse);

        AuthorResponse result = authorService.create(request);

        assertThat(result).isNotNull();
        verify(authorMapper).toEntity(request);
        verify(authorRepository).save(testAuthor);
        verify(authorMapper).toResponse(testAuthor);
    }

    @Test
    void testUpdate() {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(authorRepository.save(testAuthor)).thenReturn(testAuthor);
        when(authorMapper.toResponse(testAuthor)).thenReturn(testAuthorResponse);

        AuthorResponse result = authorService.update(1L, request);

        assertThat(result).isNotNull();
        verify(authorRepository).findById(1L);
        verify(authorMapper).updateEntity(request, testAuthor);
        verify(authorRepository).save(testAuthor);
        verify(authorMapper).toResponse(testAuthor);
    }

    @Test
    void testUpdateNotFound() {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.update(999L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(authorRepository).findById(999L);
        verify(authorRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        authorService.delete(1L);

        verify(authorRepository).existsById(1L);
        verify(authorRepository).deleteById(1L);
    }

    @Test
    void testDeleteNotFound() {
        when(authorRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> authorService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(authorRepository).existsById(999L);
        verify(authorRepository, never()).deleteById(any());
    }
}
