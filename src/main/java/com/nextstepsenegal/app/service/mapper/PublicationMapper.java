package com.nextstepsenegal.app.service.mapper;

import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.service.dto.PublicationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Publication} and its DTO {@link PublicationDTO}.
 */
@Mapper(componentModel = "spring")
public interface PublicationMapper extends EntityMapper<PublicationDTO, Publication> {}
