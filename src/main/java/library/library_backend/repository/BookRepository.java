package library.library_backend.repository;

import library.library_backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByArchiveIdIn(Collection<String> archiveIds);

    Book getBookByArchiveId(String archiveId);
    List<Book> findByDeletedFalse();

}

