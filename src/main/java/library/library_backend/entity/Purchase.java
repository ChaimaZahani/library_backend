package library.library_backend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@DiscriminatorValue("PURCHASE")
public class Purchase extends Transaction {

    private BigDecimal price;
    private int quantity;
    // Constructor for Purchase
    public Purchase(User user, Book book, BigDecimal price, LocalDateTime transactionDate,int quantity) {
        super(user, book, transactionDate);
        this.price = price;
        this.quantity = quantity;
    }
}
