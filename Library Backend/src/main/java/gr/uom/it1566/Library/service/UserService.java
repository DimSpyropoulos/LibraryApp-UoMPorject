package gr.uom.it1566.Library.service;

import gr.uom.it1566.Library.entity.LoanEntity;
import gr.uom.it1566.Library.entity.UserEntity;
import gr.uom.it1566.Library.repository.BookRepository;
import gr.uom.it1566.Library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository; // Repository για τη διαχείριση χρηστών
    private final BookRepository bookRepository; // Repository για τη διαχείριση βιβλίων
    private final LoanService loanService; // Υπηρεσία για τη διαχείριση δανειστικών συναλλαγών
    private final BookService bookService; // Υπηρεσία για τη διαχείριση βιβλίων

    // Εγγράφει έναν νέο χρήστη στο σύστημα
    public Mono<UserEntity> register(UserEntity user) {
        return userRepository.save(user); // Αποθηκεύει τον χρήστη στη βάση δεδομένων
    }
    public Mono<UserEntity> getName(String email) {
        return userRepository.findByEmail(email); // Αποθηκεύει τον χρήστη στη βάση δεδομένων
    }

    // Δανείζει ένα βιβλίο στον χρήστη
    public Mono<LoanEntity> borrowABook(UserEntity user, String bookTitle) {
        return bookRepository.findByTitle(bookTitle).flatMap(book -> {
            // Ελέγχει αν το βιβλίο είναι διαθέσιμο
            if (!book.isAvailable()) return Mono.error(new RuntimeException("Book is not available"));
            // Ενημερώνει τη διαθεσιμότητα του βιβλίου σε μη διαθέσιμο
            bookService.updateAvailability(bookTitle, false).subscribe();
            // Δημιουργεί μια νέα δανειστική συναλλαγή
            return loanService.createLoan(book.getIsbn(), bookTitle, user.getId());
        }).switchIfEmpty(Mono.error(new RuntimeException("Book does not exist"))); // Αν δεν βρεθεί το βιβλίο, επιστρέφει σφάλμα
    }

    // Επιστρέφει ένα δανεισμένο βιβλίο από τον χρήστη
    public Mono<LoanEntity> returnABook(UserEntity user, String bookTitle) {
        return bookService.getBookByTitle(bookTitle).flatMap(book -> {
            // Ελέγχει αν ο χρήστης έχει δανειστεί το συγκεκριμένο βιβλίο
            return loanService.getLoanByUserAndIsbn(user.getId(), book.getIsbn()).flatMap(loanEntity -> {
                // Ενημερώνει τη διαθεσιμότητα του βιβλίου σε διαθέσιμο
                bookService.updateAvailability(bookTitle, true).subscribe();
                // Ολοκληρώνει την δανειστική συναλλαγή
                return loanService.updateLoan(loanEntity);
            });
        });
    }

    // Επιστρέφει τον χρήστη με βάση το email του
    public Mono<UserEntity> getUserId(String email) {
        return userRepository.findByEmail(email);
    }
}
