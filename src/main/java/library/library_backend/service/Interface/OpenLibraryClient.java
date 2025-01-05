package library.library_backend.service.Interface;

import com.fasterxml.jackson.databind.JsonNode;
import library.library_backend.entity.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "openLibraryClient", url = "https://openlibrary.org/")
public interface OpenLibraryClient {

    // Search books by category
    @GetMapping("/subjects/{category}.json")
    JsonNode getBooksByCategory(@RequestParam("category") String category);

    // General search (books and other items)
    @GetMapping("/search.json")
    String searchBooks(@RequestParam Map<String, String> params);

    // Author-specific search
    @GetMapping("/search/authors.json")
    String searchAuthors(@RequestParam("q") String query);

    @GetMapping("/{archiveId}.json")
    JsonNode getBooksByArchiveId(@PathVariable String archiveId);
}
