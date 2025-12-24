package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.mapper.BookMapper;
import com.ja.easybookbackend.mapper.BookSeriesMapper;
import com.ja.easybookbackend.mapper.PublisherMapper;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.pojo.BookSeries;
import com.ja.easybookbackend.pojo.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookSeriesService {
    @Autowired
    private BookSeriesMapper bookSeriesMapper;
    @Autowired
    private PublisherMapper publisherMapper;
    @Autowired
    private BookMapper bookMapper;

    public ApiResponse<List<BookSeries>> list(String keyword, String publisherId) {
        List<BookSeries> items = bookSeriesMapper.search(keyword, publisherId);
        return ApiResponse.success(items);
    }

    public ApiResponse<BookSeries> getById(String seriesId) {
        BookSeries series = bookSeriesMapper.findById(seriesId);
        if (series == null) {
            return ApiResponse.error(404, "丛书不存在");
        }
        return ApiResponse.success(series);
    }

    public ApiResponse<BookSeries> create(BookSeries series) {
        // 校验必填字段
        if (series.getSeriesName() == null || series.getSeriesName().isEmpty()) {
            return ApiResponse.error(400, "丛书名称不能为空");
        }

        // 校验出版社是否存在
        if (series.getPublisherId() != null && !series.getPublisherId().isEmpty()) {
            Publisher publisher = publisherMapper.findById(series.getPublisherId());
            if (publisher == null) {
                return ApiResponse.error(404, "出版社不存在");
            }
        }

        // 生成seriesId
        if (series.getSeriesId() == null || series.getSeriesId().isEmpty()) {
            series.setSeriesId(genId("BS", 10));
        }

        // 默认值
        if (series.getTotalBooks() == null) {
            series.setTotalBooks(0);
        }

        bookSeriesMapper.insert(series);
        return ApiResponse.success(series);
    }

    public ApiResponse<BookSeries> update(String seriesId, BookSeries series) {
        BookSeries existing = bookSeriesMapper.findById(seriesId);
        if (existing == null) {
            return ApiResponse.error(404, "丛书不存在");
        }

        // 校验必填字段
        if (series.getSeriesName() == null || series.getSeriesName().isEmpty()) {
            return ApiResponse.error(400, "丛书名称不能为空");
        }

        // 校验出版社是否存在
        if (series.getPublisherId() != null && !series.getPublisherId().isEmpty()) {
            Publisher publisher = publisherMapper.findById(series.getPublisherId());
            if (publisher == null) {
                return ApiResponse.error(404, "出版社不存在");
            }
        }

        series.setSeriesId(seriesId);
        bookSeriesMapper.update(series);

        BookSeries updated = bookSeriesMapper.findById(seriesId);
        return ApiResponse.success(updated);
    }

    public ApiResponse<String> delete(String seriesId) {
        BookSeries series = bookSeriesMapper.findById(seriesId);
        if (series == null) {
            return ApiResponse.error(404, "丛书不存在");
        }

        // 检查是否有图书关联到这个丛书
        // 注意：这里简化处理，实际应该查询有多少本书属于这个丛书
        // 如果有关联的书，可以选择禁止删除或者自动解除关联

        bookSeriesMapper.delete(seriesId);
        return ApiResponse.success("丛书已删除", "");
    }

    /**
     * 查询某个丛书包含的所有图书
     */
    public ApiResponse<List<Book>> getBooksBySeries(String seriesId) {
        BookSeries series = bookSeriesMapper.findById(seriesId);
        if (series == null) {
            return ApiResponse.error(404, "丛书不存在");
        }

        // 查询属于该丛书的所有图书
        List<Book> books = bookMapper.searchBooks(null, null, null, null, null, "active", "title", 1000, 0, false);
        books.removeIf(book -> !seriesId.equals(book.getSeriesId()));

        return ApiResponse.success(books);
    }

    private String genId(String prefix, int maxLen) {
        String base = UUID.randomUUID().toString().replace("-", "");
        int len = Math.max(0, maxLen - prefix.length());
        return prefix + base.substring(0, len);
    }
}
