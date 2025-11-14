package com.nextstepsenegal.app.service.mapper;

import com.nextstepsenegal.app.domain.Eleve;
import com.nextstepsenegal.app.domain.User;
import com.nextstepsenegal.app.service.dto.EleveDTO;
import com.nextstepsenegal.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Eleve} and its DTO {@link EleveDTO}.
 */
@Mapper(componentModel = "spring")
public interface EleveMapper extends EntityMapper<EleveDTO, Eleve> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    EleveDTO toDto(Eleve s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
