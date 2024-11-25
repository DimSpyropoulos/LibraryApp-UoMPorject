package gr.uom.it1566.Library.service;

import gr.uom.it1566.Library.entity.LoanEntity;
import gr.uom.it1566.Library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    // Δημιουργεί μια νέα δανειστική συναλλαγή με βάση το ISBN του βιβλίου και το ID του χρήστη
    public Mono<LoanEntity> createLoan(String bookIsbn, String bookTitle, Long userId) {
        return loanRepository.save(new LoanEntity().toBuilder()
                .bookIsbn(bookIsbn)
                .bookTitle(bookTitle)
                .userId(userId)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(30)) // Ορίζει την ημερομηνία επιστροφής 30 ημέρες μετά
                .ongoing(true) // Ορίζει ότι η δανειστική συναλλαγή είναι σε εξέλιξη
                .build());
    }

    public Flux<LoanEntity> getActiveLoansByUser(Long userId) {
        return loanRepository.findAllByUserIdAndOngoingTrue(userId);
    }

    // Επιστρέφει την ενεργή δανειστική συναλλαγή ενός χρήστη με βάση το ID του και το ISBN του βιβλίου
    public Mono<LoanEntity> getLoanByUserAndIsbn(Long userId, String bookIsbn) {
        return loanRepository.findActiveByUserIdAndBookIsbn(userId, bookIsbn);
    }

    // Ενημερώνει την κατάσταση της δανειστικής συναλλαγής για να την ολοκληρώσει
    public Mono<LoanEntity> updateLoan(LoanEntity loanEntity) {
        return loanRepository.stopLoan(loanEntity.getLoanId());
    }


}
