package org.acme.web.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.acme.web.dto.request.CreateBookRequest;
import org.acme.web.dto.request.UpdateBookRequest;
import org.acme.web.dto.response.BookResponse;
import org.acme.web.entity.Author;
import org.acme.web.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.lang.NonNull;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @NonNull
    Book toEntity(@NonNull CreateBookRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "publicationYear", source = "publicationYear")
    @Mapping(target = "authorIds", source = "authors", qualifiedByName = "authorsToIds")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "updatedById", source = "updatedBy.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @NonNull
    BookResponse toResponse(@NonNull Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@NonNull UpdateBookRequest request, @MappingTarget Book book);

    @Named("authorsToIds")
    default Set<Long> authorsToIds(Set<Author> authors) {
        if (authors == null) {
            return Set.of();
        }
        return authors.stream()
                .map(Author::getId)
                .collect(Collectors.toSet());
    }
}
