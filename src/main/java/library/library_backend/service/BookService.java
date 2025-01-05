package library.library_backend.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import library.library_backend.entity.Book;
import library.library_backend.entity.BookDTO;
import library.library_backend.entity.Category;
import library.library_backend.repository.BookRepository;
import library.library_backend.repository.CategoryRepository;
import library.library_backend.service.Interface.OpenLibraryClient;
import org.springframework.stereotype.Service;


import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final OpenLibraryClient openLibraryClient;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BookService(BookRepository bookRepository, OpenLibraryClient openLibraryClient, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.openLibraryClient = openLibraryClient;
        this.categoryRepository = categoryRepository;
    }

    // Fetch all books from the database
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    // Search books by category
    public JsonNode  fetchBooksByCategory(String category) {
        return openLibraryClient.getBooksByCategory(category);
    }
    // Search books by archiveId
    public JsonNode  fetchBooksByArchiveId(String archiveId) {
        return openLibraryClient.getBooksByArchiveId(archiveId);
    }
    // Search books with various query parameters
    public String searchBooks(Map<String, String> params) {
        return openLibraryClient.searchBooks(params);
    }

    // Search authors with a specific query
    public String searchAuthors(String query) {
        return openLibraryClient.searchAuthors(query);
    }


    public void saveBooksFromApi(JsonNode worksNode, String categoryName) {
        // Create an instance of BookMapper
        BookMapper bookMapper = new BookMapper();


        // Create a list to hold BookDTOs
        List<BookDTO> bookDTOList = new ArrayList<>();
        for (JsonNode workNode : worksNode) {
            // Map JsonNode to BookDTO using BookMapper
            BookDTO bookDTO = bookMapper.mapToDTO(workNode);
            JsonNode bookData = fetchBooksByArchiveId(bookDTO.getKey());
            if (bookData.has("description") ) {
                String description="";
                if (bookData.get("description").has("value")){

                    description = bookData.get("description").get("value").asText();

                }else {
                     description = bookData.get("description").asText();
                }
                bookDTO.setDescription(description);
            }
            // Add the mapped BookDTO to the list
            bookDTOList.add(bookDTO);
        }

        // Extract archiveIds for existing books lookup
        List<String> archiveIds = bookDTOList.stream().map(BookDTO::getKey).collect(Collectors.toList());
        List<Book> existingBooks = bookRepository.findByArchiveIdIn(archiveIds);
        Map<String, Book> existingBooksMap = existingBooks.stream().collect(Collectors.toMap(Book::getArchiveId, book -> book));

        // Fetch the category by name or create a new one if it doesn't exist
        // Fetch the category by name
        Category newCategory = categoryRepository.findByName(categoryName);
        if (newCategory == null) {
            // Category doesn't exist, create and save it
            newCategory = categoryRepository.save(new Category(categoryName));
        }


        for (BookDTO bookDTO : bookDTOList) {
            Book book = existingBooksMap.get(bookDTO.getKey());
            if (book != null) {
                // Update existing book
                book.setTitle(bookDTO.getTitle());
                book.setCoverId(bookDTO.getCover_Id());
                book.setCoverEditionKey(bookDTO.getCoverEditionKey());
                if (!book.getCategories().contains(newCategory)) {
                    book.addCategoryById(newCategory.getId());  // Only add if not already added
                }
            } else {
                // Save new book using BookMapper to convert BookDTO to Book
                book = bookMapper.mapToEntity(bookDTO);
                book.addCategoryById(newCategory.getId());

            }

            // Pricing and quantity logic
            String availabilityStatus = bookDTO.getAvailability() != null ? bookDTO.getAvailability() : "private";
            book.setAvailabilityStatus(availabilityStatus);
            BigDecimal price= BigDecimal.valueOf(0);
            if ("open".equalsIgnoreCase(availabilityStatus) ) {
                price=new BigDecimal("10.00");
            }else if("private".equalsIgnoreCase(availabilityStatus)){
                price=new BigDecimal("50.00");
            }
            book.setPrice(price);
            String description = bookDTO.getDescription() != null ? bookDTO.getDescription() : "no description available";
            book.setDescription(description);
            int availableQuantity = "open".equalsIgnoreCase(availabilityStatus) ? 20 : 10;
            book.setAvailableQuantity(availableQuantity);

            // Save the new book entity
            bookRepository.save(book);
        }

    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }
    public Book getBookByArchiveId(String archiveId) {
        return  bookRepository.getBookByArchiveId(archiveId);
    }
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void updateBook(Long id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Update fields
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthors(updatedBook.getAuthors());
        existingBook.setCategories(updatedBook.getCategories());
        existingBook.setFirstPublishYear(updatedBook.getFirstPublishYear());
        existingBook.setAvailabilityStatus(updatedBook.getAvailabilityStatus());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setArchiveId(updatedBook.getArchiveId());
        existingBook.setCoverId(updatedBook.getCoverId());
        existingBook.setCoverEditionKey(updatedBook.getCoverEditionKey());
        existingBook.setAvailableQuantity(updatedBook.getAvailableQuantity());
        existingBook.setImageUrl(updatedBook.getImageUrl());

        bookRepository.save(existingBook);
    }

    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public List<Book> getAllNonDeleted() {
        return bookRepository.findByDeletedFalse();
    }
}
