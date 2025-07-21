package dev.akshit.usermicromart.dtos;

import dev.akshit.usermicromart.enums.SessionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenResponseDto {
    private UserDto userDto;
    private SessionStatus sessionStatus;
}
