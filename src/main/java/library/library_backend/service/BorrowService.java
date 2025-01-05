package library.library_backend.service;

import library.library_backend.entity.Book;
import library.library_backend.entity.Borrow;
import library.library_backend.entity.User;
import library.library_backend.repository.BookRepository;
import library.library_backend.repository.BorrowRepository;
import library.library_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    public Borrow borrowBook(Long bookId, LocalDate dueDate, String username) {
        // Retrieve the book from the database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (book == null) {
            throw new RuntimeException("Book  not found");
        }
        // Check if there is stock available
        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("Book not available for borrowing");
        }

        // Update the book's quantity
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        // Create the borrow record
        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setTransactionDate(LocalDateTime.now());
        borrow.setDueDate(dueDate);  // Assuming 2 weeks borrow period
        // Fetch the user from the database using the extracted username
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User  not found");
        }
        borrow.setUser(user);
        // Save the borrow record
        borrowRepository.save(borrow);

        return borrow;
    }


    public boolean isBookAlreadyBorrowed(String username, Long bookId)  {
        System.out.println("username: " + username);
        System.out.println("bookId: " + bookId);

        // Find the borrow record based on username and bookId
        Borrow borrow = borrowRepository.findByUser_UsernameAndBookId(username, bookId);

        System.out.println("isBookAlreadyBorrowed: " + borrow);

        // If borrow is null, the book is not borrowed
        if (borrow == null) {
            // Throw an exception if the borrow is not found
            return false;
        }

        // Return true if the book is already borrowed
        return true;
    }


    public List<Borrow> getBorrowsByUser(User user) {
        return borrowRepository.getBorrowsByUser(user);
    }

    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }

    public Borrow getBorrowById(Long borrowId) {
        return borrowRepository.findById(borrowId).orElse(null);  // Find borrow record by ID
    }

    public void save(Borrow borrow) {
        borrowRepository.save(borrow);  // Save the updated borrow record

    }
}
