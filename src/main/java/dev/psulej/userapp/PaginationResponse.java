package dev.psulej.userapp;

import java.util.List;

public class PaginationResponse<T> {

    private final long totalItems;
    private final long totalPages;
    private final int currentPage;
    private final List<T> items;

    public PaginationResponse(long totalItems, long totalPages, int currentPage, List<T> items) {
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.items = items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<T> getItems() {
        return items;
    }
}
