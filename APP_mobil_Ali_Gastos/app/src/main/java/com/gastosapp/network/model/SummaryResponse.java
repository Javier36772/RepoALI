package com.gastosapp.network.model;

import java.util.List;
import com.gastosapp.model.Expense;

public class SummaryResponse {
    private boolean success;
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private String currentMonthTotal;
        private List<CategorySummary> categoryBreakdown;
        private List<Expense> recentExpenses;
        private String allTimeTotal;

        public String getCurrentMonthTotal() {
            return currentMonthTotal;
        }

        public List<CategorySummary> getCategoryBreakdown() {
            return categoryBreakdown;
        }

        public List<Expense> getRecentExpenses() {
            return recentExpenses;
        }

        public String getAllTimeTotal() {
            return allTimeTotal;
        }
    }

    public static class CategorySummary {
        private String category;
        private int expense_count;
        private String total_amount;

        public String getCategory() {
            return category;
        }

        public int getExpense_count() {
            return expense_count;
        }

        public String getTotal_amount() {
            return total_amount;
        }
    }
}
