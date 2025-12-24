package com.ja.easybookbackend.controller;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.pojo.BookSeries;
import com.ja.easybookbackend.service.BookSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/book-series")
@CrossOrigin(origins = "*")
public class BookSeriesController {
    @Autowired
    private BookSeriesService bookSeriesService;

    /**
     * 获取丛书列表（支持搜索和出版社筛选）
     * GET /api/admin/book-series?keyword=XXX&publisherId=P001
     */
    @GetMapping
    public ApiResponse<List<BookSeries>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String publisherId) {
        return bookSeriesService.list(keyword, publisherId);
    }

    /**
     * 获取丛书详情
     * GET /api/admin/book-series/{seriesId}
     */
    @GetMapping("/{seriesId}")
    public ApiResponse<BookSeries> getById(@PathVariable String seriesId) {
        return bookSeriesService.getById(seriesId);
    }

    /**
     * 创建丛书
     * POST /api/admin/book-series
     * Body: { "series_name": "XXX", "publisher_id": "P001", "total_books": 5, "description": "..." }
     */
    @PostMapping
    public ApiResponse<BookSeries> create(@RequestBody BookSeries series) {
        return bookSeriesService.create(series);
    }

    /**
     * 更新丛书
     * PUT /api/admin/book-series/{seriesId}
     */
    @PutMapping("/{seriesId}")
    public ApiResponse<BookSeries> update(@PathVariable String seriesId, @RequestBody BookSeries series) {
        return bookSeriesService.update(seriesId, series);
    }

    /**
     * 删除丛书
     * DELETE /api/admin/book-series/{seriesId}
     */
    @DeleteMapping("/{seriesId}")
    public ApiResponse<String> delete(@PathVariable String seriesId) {
        return bookSeriesService.delete(seriesId);
    }

    /**
     * 查询某个丛书包含的所有图书
     * GET /api/admin/book-series/{seriesId}/books
     */
    @GetMapping("/{seriesId}/books")
    public ApiResponse<List<Book>> getBooks(@PathVariable String seriesId) {
        return bookSeriesService.getBooksBySeries(seriesId);
    }
}
