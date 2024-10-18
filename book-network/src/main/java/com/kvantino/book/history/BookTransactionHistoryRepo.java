package com.kvantino.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepo extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
                        SELECT history
                        from BookTransactionHistory history
                        where history.user.id = :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long userId);

    @Query("""
                            SELECT history
                            from BookTransactionHistory history
                            where history.book.owner.id = :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long userId);

    @Query("""
                        SELECT (COUNT(*) > 0) AS isBorrowed
                        FROM  BookTransactionHistory bookTransactionHistory
                        WHERE bookTransactionHistory.user.id = :userId
                        AND bookTransactionHistory.book.id = :bookId
                        AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Long userId);

    @Query("""
                        SELECT transaction
                        FROM BookTransactionHistory transaction
                        WHERE transaction.user.id = :userId
                        AND transaction.book.id = :bookId
                        AND transaction.returned = false
                        AND transaction.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Long userId);

    @Query("""
                SELECT transaction
                        FROM BookTransactionHistory transaction
                        WHERE transaction.book.owner.id = :ownerId
                        AND transaction.book.id = :bookId
                        AND transaction.returned = true
                        AND transaction.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Long ownerId);
}
