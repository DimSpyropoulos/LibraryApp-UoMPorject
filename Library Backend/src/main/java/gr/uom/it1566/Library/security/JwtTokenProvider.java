package gr.uom.it1566.Library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "roles"; // Κλειδί για τις αρχές του χρήστη
    private final JwtProperties jwtProperties; // Χρήσιμες ρυθμίσεις JWT
    private SecretKey secretKey; // Κλειδί υπογραφής για JWT

    @PostConstruct
    public void init() {
        // Δημιουργία κλειδιού HMAC από τη μυστική τιμή
        var secret = Base64.getEncoder()
                .encodeToString(this.jwtProperties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication) {
        // Λαμβάνει το όνομα χρήστη και τις αρχές του χρήστη
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Δημιουργία των claims του JWT
        Claims claims = Jwts.claims().setSubject(username);
        if (!authorities.isEmpty()) {
            claims.put(AUTHORITIES_KEY, authorities.stream()
                    .map(GrantedAuthority::getAuthority).collect(joining(","))); // Προσθήκη αρχών στο JWT
        }

        // Ορισμός ημερομηνίας δημιουργίας και λήξης του token
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.jwtProperties.getValidityInMs());

        // Δημιουργία και επιστροφή του JWT
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
                .signWith(this.secretKey, SignatureAlgorithm.HS256).compact();
    }

    public Authentication getAuthentication(String token) {
        // Ανάκτηση των claims από το token
        Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build()
                .parseClaimsJws(token).getBody();

        // Λαμβάνει τις αρχές του χρήστη από τα claims
        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);
        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        // Δημιουργία του principal με το όνομα χρήστη και τις αρχές του
        User principal = new User(claims.getSubject(), "", authorities);

        // Επιστροφή ενός αντικειμένου Authentication
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            // Επικύρωση του JWT και ανάκτηση των claims
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(this.secretKey)
                    .build().parseClaimsJws(token);
            log.info("expiration date: {}", claims.getBody().getExpiration()); // Καταγραφή ημερομηνίας λήξης
            return true; // Επικύρωση επιτυχής
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token: {}", e.getMessage()); // Καταγραφή σφάλματος
            log.trace("Invalid JWT token trace.", e); // Καταγραφή λεπτομερειών σφάλματος
        }
        return false; // Επικύρωση αποτυχημένη
    }
}
