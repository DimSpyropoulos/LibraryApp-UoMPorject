package gr.uom.it1566.Library.repository;

import gr.uom.it1566.Library.entity.BookEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Δηλώνει το BookRepository που επεκτείνει το R2dbcRepository για την οντότητα BookEntity
public interface BookRepository extends R2dbcRepository<BookEntity, String> {

    @Query("SELECT * FROM books")
    Flux<BookEntity> getAllBooks();

    @Query("SELECT * FROM books WHERE LOWER(title) LIKE :title")
    Flux<BookEntity> findByTitleContaining(String title);

    @Query("SELECT * FROM books WHERE LOWER(author) LIKE :author")
    Flux<BookEntity> findByAuthorContaining(String author);

    // Μέθοδος για εύρεση βιβλίου με βάση τον τίτλο
    Mono<BookEntity> findByTitle(String title);

    // Ενημερώνει τη διαθεσιμότητα ενός βιβλίου βάσει του τίτλου
    @Query("UPDATE books SET available = :bookAvailability WHERE title = :bookTitle")
    Mono<Void> updateBookAvailability(String bookTitle, boolean bookAvailability);


}
