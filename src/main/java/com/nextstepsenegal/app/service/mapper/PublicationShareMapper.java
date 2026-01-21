package com.nextstepsenegal.app.service.mapper;

import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.domain.PublicationShare;
import com.nextstepsenegal.app.service.dto.PublicationDTO;
import com.nextstepsenegal.app.service.dto.PublicationShareDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PublicationShare} and its DTO {@link PublicationShareDTO}.
 */
@Mapper(componentModel = "spring")
public interface PublicationShareMapper extends EntityMapper<PublicationShareDTO, PublicationShare> {
    @Mapping(target = "publication", source = "publication", qualifiedByName = "publicationId")
    PublicationShareDTO toDto(PublicationShare s);

    @Named("publicationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PublicationDTO toDtoPublicationId(Publication publication);
}
