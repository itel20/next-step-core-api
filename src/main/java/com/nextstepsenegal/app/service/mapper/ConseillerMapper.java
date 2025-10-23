package com.nextstepsenegal.app.service.mapper;

import com.nextstepsenegal.app.domain.Conseiller;
import com.nextstepsenegal.app.domain.User;
import com.nextstepsenegal.app.service.dto.ConseillerDTO;
import com.nextstepsenegal.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Conseiller} and its DTO {@link ConseillerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConseillerMapper extends EntityMapper<ConseillerDTO, Conseiller> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    ConseillerDTO toDto(Conseiller s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
