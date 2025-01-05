package library.library_backend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@DiscriminatorValue("BORROW")
public class Borrow extends Transaction {

    private LocalDate dueDate;
    private LocalDate actualReturnDate;  // Actual return date
    private boolean overdue;  // Flag to check if the return is overdue
    // Constructor for Borrow
    public Borrow(User user, Book book, LocalDate dueDate, LocalDateTime transactionDate) {
        super(user, book, transactionDate);
        this.dueDate = dueDate;
    }
}
