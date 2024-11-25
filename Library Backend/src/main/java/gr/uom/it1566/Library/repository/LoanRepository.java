package gr.uom.it1566.Library.repository;

import gr.uom.it1566.Library.entity.LoanEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Δηλώνει το LoanRepository που επεκτείνει το R2dbcRepository για την οντότητα LoanEntity
public interface LoanRepository extends R2dbcRepository<LoanEntity, String> {

    // Μέθοδος για εύρεση όλων των δανείων για έναν χρήστη με βάση το userId
   Flux<LoanEntity> findAllByUserIdAndOngoingTrue(Long userId);

    // Ερώτηση SQL για εύρεση ενεργού δανείου με βάση το userId και το bookIsbn
    @Query("SELECT * FROM loans WHERE user_id = :userId AND book_isbn =:bookIsbn AND ongoing = true")
    Mono<LoanEntity> findActiveByUserIdAndBookIsbn(Long userId, String bookIsbn);

    // Ερώτηση SQL για να σταματήσει ένα δάνειο με βάση το loanId
    @Query("UPDATE loans SET ongoing = false WHERE loan_id = :loanId")
    Mono<LoanEntity> stopLoan(Integer loanId);
}


