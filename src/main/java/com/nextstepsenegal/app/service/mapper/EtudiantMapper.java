package com.nextstepsenegal.app.service.mapper;

import com.nextstepsenegal.app.domain.Etudiant;
import com.nextstepsenegal.app.domain.User;
import com.nextstepsenegal.app.service.dto.EtudiantDTO;
import com.nextstepsenegal.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Etudiant} and its DTO {@link EtudiantDTO}.
 */
@Mapper(componentModel = "spring")
public interface EtudiantMapper extends EntityMapper<EtudiantDTO, Etudiant> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    EtudiantDTO toDto(Etudiant s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
