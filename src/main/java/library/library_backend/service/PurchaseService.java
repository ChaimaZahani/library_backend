package library.library_backend.service;

import library.library_backend.entity.*;
import library.library_backend.repository.BookRepository;
import library.library_backend.repository.PurchaseRepository;
import library.library_backend.repository.TransactionRepository;
import library.library_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class PurchaseService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;

    public Purchase purchaseBook(Map<String, Object> purchase, String username) {
        System.out.println("test2"+ purchase);



        // Fetch the user from the database using the extracted username
        User user = userRepository.findByUsername(username);
        if (user == null) {
             throw new RuntimeException("User  not found");
        }

        System.out.println(user.getUsername());
        Long bookId = Long.parseLong(purchase.get("bookId").toString());  // Extract the bookId
        BigDecimal price = new BigDecimal(purchase.get("price").toString());
        int quantity = Integer.parseInt(purchase.get("quantity").toString());
        // Optional: Validate the purchase details (price, quantity)
        if (price.compareTo(BigDecimal.ZERO) <= 0 || quantity <= 0) {
            throw new RuntimeException("Invalid purchase details.");
        }
        // Fetch the Book entity from the database using the bookId
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (book == null ) {
            throw new RuntimeException("Book not found");
        }
        // Check if there is enough stock
        if (book.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // Update the book's quantity
        book.setAvailableQuantity(book.getAvailableQuantity() - quantity);

        // Save the updated book entity
        bookRepository.save(book);

        LocalDateTime date = LocalDateTime.now();
        // Set the user in the purchase entity
        Purchase PurchasedBook = new Purchase(user,book,price,date,quantity);


        // Save the purchase
        return transactionRepository.save(PurchasedBook);
    }
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public List<Purchase> getPurchasesByUser(User user) {
        return purchaseRepository.getPurchasesByUser(user);
    }
}
