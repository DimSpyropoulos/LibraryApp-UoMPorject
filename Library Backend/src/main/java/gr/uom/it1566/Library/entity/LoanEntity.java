package gr.uom.it1566.Library.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("loans")
public class LoanEntity {

    @Id
    private Integer loanId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private String bookIsbn;
    private String bookTitle;
    private Long userId;
    private boolean ongoing;
}

