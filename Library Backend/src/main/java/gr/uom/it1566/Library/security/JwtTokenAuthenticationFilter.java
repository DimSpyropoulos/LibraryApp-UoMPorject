package gr.uom.it1566.Library.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// Δηλώνει την κλάση JwtTokenAuthenticationFilter που υλοποιεί το WebFilter
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements WebFilter {
    public static final String HEADER_PREFIX = "Bearer "; // Πρόθεμα για το header του JWT
    private final JwtTokenProvider tokenProvider; // Παροχέας JWT

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Λαμβάνει το token από το αίτημα
        String token = resolveToken(exchange.getRequest());

        // Ελέγχει αν το token είναι έγκυρο
        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
            return Mono.fromCallable(() -> this.tokenProvider.getAuthentication(token)) // Δημιουργεί Mono από το authentication
                    .subscribeOn(Schedulers.boundedElastic()) // Εκτελεί την εργασία σε boundedElastic scheduler
                    .flatMap(authentication -> {
                        // Συνχίζει τη διαδικασία του φίλτρου που αφορά το exchange
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)); // Ρυθμίζει το context ασφαλείας
                    });
        }
        // Εάν το token δεν είναι έγκυρο, συνεχίζει με την αλυσίδα φίλτρων
        return chain.filter(exchange);
    }

    // Μέθοδος για να εξάγει το token από το request
    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION); // Λαμβάνει το Authorization header
        // Ελέγχει αν το bearerToken έχει κείμενο και αν ξεκινάει με το HEADER_PREFIX
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(7); // Επιστρέφει το token χωρίς το πρόθεμα
        }
        return null; // Επιστρέφει null αν δεν βρεθεί έγκυρο token
    }
}
