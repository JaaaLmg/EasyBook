package com.ja.easybookbackend.service;

import com.ja.easybookbackend.dto.ApiResponse;
import com.ja.easybookbackend.dto.BookDetailResponse;
import com.ja.easybookbackend.dto.PageResult;
import com.ja.easybookbackend.mapper.AuthorMapper;
import com.ja.easybookbackend.mapper.BookMapper;
import com.ja.easybookbackend.mapper.InventoryMapper;
import com.ja.easybookbackend.mapper.PublisherMapper;
import com.ja.easybookbackend.mapper.BookRelationMapper;
import com.ja.easybookbackend.dto.CreateBookRequest;
import com.ja.easybookbackend.pojo.Book;
import com.ja.easybookbackend.pojo.Inventory;
import com.ja.easybookbackend.pojo.Publisher;
import com.ja.easybookbackend.pojo.Author;
import com.ja.easybookbackend.pojo.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private AuthorMapper authorMapper;
    @Autowired
    private PublisherMapper publisherMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private BookRelationMapper bookRelationMapper;

    public ApiResponse<PageResult<Book>> listBooks(Integer page,
                                                   Integer pageSize,
                                                   String keyword,
                                                   String categoryId,
                                                   String publisherId,
                                                   Double minPrice,
                                                   Double maxPrice,
                                                   String status,
                                                   String sort) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (p - 1) * ps;
        boolean joinAuthors = keyword != null && !keyword.isEmpty();
        List<Book> items = bookMapper.searchBooks(keyword, categoryId, publisherId, minPrice, maxPrice, status, sort, ps, offset, joinAuthors);
        Integer total = bookMapper.countBooks(keyword, categoryId, publisherId, minPrice, maxPrice, status, joinAuthors);
        PageResult<Book> result = new PageResult<>(items, total, p, ps);
        return ApiResponse.success(result);
    }

    public ApiResponse<BookDetailResponse> getBookDetail(String isbn) {
        Book book = bookMapper.findByIsbn(isbn);
        if (book == null) {
            return ApiResponse.error(404, "图书不存在");
        }
        BookDetailResponse response = BookDetailResponse.empty();
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setEdition(book.getEdition());
        response.setPublisherId(book.getPublisherId());
        response.setPublishDate(book.getPublishDate());
        response.setPrice(book.getPrice());
        response.setPageCount(book.getPageCount());
        response.setFormat(book.getFormat());
        response.setLanguage(book.getLanguage());
        response.setDescription(book.getDescription());
        response.setTableOfContents(book.getTableOfContents());
        response.setCoverImage(book.getCoverImage());
        response.setSeriesId(book.getSeriesId());
        response.setBookType(book.getBookType());
        response.setStatus(book.getStatus());
        response.setCreateTime(book.getCreateTime());
        response.setUpdateTime(book.getUpdateTime());
        response.setAuthors(authorMapper.findAuthorsByIsbn(isbn));
        Publisher publisher = publisherMapper.findById(book.getPublisherId());
        response.setPublisher(publisher);
        Inventory inv = inventoryMapper.findByIsbn(isbn);
        BookDetailResponse.InventorySummary invSummary = new BookDetailResponse.InventorySummary();
        if (inv != null) {
            int quantity = inv.getQuantity() == null ? 0 : inv.getQuantity();
            int safety = inv.getSafetyStock() == null ? 0 : inv.getSafetyStock();
            String status = quantity == 0 ? "out_of_stock" : (quantity < safety ? "low" : "normal");
            invSummary.setQuantity(quantity);
            invSummary.setReservedQuantity(inv.getReservedQuantity() == null ? 0 : inv.getReservedQuantity());
            invSummary.setAvailableQuantity(quantity);
            invSummary.setSafetyStock(safety);
            invSummary.setStatus(status);
        } else {
            invSummary.setQuantity(0);
            invSummary.setReservedQuantity(0);
            invSummary.setAvailableQuantity(0);
            invSummary.setSafetyStock(0);
            invSummary.setStatus("out_of_stock");
        }
        response.setInventory(invSummary);
        return ApiResponse.success(response);
    }

    public java.util.List<Book> fullTextSearch(String q, int pageSize, int offset) {
        return bookMapper.fullTextSearch(q, pageSize, offset);
    }

    public Integer fullTextCount(String q) {
        return bookMapper.fullTextCount(q);
    }

    private String genId(String prefix, int maxLen) {
        String base = UUID.randomUUID().toString().replace("-", "");
        int len = Math.max(0, maxLen - prefix.length());
        return prefix + base.substring(0, len);
    }
    private String safeId(String id, int maxLen) {
        if (id == null) return null;
        String s = id.replace("-", "");
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }

    @Transactional
    public ApiResponse<Book> createBook(CreateBookRequest req) {
        if (req == null) {
            return ApiResponse.error(400, "请求体为空");
        }
        if (req.getIsbn() == null || req.getIsbn().isEmpty()) {
            return ApiResponse.error(400, "缺少ISBN");
        }
        Book exists = bookMapper.findByIsbn(req.getIsbn());
        if (exists != null) {
            return ApiResponse.error(400, "图书已存在");
        }
        Book book = new Book();
        book.setIsbn(req.getIsbn());
        book.setTitle(req.getTitle());
        book.setEdition(req.getEdition());
        book.setPublisherId(req.getPublisherId());
        if (req.getPublishDate() != null && !req.getPublishDate().isEmpty()) {
            book.setPublishDate(LocalDate.parse(req.getPublishDate()));
        }
        book.setPrice(req.getPrice() == null ? 0.0 : req.getPrice());
        book.setPageCount(req.getPageCount());
        book.setFormat(req.getFormat());
        book.setLanguage(req.getLanguage() == null ? "zh" : req.getLanguage());
        book.setDescription(req.getDescription());
        book.setTableOfContents(req.getTableOfContents());
        book.setCoverImage(req.getCoverImage());
        book.setSeriesId(req.getSeriesId());
        book.setBookType(req.getBookType() == null ? "normal" : req.getBookType());
        book.setStatus(req.getStatus() == null ? "active" : req.getStatus());
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.insert(book);

        if (req.getAuthors() != null && !req.getAuthors().isEmpty()) {
            for (CreateBookRequest.AuthorRef ar : req.getAuthors()) {
                if (ar.getAuthorId() != null && !ar.getAuthorId().isEmpty()) {
                    int order = ar.getAuthorOrder() == null ? 1 : ar.getAuthorOrder();
                    String aid = safeId(ar.getAuthorId(), 10);
                    Author foundById = authorMapper.findById(aid);
                    if (foundById == null) {
                        String name = null;
                        if (req.getAuthorNames() != null && ar.getAuthorOrder() != null && ar.getAuthorOrder() >= 1 && req.getAuthorNames().size() >= ar.getAuthorOrder()) {
                            name = req.getAuthorNames().get(ar.getAuthorOrder() - 1);
                        }
                        Author a = new Author();
                        a.setAuthorId(aid);
                        a.setAuthorName((name == null || name.isBlank()) ? "未知作者" : name.trim());
                        a.setNationality(null);
                        a.setBirthDate(null);
                        a.setBiography(null);
                        a.setCreateTime(LocalDateTime.now());
                        authorMapper.insert(a);
                    }
                    bookRelationMapper.insertBookAuthor(req.getIsbn(), aid, order);
                }
            }
        }
        if ((req.getAuthors() == null || req.getAuthors().isEmpty()) && req.getAuthorNames() != null && !req.getAuthorNames().isEmpty()) {
            int order = 1;
            for (String name : req.getAuthorNames()) {
                if (name == null || name.isBlank()) continue;
                Author found = authorMapper.findByName(name.trim());
                if (found == null) {
                    Author a = new Author();
                    a.setAuthorId(genId("A", 10));
                    a.setAuthorName(name.trim());
                    a.setNationality(null);
                    a.setBirthDate(null);
                    a.setBiography(null);
                    a.setCreateTime(LocalDateTime.now());
                    authorMapper.insert(a);
                    found = a;
                }
                bookRelationMapper.insertBookAuthor(req.getIsbn(), found.getAuthorId(), order);
                order++;
                if (order > 4) break;
            }
        }

        if (req.getKeywords() != null && !req.getKeywords().isEmpty()) {
            int count = 0;
            for (String kwText : req.getKeywords()) {
                if (kwText == null || kwText.isBlank()) continue;
                Keyword kw = bookRelationMapper.findKeywordByText(kwText.trim());
                if (kw == null) {
                    kw = new Keyword();
                    kw.setKeywordId(genId("K", 10));
                    kw.setKeyword(kwText.trim());
                    kw.setSearchCount(0);
                    kw.setCreateTime(LocalDateTime.now());
                    bookRelationMapper.insertKeyword(kw);
                }
                bookRelationMapper.insertBookKeyword(req.getIsbn(), kw.getKeywordId(), 1.0);
                count++;
                if (count >= 10) break;
            }
        }

        Inventory inv = new Inventory();
        inv.setInventoryId("INV" + UUID.randomUUID().toString().substring(0, 12));
        inv.setIsbn(req.getIsbn());
        inv.setQuantity(req.getInitialStock() == null ? 0 : req.getInitialStock());
        inv.setSafetyStock(req.getSafetyStock() == null ? 10 : req.getSafetyStock());
        inv.setReservedQuantity(0);
        inv.setLocationCode(req.getLocationCode());
        inv.setWarehouse(req.getWarehouse() == null ? "main" : req.getWarehouse());
        inv.setLastRestock(null);
        inv.setLastCheck(LocalDateTime.now());
        inventoryMapper.insert(inv);

        return ApiResponse.success(book);
    }

    @Transactional
    public ApiResponse<Book> updateBook(String isbn, CreateBookRequest req) {
        if (req == null) {
            return ApiResponse.error(400, "请求体为空");
        }

        // 检查图书是否存在
        Book book = bookMapper.findByIsbn(isbn);
        if (book == null) {
            return ApiResponse.error(404, "图书不存在");
        }

        // 更新基本信息
        if (req.getTitle() != null) book.setTitle(req.getTitle());
        if (req.getEdition() != null) book.setEdition(req.getEdition());
        if (req.getPublisherId() != null) book.setPublisherId(req.getPublisherId());
        if (req.getPublishDate() != null && !req.getPublishDate().isEmpty()) {
            book.setPublishDate(LocalDate.parse(req.getPublishDate()));
        }
        if (req.getPrice() != null) book.setPrice(req.getPrice());
        if (req.getPageCount() != null) book.setPageCount(req.getPageCount());
        if (req.getFormat() != null) book.setFormat(req.getFormat());
        if (req.getLanguage() != null) book.setLanguage(req.getLanguage());
        if (req.getDescription() != null) book.setDescription(req.getDescription());
        if (req.getTableOfContents() != null) book.setTableOfContents(req.getTableOfContents());
        if (req.getCoverImage() != null) book.setCoverImage(req.getCoverImage());
        if (req.getSeriesId() != null) book.setSeriesId(req.getSeriesId());
        if (req.getBookType() != null) book.setBookType(req.getBookType());
        if (req.getStatus() != null) book.setStatus(req.getStatus());
        book.setUpdateTime(LocalDateTime.now());

        bookMapper.update(book);

        // 更新作者关系（先删除旧的，再添加新的）
        if (req.getAuthorNames() != null && !req.getAuthorNames().isEmpty()) {
            // 删除旧的作者关系
            bookRelationMapper.deleteBookAuthors(isbn);

            // 添加新的作者
            int order = 1;
            for (String name : req.getAuthorNames()) {
                if (name == null || name.isBlank()) continue;
                Author found = authorMapper.findByName(name.trim());
                if (found == null) {
                    Author a = new Author();
                    a.setAuthorId(genId("A", 10));
                    a.setAuthorName(name.trim());
                    a.setNationality(null);
                    a.setBirthDate(null);
                    a.setBiography(null);
                    a.setCreateTime(LocalDateTime.now());
                    authorMapper.insert(a);
                    found = a;
                }
                bookRelationMapper.insertBookAuthor(isbn, found.getAuthorId(), order);
                order++;
                if (order > 4) break;
            }
        }

        // 更新分类关系（先删除旧的，再添加新的）
        if (req.getCategoryIds() != null) {
            bookRelationMapper.deleteBookCategories(isbn);
            for (String categoryId : req.getCategoryIds()) {
                if (categoryId != null && !categoryId.isEmpty()) {
                    bookRelationMapper.insertBookCategory(isbn, categoryId);
                }
            }
        }

        return ApiResponse.success(book);
    }

    @Transactional
    public ApiResponse<String> deleteBook(String isbn) {
        // 检查图书是否存在
        Book book = bookMapper.findByIsbn(isbn);
        if (book == null) {
            return ApiResponse.error(404, "图书不存在");
        }

        // 检查是否有待处理订单（防止删除有订单的图书）
        // 这里简化处理，直接软删除

        // 软删除：将状态设为 out_of_print
        bookMapper.softDelete(isbn);

        // 同时将库存清零
        inventoryMapper.clearInventory(isbn);

        return ApiResponse.success("图书已删除");
    }
}
