//package com.olms.backend.repository;
//
//import com.olms.backend.entities.Book;
//import com.olms.backend.entities.Author;
//import com.olms.backend.entities.Category;
//import com.olms.backend.entities.Publication;
//
//import jakarta.persistence.LockModeType;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Lock;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.data.jpa.repository.*;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
////// public interface BookRepository extends JpaRepository<Book, Integer> {
//////     Optional<Book> findByIsbn(String isbn);
////// }
////
////@Repository
////public interface BookRepository extends JpaRepository<Book, Integer> {
////    Optional<Book> findByIsbn(String isbn);
////    List<Book> findByAuthors(Author author);
////    List<Book> findByCategory(Category category);
////    List<Book> findByPublication(Publication publication);
////}
//
//// Abhishek
//
//
//// BookRepository.java
//public interface BookRepository extends JpaRepository<Book, Integer> {
//
//  Optional<Book> findByIsbn(String isbn);
//
//  @Lock(LockModeType.PESSIMISTIC_WRITE)
//  @Query("select b from Book b where b.bookId = :bookId")
//  Optional<Book> findByIdForUpdate(@Param("bookId") Integer bookId);
//
//  List<Book> findByAuthors(Author author);
//  List<Book> findByPublication(Publication pub);
//  List<Book> findByCategory(Category category);
//}


//package com.olms.backend.repository;
//
//import com.olms.backend.entities.*;
//import jakarta.persistence.LockModeType;
//import org.springframework.data.jpa.repository.*;
//import org.springframework.data.repository.query.Param;
//
//import java.util.*;
//
//public interface BookRepository extends JpaRepository<Book, Integer> {
//
//  Optional<Book> findByIsbn(String isbn);
//
//  List<Book> findByAuthors(Author author);
//  List<Book> findByPublication(Publication publication);
//  List<Book> findByCategory(Category category);
//
//  // for pessimistic lock when issuing/returning a copy
//  @Lock(LockModeType.PESSIMISTIC_WRITE)
//  @Query("select b from Book b where b.bookId = :id")
//  Optional<Book> findByIdForUpdate(@Param("id") Integer id);
//}


// com/olms/backend/repository/BookRepository.java
package com.olms.backend.repository;

import com.olms.backend.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.*;

public interface BookRepository extends JpaRepository<Book, Integer> {

  Optional<Book> findByIsbn(String isbn);

  List<Book> findByAuthors(Author a);

  List<Book> findByPublication(Publication p);

  List<Book> findByCategory(Category c);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select b from Book b where b.bookId = :id")
  Optional<Book> findByIdForUpdate(Integer id);
}
