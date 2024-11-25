package gr.uom.it1566.Library.controller;

import gr.uom.it1566.Library.entity.LoanEntity;
import gr.uom.it1566.Library.service.LoanService;
import gr.uom.it1566.Library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



// Δηλώνει ότι αυτή η κλάση είναι Rest Controller για αιτήματα HTTP που αφορούν το "/loan"
@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor // Παράγει constructor που αρχικοποιεί όλα τα final πεδία
public class LoanController {

    private final UserService userService;
    private final LoanService loanService;


    @GetMapping("/active")
    public Flux<LoanEntity> getActiveLoans(@AuthenticationPrincipal Mono<UserDetails> principal) {
        // Ανάκτηση του συνδεδεμένου χρήστη και επιστροφή των ενεργών δανεισμών
        return principal.flatMapMany(user ->
                userService.getUserId(user.getUsername()) // Εύρεση ID χρήστη με βάση το όνομα
                        .flatMapMany(userId ->
                                loanService.getActiveLoansByUser(userId.getId())) // Κλήση για την εύρεση ενεργών δανεισμών
                        .switchIfEmpty(Flux.empty()) // Σφάλμα αν δεν βρεθεί χρήστης
        ).switchIfEmpty(Flux.empty());
    }

    // Endpoint για δανεισμό βιβλίου
    @PostMapping("/borrow")
    public Mono<LoanEntity> borrow(@AuthenticationPrincipal Mono<UserDetails> principal,
                                   @RequestParam String bookTitle) {

        // Ανάκτηση του συνδεδεμένου χρήστη και απόπειρα δανεισμού βιβλίου
        return principal.flatMap(user ->
                userService.getUserId(user.getUsername()) // Εύρεση ID χρήστη με βάση το όνομα
                        .flatMap(userEntity ->
                                userService.borrowABook(userEntity, bookTitle)) // Κλήση δανεισμού βιβλίου
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found"))) // Σφάλμα αν δεν βρεθεί χρήστης
        ).switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    // Endpoint για επιστροφή βιβλίου
    @PostMapping("/return-book")
    public Mono<LoanEntity> bookReturn(@AuthenticationPrincipal Mono<UserDetails> principal,
                                 @RequestParam String bookTitle) {

        // Ανάκτηση του συνδεδεμένου χρήστη και απόπειρα επιστροφής βιβλίου
        return principal.flatMap(user ->
                userService.getUserId(user.getUsername()) // Εύρεση ID χρήστη
                        .flatMap(userEntity ->
                                userService.returnABook(userEntity, bookTitle) // Κλήση επιστροφής βιβλίου
                        )
        );
    }
}
