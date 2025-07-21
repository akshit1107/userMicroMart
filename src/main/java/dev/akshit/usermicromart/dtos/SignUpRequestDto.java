package dev.akshit.usermicromart.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
