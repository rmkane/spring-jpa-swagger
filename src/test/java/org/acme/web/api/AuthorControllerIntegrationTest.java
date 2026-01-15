package org.acme.web.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.acme.web.dto.request.CreateAuthorRequest;
import org.acme.web.dto.request.UpdateAuthorRequest;
import org.acme.web.entity.Author;
import org.acme.web.entity.User;
import org.acme.web.repository.AuthorRepository;
import org.acme.web.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Author testAuthor;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        testUser = userRepository.save(testUser);

        testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .bio("Test author biography")
                .createdBy(testUser)
                .updatedBy(testUser)
                .build();
        testAuthor = authorRepository.save(testAuthor);
    }

    @Test
    void testGetAllAuthors() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")));
    }

    @Test
    void testGetAuthorById() throws Exception {
        mockMvc.perform(get("/api/authors/{id}", testAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testAuthor.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.bio", is("Test author biography")));
    }

    @Test
    void testGetAuthorByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/authors/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    void testCreateAuthor() throws Exception {
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setBio("New author biography");

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.bio", is("New author biography")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateAuthorValidationError() throws Exception {
        CreateAuthorRequest request = new CreateAuthorRequest();
        // Missing required firstName and lastName

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void testUpdateAuthor() throws Exception {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setBio("Updated biography");

        mockMvc.perform(put("/api/authors/{id}", testAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.bio", is("Updated biography")));
    }

    @Test
    void testUpdateAuthorNotFound() throws Exception {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setFirstName("Jane");

        mockMvc.perform(put("/api/authors/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/authors/{id}", testAuthor.getId()))
                .andExpect(status().isNoContent());

        // Verify author is deleted
        mockMvc.perform(get("/api/authors/{id}", testAuthor.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAuthorNotFound() throws Exception {
        mockMvc.perform(delete("/api/authors/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
