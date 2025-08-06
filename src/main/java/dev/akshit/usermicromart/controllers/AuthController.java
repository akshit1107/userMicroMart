package dev.akshit.usermicromart.controllers;

import dev.akshit.usermicromart.dtos.*;
import dev.akshit.usermicromart.enums.SessionStatus;
import dev.akshit.usermicromart.exceptions.RoleDoNotExistsException;
import dev.akshit.usermicromart.exceptions.UserAlreadyExistsException;
import dev.akshit.usermicromart.exceptions.UserDoesNotExistException;
import dev.akshit.usermicromart.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<UserDto> logIn(@RequestBody LoginRequestDto request) throws UserDoesNotExistException {
        return authService.logIn(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(@RequestBody LogoutRequestDto request) {
        return authService.logOut(request.getToken(), request.getUserId());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) throws UserAlreadyExistsException, RoleDoNotExistsException {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword(), request.getUserRole());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateTokenResponseDto> validateToken(@RequestBody ValidateTokenRequestDto request) {
        Optional<UserDto> userDto= authService.validateToken(request.getToken(), request.getUserId());
        if(userDto.isEmpty()){
            ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
            responseDto.setSessionStatus(SessionStatus.INVALID);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }
        ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
        responseDto.setSessionStatus(SessionStatus.ACTIVE);
        responseDto.setUserDto(userDto.get());
        responseDto.setRoleId(new HashSet<>(userDto.get().getRoles().stream().map(role -> role.getId()).toList()));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
