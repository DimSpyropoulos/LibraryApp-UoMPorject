package gr.uom.it1566.Library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// Δηλώνει ότι αυτή η κλάση είναι οντότητα που αναπαριστά ένα βιβλίο
@Data // Δημιουργεί getters, setters, toString, equals και hashCode με βάση τα πεδία
@AllArgsConstructor // Δημιουργεί constructor με όλα τα πεδία
@NoArgsConstructor // Δημιουργεί default constructor χωρίς παραμέτρους
@Builder(toBuilder = true) // Δημιουργεί builder pattern για εύκολη κατασκευή αντικειμένων
@Table("books") // Δηλώνει ότι αυτή η οντότητα αντιστοιχεί στον πίνακα "books" στη βάση δεδομένων
public class BookEntity {

    @Id // Δηλώνει ότι αυτό το πεδίο είναι το πρωτεύον κλειδί της οντότητας
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String pages;
    private boolean available;
}
