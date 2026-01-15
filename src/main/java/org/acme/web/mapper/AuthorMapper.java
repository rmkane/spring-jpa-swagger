package org.acme.web.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.acme.web.dto.request.CreateAuthorRequest;
import org.acme.web.dto.request.UpdateAuthorRequest;
import org.acme.web.dto.response.AuthorResponse;
import org.acme.web.entity.Author;
import org.acme.web.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.lang.NonNull;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @NonNull
    Author toEntity(@NonNull CreateAuthorRequest request);

    @Mapping(target = "bookIds", source = "books", qualifiedByName = "booksToIds")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "updatedById", source = "updatedBy.id")
    @NonNull
    AuthorResponse toResponse(@NonNull Author author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@NonNull UpdateAuthorRequest request, @MappingTarget Author author);

    @Named("booksToIds")
    default Set<Long> booksToIds(Set<Book> books) {
        if (books == null) {
            return Set.of();
        }
        return books.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
    }
}
