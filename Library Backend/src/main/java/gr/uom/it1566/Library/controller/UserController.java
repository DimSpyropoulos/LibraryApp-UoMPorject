package gr.uom.it1566.Library.controller;

import gr.uom.it1566.Library.dto.AuthenticationRequest;
import gr.uom.it1566.Library.entity.UserEntity;
import gr.uom.it1566.Library.security.JwtTokenProvider;
import gr.uom.it1566.Library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder; // Κωδικοποιητής κωδικών
    private final UserService userService; // Χρησιμοποιείται για λειτουργίες σχετικές με τον χρήστη

    private final JwtTokenProvider tokenProvider; // Παρέχει λειτουργικότητα δημιουργίας JWT
    private final ReactiveAuthenticationManager authenticationManager; // Χρησιμοποιείται για authentication

    // Endpoint για την εγγραφή νέου χρήστη
    @PostMapping("/register")
    public Mono<UserEntity> register(@RequestBody UserEntity user) {
        return userService.register(user
                .toBuilder()
                .password(
                        passwordEncoder.encode(user.getPassword()) // Κωδικοποιεί τον κωδικό του χρήστη
                )
                .build());
    }


    // Endpoint για την αυθεντικοποίηση χρήστη και δημιουργία JWT token
    @PostMapping("/login")
    public Mono<ResponseEntity> login(
            @RequestBody Mono<AuthenticationRequest> authRequest) {

        return authRequest
                .flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                login.getEmail(), login.getPassword())) // Δημιουργεί token για τον χρήστη
                        .map(this.tokenProvider::createToken))
                .map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt); // Προσθέτει το token στα headers
                    var tokenBody = Map.of("access_token", jwt);
                    return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK); // Επιστρέφει την απόκριση με το token
                });
    }
}
