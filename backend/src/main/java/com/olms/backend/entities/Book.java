package com.olms.backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;

    @Column(nullable = false, length = 200)
    private String title;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(unique = true, length = 20)
    private String isbn;

    private Integer totalCopies;
    private Integer availableCopies;
    private String shelfLocation;

    // @ManyToMany
    // @JoinTable(
    //     name = "book_authors",
    //     joinColumns = @JoinColumn(name = "book_id"),
    //     inverseJoinColumns = @JoinColumn(name = "author_id")
    // )
    // private Set<Author> authors = new HashSet<>();  // lIst or hashset

    @ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "bookId"),
        inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "authorId")
    )
    private Set<Author> authors = new HashSet<>();


    // @Column(nullable = false)
    // private Boolean isExist = true;

    @Column(name = "is_exist", nullable = false)
    private Boolean isExist = true;
}