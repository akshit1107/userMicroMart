package dev.akshit.usermicromart.services;

import dev.akshit.usermicromart.dtos.UserDto;
import dev.akshit.usermicromart.enums.SessionStatus;
import dev.akshit.usermicromart.exceptions.RoleDoNotExistsException;
import dev.akshit.usermicromart.exceptions.UserAlreadyExistsException;
import dev.akshit.usermicromart.exceptions.UserDoesNotExistException;
import dev.akshit.usermicromart.models.Role;
import dev.akshit.usermicromart.models.Session;
import dev.akshit.usermicromart.models.User;
import dev.akshit.usermicromart.repositories.RoleRepository;
import dev.akshit.usermicromart.repositories.SessionRepository;
import dev.akshit.usermicromart.repositories.UserRepository;
//import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import java.util.*;

@Service
public class AuthService {

    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RoleRepository roleRepository;

    private JWTService jwtService;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, JWTService jwtService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<UserDto> logIn(String email, String password) throws UserDoesNotExistException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserDoesNotExistException("User with email " + email + " does not exist.");
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Create a token ()
//        String token = RandomStringUtils.randomAscii(20);
        Map token = jwtService.generateToken(user.getEmail());
        Long expiringAt = Long.parseLong(token.get("expiringAt").toString());
        String tokenString = token.get("token").toString();
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("AUTH_TOKEN", tokenString);

        Session session = new Session();
        session.setUser(user);
        session.setToken(tokenString);
        session.setExpiringAt(new Date(expiringAt));
        session.setStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);
        ResponseEntity<UserDto> response = new ResponseEntity<>(
            userDto,
            headers,
            HttpStatus.OK
        );

        return response;
    }

    public ResponseEntity<Void> logOut(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        Session session = sessionOptional.get();
        session.setStatus(SessionStatus.LOGGED_OUT);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }

    public UserDto signUp(String email, String password, Set<String> userRole) throws UserAlreadyExistsException, RoleDoNotExistsException {

        Optional<User> userOptinal = userRepository.findByEmail(email);
        if(!userOptinal.isEmpty()){
            throw new UserAlreadyExistsException("User with email " + email + " already exists.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(password));

        Set<Role> roles = roleRepository.findByNameIn(userRole);

        if(roles.isEmpty()){
            throw new RoleDoNotExistsException("Role(s) does not exist.");
        }

//        roles.stream().

        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Assign the role to the user


        UserDto userDto = UserDto.from(savedUser);
        userDto.setRoles(roles);
        return userDto;
    }

    public Optional<UserDto> validateToken(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }
        Session session = sessionOptional.get();
        if (!session.getStatus().equals(SessionStatus.ACTIVE)) {
            return Optional.empty();
        }

        User user = userRepository.findById(userId).get();

        UserDto userDto = UserDto.from(user);

        if(session.getExpiringAt().compareTo(new Date()) <= 0){
            session.setStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
            return Optional.empty();
        }
        return Optional.of(userDto);
    }

}
