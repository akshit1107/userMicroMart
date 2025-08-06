package dev.akshit.usermicromart.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignUpRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<String> userRole;

}
