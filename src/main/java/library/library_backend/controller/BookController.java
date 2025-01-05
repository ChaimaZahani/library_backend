package library.library_backend.controller;


import com.fasterxml.jackson.databind.JsonNode;
import library.library_backend.entity.AuthorDTO;
import library.library_backend.entity.Book;
import library.library_backend.repository.CategoryRepository;
import library.library_backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    @Autowired
    private  CategoryRepository categoryRepository;
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Fetch all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllNonDeleted();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);
    }
    // Search books based on category
    @GetMapping("/category/{category}")
    public JsonNode getBooksByCategory(@PathVariable String category) {
        return bookService.fetchBooksByCategory(category);
    }
    // Search books based on various parameters
    @GetMapping("/books")
    public String searchBooks(@RequestParam Map<String, String> params) {
        return bookService.searchBooks(params);
    }

    // Controller method to fetch a book by archiveId
    @GetMapping("/byArchiveId")
    public ResponseEntity<Book> getBookByArchiveId(@RequestParam String archiveId) {
        Book book = bookService.getBookByArchiveId(archiveId);

        if (book != null) {
            return ResponseEntity.ok(book);  // Return the book with status 200 OK
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if the book is not found
        }
    }
    // Search authors by query
    @GetMapping("/authors")
    public String searchAuthors(@RequestParam("q") String query) {
        return bookService.searchAuthors(query);
    }
    // Endpoint to save books fetched by category
    @PostMapping("/saveByCategory")
    public ResponseEntity<?> saveBooksByCategory(@RequestParam String category) {
        Map<String, String> response = new HashMap<>();
        try {
            JsonNode books = bookService.fetchBooksByCategory(category);
            if (books != null && !books.isEmpty()) {
                bookService.saveBooksFromApi(books.get("works"), category);
                response.put("message", "Book saved successfully!");

                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No books found for this category.");

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("message", "An error occurred: " + e.getMessage());

            return ResponseEntity.ok(response);
        }
    }
    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody Map<String, Object> bookData) {
        Map<String, String> response = new HashMap<>();
        System.out.println(bookData);
        try {
            Book book = new Book();
            System.out.println("test");
            book.setTitle((String) bookData.get("title"));
            book.setArchiveId((String) bookData.get("archiveId"));
            Map<String, Object> availabilityStatus = (Map<String, Object>) bookData.get("availabilityStatus");
            if (availabilityStatus != null) {
                book.setAvailabilityStatus((String) availabilityStatus.get("name"));
            } else {
                book.setAvailabilityStatus("default status"); // Handle the case where availabilityStatus is null or doesn't contain "name"
            }
            book.setFirstPublishYear((int) bookData.get("firstPublishYear"));
            book.setDescription((String) bookData.get("description"));
            book.setImageUrl((String) bookData.get("imageUrl"));
            System.out.println("test2");
            Object priceData = bookData.get("price");

            if (priceData instanceof Double) {
                book.setPrice(BigDecimal.valueOf((Double) priceData));
            } else if (priceData instanceof Integer) {
                book.setPrice(BigDecimal.valueOf((Integer) priceData));
            } else {
                // Handle the case where the price is neither Double nor Integer
                // Maybe throw an exception or set a default value
                throw new IllegalArgumentException("Invalid price type");
            }
            book.setAvailableQuantity((int) bookData.get("availableQuantity"));
            System.out.println("test4");

            // Handle authors
            List<AuthorDTO> authorDTOs = new ArrayList<>();
            List<Map<String, String>> authorData = (List<Map<String, String>>) bookData.get("authors");
            System.out.println(authorData);
            for (Map<String, String> authorInfo : authorData) {
                System.out.println("testt***************"+authorInfo);
                String authorKey = authorInfo.get("key");
                String authorName = authorInfo.get("name");

                // Create new AuthorDTO and add it to the list
                AuthorDTO authorDTO = new AuthorDTO();
                authorDTO.setKey(authorKey);
                authorDTO.setName(authorName);
                authorDTOs.add(authorDTO);
            }
            book.setAuthors(authorDTOs);
            System.out.println("test4");

            // Handle categories
            List<Map<String, Object>> categoryNames = (List<Map<String, Object>>) bookData.get("categories");
            if (categoryNames != null) {
                for (Map<String, Object> categoryData : categoryNames) {
                    System.out.println(categoryData);
                    String category = categoryData.get("name").toString();
                    book.addCategoryByName(category,categoryRepository);
                }
            }
            System.out.println("test5");

            bookService.saveBook(book); // Assuming the saveBook method is implemented in BookService
            response.put("message", "Book added successfully!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Endpoint to update a book
    @PutMapping("/update/{id}")
    public ResponseEntity <?> updateBook(@PathVariable Long id, @RequestBody Map<String, Object> bookData) {
        Optional<Book> existingBook = bookService.getBookById(id);
        Map<String, String> response = new HashMap<>();

        System.out.println(bookData);
        if (existingBook.isPresent()) {
            try {
                Book book = new Book();
                book.setTitle((String) bookData.get("title"));
                book.setArchiveId((String) bookData.get("archiveId"));
                book.setCoverEditionKey((String) bookData.get("coverEditionKey"));
                Map<String, Object> availabilityStatus = (Map<String, Object>) bookData.get("availabilityStatus");
                if (availabilityStatus != null) {
                    book.setAvailabilityStatus((String) availabilityStatus.get("name"));
                } else {
                    book.setAvailabilityStatus("default status"); // Handle the case where availabilityStatus is null or doesn't contain "name"
                }
                book.setFirstPublishYear((int) bookData.get("firstPublishYear"));
                book.setDescription((String) bookData.get("description"));
                book.setImageUrl((String) bookData.get("imageUrl"));
                System.out.println("test2");
                Object priceData = bookData.get("price");

                if (priceData instanceof Double) {
                    book.setPrice(BigDecimal.valueOf((Double) priceData));
                } else if (priceData instanceof Integer) {
                    book.setPrice(BigDecimal.valueOf((Integer) priceData));
                } else {
                    // Handle the case where the price is neither Double nor Integer
                    // Maybe throw an exception or set a default value
                    throw new IllegalArgumentException("Invalid price type");
                }
                book.setAvailableQuantity((int) bookData.get("availableQuantity"));
                System.out.println("test4");

                // Handle authors
                List<AuthorDTO> authorDTOs = new ArrayList<>();
                List<Map<String, String>> authorData = (List<Map<String, String>>) bookData.get("authors");
                System.out.println(authorData);
                for (Map<String, String> authorInfo : authorData) {
                    System.out.println("testt***************"+authorInfo);
                    String authorKey = authorInfo.get("key");
                    String authorName = authorInfo.get("name");

                    // Create new AuthorDTO and add it to the list
                    AuthorDTO authorDTO = new AuthorDTO();
                    authorDTO.setKey(authorKey);
                    authorDTO.setName(authorName);
                    authorDTOs.add(authorDTO);
                }
                book.setAuthors(authorDTOs);
                System.out.println("test4");

                // Handle categories
                List<Map<String, Object>> categoryNames = (List<Map<String, Object>>) bookData.get("categories");
                if (categoryNames != null) {
                    for (Map<String, Object> categoryData : categoryNames) {
                        System.out.println(categoryData);
                        String category = categoryData.get("name").toString();
                        book.addCategoryByName(category,categoryRepository);
                    }
                }
                bookService.updateBook(id, book);
                response.put("message", "Book added successfully!");

                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("message", "An error occurred while updating the book: " + e.getMessage());

                return ResponseEntity.status(500).body(response);
            }
        } else {
            response.put("message", "Book not found!");

            return ResponseEntity.status(404).body(response);
        }
    }

    // Endpoint to delete a book
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        Optional<Book> bookOptional = bookService.getBookById(id);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setDeleted(true);  // Mark the book as deleted
            bookService.save(book); // Save the updated book

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();

    }

}

