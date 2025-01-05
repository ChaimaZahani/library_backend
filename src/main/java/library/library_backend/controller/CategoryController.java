package library.library_backend.controller;


import jakarta.persistence.EntityNotFoundException;
import library.library_backend.entity.Book;
import library.library_backend.entity.Category;
import library.library_backend.repository.BookRepository;
import library.library_backend.repository.CategoryRepository;
import library.library_backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    // Constructor injection for the service
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get all categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Get a category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    // Add a new category
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Map<String, Object> category) {

        Category newCategory = new Category((String) category.get("name"));
        categoryService.addCategory(newCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    // Update an existing category
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Map<String, Object>  updatedCategory) {

        Category category = categoryService.updateCategory(id, updatedCategory);
        return ResponseEntity.ok(category);
    }

    // Delete a category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {





        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        List<Book> books = category.getBooks();
        if (books != null) {
            for (Book book : books) {
                book.getCategories().remove(category);
                bookRepository.save(book); // Save changes to the book
            }
        }

        // Finally, delete the category itself
        categoryRepository.delete(category);
        return ResponseEntity.noContent().build();
    }
}
