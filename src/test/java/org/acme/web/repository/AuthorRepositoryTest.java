package org.acme.web.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.acme.web.config.JpaAuditingConfig;
import org.acme.web.entity.Author;
import org.acme.web.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Author testAuthor;

    @BeforeEach
    void setUp() {
        // Create a test user for audit fields
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .createdAt(LocalDateTime.now())
                .build();
        testUser = entityManager.persistAndFlush(testUser);

        // Create a test author
        LocalDateTime now = LocalDateTime.now();
        testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .bio("Test author biography")
                .createdBy(testUser)
                .updatedBy(testUser)
                .createdAt(now)
                .build();
        testAuthor = entityManager.persistAndFlush(testAuthor);
    }

    @Test
    void testFindAll() {
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).isNotEmpty();
        assertThat(authors).anyMatch(a -> a.getFirstName().equals("John") && a.getLastName().equals("Doe"));
    }

    @Test
    void testFindById() {
        Optional<Author> found = authorRepository.findById(testAuthor.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
        assertThat(found.get().getBio()).isEqualTo("Test author biography");
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Author> found = authorRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testSave() {
        LocalDateTime now = LocalDateTime.now();
        Author newAuthor = Author.builder()
                .firstName("Jane")
                .lastName("Smith")
                .bio("New author")
                .createdBy(testUser)
                .updatedBy(testUser)
                .createdAt(now)
                .build();

        Author saved = authorRepository.save(newAuthor);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Jane");
        assertThat(saved.getLastName()).isEqualTo("Smith");
    }

    @Test
    void testDelete() {
        Long id = testAuthor.getId();
        authorRepository.deleteById(id);
        entityManager.flush();

        Optional<Author> deleted = authorRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindAllWithEagerLoading() {
        List<Author> authors = authorRepository.findAll();
        // Verify that books collection is eagerly loaded (no
        // LazyInitializationException)
        assertThat(authors).isNotEmpty();
        authors.forEach(author -> {
            // Accessing books should not throw LazyInitializationException
            assertThat(author.getBooks()).isNotNull();
        });
    }

    @Test
    void testFindByIdWithEagerLoading() {
        Optional<Author> found = authorRepository.findById(testAuthor.getId());
        assertThat(found).isPresent();
        Author author = found.get();
        // Verify audit fields are eagerly loaded
        assertThat(author.getCreatedBy()).isNotNull();
        assertThat(author.getUpdatedBy()).isNotNull();
        // Verify books collection is eagerly loaded
        assertThat(author.getBooks()).isNotNull();
    }
}
