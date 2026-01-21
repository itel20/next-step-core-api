package com.nextstepsenegal.app.service.mapper;

import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.domain.PublicationLike;
import com.nextstepsenegal.app.service.dto.PublicationDTO;
import com.nextstepsenegal.app.service.dto.PublicationLikeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PublicationLike} and its DTO {@link PublicationLikeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PublicationLikeMapper extends EntityMapper<PublicationLikeDTO, PublicationLike> {
    @Mapping(target = "publication", source = "publication", qualifiedByName = "publicationId")
    PublicationLikeDTO toDto(PublicationLike s);

    @Named("publicationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PublicationDTO toDtoPublicationId(Publication publication);
}
