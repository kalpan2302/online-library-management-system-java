package com.olms.backend.controller;

import com.olms.backend.dto.CategoryRequest;
import com.olms.backend.dto.UpdateCategoryRequest;
import com.olms.backend.entities.Category;
import com.olms.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasAuthority('ADMIN')")
public class CategoryController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody CategoryRequest req) {
        Category created = bookService.addCategory(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id,
    @RequestBody UpdateCategoryRequest req) {
        return ResponseEntity.ok(bookService.updateCategory(id, req));
    }
    
    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
    //     bookService.deleteCategory(id);
    //     return ResponseEntity.ok("Category soft-deleted");
    // }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        bookService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> listCategories() {
        return ResponseEntity.ok(bookService.listCategories());
    }
}