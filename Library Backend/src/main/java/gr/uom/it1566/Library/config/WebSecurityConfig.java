package gr.uom.it1566.Library.config;

import gr.uom.it1566.Library.repository.UserRepository;
import gr.uom.it1566.Library.security.JwtTokenAuthenticationFilter;
import gr.uom.it1566.Library.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

// Δηλώνει ότι αυτή η κλάση είναι configuration κλάση που χρησιμοποιεί WebFlux Security
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final String [] publicRoutes = {"/users/register", "/users/login"}; // Δηλώνει τις δημόσιες διαδρομές

    // Ορίζει το βασικό φίλτρο ασφαλείας για τη διαχείριση HTTP αιτημάτων με JWT έλεγχο
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtTokenProvider tokenProvider,
                                                            ReactiveAuthenticationManager reactiveAuthenticationManager) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Απενεργοποιεί το CSRF για WebFlux
                .cors(corsSpec -> corsSpec.configurationSource(s -> {
                    CorsConfiguration cors = new CorsConfiguration();
                    cors.addAllowedOrigin("http://localhost:3000");
                    cors.addAllowedMethod(HttpMethod.GET);
                    cors.addAllowedMethod(HttpMethod.POST);
                    cors.addAllowedMethod(HttpMethod.PUT);
                    cors.addAllowedMethod(HttpMethod.DELETE);
                    cors.addAllowedHeader("*");
                    cors.setAllowCredentials(true);
                    return cors;
                }))
                .authenticationManager(reactiveAuthenticationManager) // Ορίζει τον authentication manager
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Δεν αποθηκεύει το context στον session
                .authorizeExchange(auth -> {
                    auth.pathMatchers(publicRoutes).permitAll() // Επιτρέπει την πρόσβαση στις δημόσιες διαδρομές
                            .anyExchange().authenticated(); // Απαιτεί authentication για τις υπόλοιπες διαδρομές
                })
                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC) // Προσθέτει το JWT φίλτρο
                .build();
    }

    // Ορίζει την υπηρεσία για αναζήτηση χρηστών μέσω email για έλεγχο ταυτότητας
    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository users) {
        return email -> users.findByEmail(email)
                .map(u -> User
                        .withUsername(u.getEmail()).password(u.getPassword()) // Δημιουργεί το User αντικείμενο
                        .build()
                );
    }

    // Δημιουργεί και ρυθμίζει τον Reactive Authentication Manager με τον κωδικοποιητή
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder); // Ορίζει τον κωδικοποιητή για τους κωδικούς
        return authenticationManager;
    }

    // Ορίζει τον κωδικοποιητή που χρησιμοποιείται για την αποθήκευση και επαλήθευση των κωδικών
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
