package org.acme.web.mapper;

import org.acme.web.dto.response.MessageResponse;
import org.acme.web.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.NonNull;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "title", source = "subject")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "updatedById", source = "updatedBy.id")
    @NonNull
    MessageResponse toResponse(@NonNull Message message);
}
