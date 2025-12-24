package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.BookDetailResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ja.easybookbackend.dto.CreateBookRequest;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public ApiResponse<PageResult<Book>> list(@RequestParam(value = "page", required = false) Integer page,
                                              @RequestParam(value = "page_size", required = false) Integer pageSize,
                                              @RequestParam(value = "keyword", required = false) String keyword,
                                              @RequestParam(value = "category_id", required = false) String categoryId,
                                              @RequestParam(value = "publisher_id", required = false) String publisherId,
                                              @RequestParam(value = "min_price", required = false) Double minPrice,
                                              @RequestParam(value = "max_price", required = false) Double maxPrice,
                                              @RequestParam(value = "status", required = false) String status,
                                              @RequestParam(value = "sort", required = false) String sort) {
        return bookService.listBooks(page, pageSize, keyword, categoryId, publisherId, minPrice, maxPrice, status, sort);
    }

    @GetMapping("/{isbn}")
    public ApiResponse<BookDetailResponse> detail(@PathVariable("isbn") String isbn) {
        return bookService.getBookDetail(isbn);
    }

    @GetMapping("/search")
    public ApiResponse<PageResult<Book>> search(@RequestParam("q") String q,
                                                @RequestParam(value = "page", required = false) Integer page,
                                                @RequestParam(value = "page_size", required = false) Integer pageSize) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (p - 1) * ps;
        java.util.List<Book> items = bookService.fullTextSearch(q, ps, offset);
        Integer total = bookService.fullTextCount(q);
        return ApiResponse.success(new PageResult<>(items, total, p, ps));
    }

    @PostMapping
    public ApiResponse<Book> create(@RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @PutMapping("/{isbn}")
    public ApiResponse<Book> update(@PathVariable("isbn") String isbn,
                                    @RequestBody CreateBookRequest request) {
        return bookService.updateBook(isbn, request);
    }

    @DeleteMapping("/{isbn}")
    public ApiResponse<String> delete(@PathVariable("isbn") String isbn) {
        return bookService.deleteBook(isbn);
    }
}
