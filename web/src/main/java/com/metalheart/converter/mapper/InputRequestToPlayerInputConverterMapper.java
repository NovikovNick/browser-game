package com.metalheart.converter.mapper;

import com.metalheart.model.InputRequest;
import com.metalheart.model.PlayerInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InputRequestToPlayerInputConverterMapper {

    InputRequestToPlayerInputConverterMapper INSTANCE =
        Mappers.getMapper(InputRequestToPlayerInputConverterMapper.class);

    @Mapping(expression = "java(java.time.Instant.now())", target = "time")
    PlayerInput map(InputRequest task);
}
