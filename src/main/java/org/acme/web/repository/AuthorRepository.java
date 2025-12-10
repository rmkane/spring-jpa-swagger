package org.acme.web.repository;

import java.util.List;
import java.util.Optional;

import org.acme.web.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Override
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books LEFT JOIN FETCH a.createdBy LEFT JOIN FETCH a.updatedBy")
    @NonNull
    List<Author> findAll();

    @Override
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books LEFT JOIN FETCH a.createdBy LEFT JOIN FETCH a.updatedBy WHERE a.id = :id")
    @NonNull
    Optional<Author> findById(@NonNull @Param("id") Long id);
}
