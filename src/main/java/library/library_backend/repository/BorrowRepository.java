package library.library_backend.repository;

import library.library_backend.entity.Borrow;
import library.library_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    Borrow findByUser_UsernameAndBookId(String username, Long bookId);

    List<Borrow> getBorrowsByUser(User user);

}
