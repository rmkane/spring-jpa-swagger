package org.acme.web.repository;

import java.util.List;
import java.util.Optional;

import org.acme.web.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors LEFT JOIN FETCH b.createdBy LEFT JOIN FETCH b.updatedBy")
    @NonNull
    List<Book> findAll();

    @Override
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors LEFT JOIN FETCH b.createdBy LEFT JOIN FETCH b.updatedBy WHERE b.id = :id")
    @NonNull
    Optional<Book> findById(@NonNull @Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.isbn = :isbn")
    Optional<Book> findByIsbn(@NonNull @Param("isbn") String isbn);
}
