package gr.uom.it1566.Library.service;

import gr.uom.it1566.Library.entity.BookEntity;
import gr.uom.it1566.Library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;


    public Flux<BookEntity> searchBooks(String query) {

        if(query.isEmpty()) {return bookRepository.getAllBooks();}
        else {
            return bookRepository.findByTitleContaining("%" + query.toLowerCase() + "%")
                    .switchIfEmpty(
                            bookRepository.findByAuthorContaining("%" + query.toLowerCase() + "%")
                            .switchIfEmpty(Flux.empty())
                    );
        }
    }

    // Επιστρέφει μια οντότητα βιβλίου με βάση τον τίτλο από το repository
    public Mono<BookEntity> getBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }


    // Ενημερώνει την κατάσταση διαθεσιμότητας ενός βιβλίου με βάση τον τίτλο
    public Mono<Void> updateAvailability(String bookTitle, boolean availability) {
        return bookRepository.updateBookAvailability(bookTitle, availability);
    }
}
