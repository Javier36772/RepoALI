package com.gastosapp.network.model;

import java.util.List;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Pagination pagination;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public static class Pagination {
        private int total;
        private int limit;
        private int offset;
        private boolean hasMore;

        public int getTotal() {
            return total;
        }

        public int getLimit() {
            return limit;
        }

        public int getOffset() {
            return offset;
        }

        public boolean isHasMore() {
            return hasMore;
        }
    }
}
