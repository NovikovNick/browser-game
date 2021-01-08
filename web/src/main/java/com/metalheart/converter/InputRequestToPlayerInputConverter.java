package com.metalheart.converter;

import com.metalheart.converter.mapper.InputRequestToPlayerInputConverterMapper;
import com.metalheart.model.InputRequest;
import com.metalheart.model.PlayerInput;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InputRequestToPlayerInputConverter implements Converter<InputRequest, PlayerInput> {

    @Override
    public PlayerInput convert(InputRequest source) {
        return InputRequestToPlayerInputConverterMapper.INSTANCE.map(source);
    }
}
