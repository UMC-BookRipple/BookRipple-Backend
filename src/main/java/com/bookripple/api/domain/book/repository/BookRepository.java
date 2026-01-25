package com.bookripple.api.domain.book.repository;

import com.bookripple.api.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn13(String isbn13);

    Optional<Book> findByAladinBookId(Long aladinBookId);
}
