package dev.akshit.usermicromart.dtos;

import dev.akshit.usermicromart.enums.SessionStatus;
import dev.akshit.usermicromart.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ValidateTokenResponseDto {
    private UserDto userDto;
    private SessionStatus sessionStatus;
    private Set<Long> roleId;
}
