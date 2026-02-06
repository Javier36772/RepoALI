package com.gastosapp.data;

import com.gastosapp.model.Expense;
import com.gastosapp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Datos mock locales para pruebas.
 * TODO: Reemplazar con llamadas a API real cuando esté disponible.
 */
public class MockData {
    private static MockData instance;
    private User currentUser;
    private List<Expense> expenses;

    private MockData() {
        expenses = new ArrayList<>();
        initMockExpenses();
    }

    public static synchronized MockData getInstance() {
        if (instance == null) {
            instance = new MockData();
        }
        return instance;
    }

    private void initMockExpenses() {
        expenses.add(new Expense(
                UUID.randomUUID().toString(),
                "Comida en restaurante",
                250.00,
                "Alimentos",
                "2026-02-06",
                "user1"));
        expenses.add(new Expense(
                UUID.randomUUID().toString(),
                "Uber al trabajo",
                85.50,
                "Transporte",
                "2026-02-05",
                "user1"));
        expenses.add(new Expense(
                UUID.randomUUID().toString(),
                "Netflix mensual",
                199.00,
                "Entretenimiento",
                "2026-02-04",
                "user1"));
        expenses.add(new Expense(
                UUID.randomUUID().toString(),
                "Consulta médica",
                500.00,
                "Salud",
                "2026-02-03",
                "user1"));
        expenses.add(new Expense(
                UUID.randomUUID().toString(),
                "Luz del mes",
                450.00,
                "Servicios",
                "2026-02-02",
                "user1"));
        expenses.add(new Expense(
                UUID.randomUUID().toString(),
                "Compras supermercado",
                1200.00,
                "Alimentos",
                "2026-02-01",
                "user1"));
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Expense> getRecentExpenses(int limit) {
        int size = Math.min(limit, expenses.size());
        return new ArrayList<>(expenses.subList(0, size));
    }

    public double getTotalMonth() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    public void addExpense(Expense expense) {
        expense.setId(UUID.randomUUID().toString());
        expenses.add(0, expense);
    }

    public void updateExpense(Expense updatedExpense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(updatedExpense.getId())) {
                expenses.set(i, updatedExpense);
                break;
            }
        }
    }

    public void deleteExpense(String expenseId) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(expenseId)) {
                expenses.remove(i);
                break;
            }
        }
    }

    public List<Expense> searchExpenses(String query) {
        List<Expense> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Expense expense : expenses) {
            if (expense.getDescription().toLowerCase().contains(lowerQuery) ||
                    expense.getCategory().toLowerCase().contains(lowerQuery)) {
                filtered.add(expense);
            }
        }
        return filtered;
    }

    /**
     * Placeholder para login con API.
     * TODO: Implementar llamada real a API de autenticación.
     */
    public User mockLogin(String email, String password) {
        // Simulación de login exitoso
        if (email != null && !email.isEmpty() && password != null && password.length() >= 6) {
            User user = new User(
                    UUID.randomUUID().toString(),
                    email,
                    email.split("@")[0]);
            setCurrentUser(user);
            return user;
        }
        return null;
    }

    /**
     * Placeholder para registro con API.
     * TODO: Implementar llamada real a API de registro.
     */
    public boolean mockRegister(String name, String email, String password) {
        // Simulación de registro exitoso
        return name != null && !name.isEmpty() &&
                email != null && !email.isEmpty() &&
                password != null && password.length() >= 6;
    }

    public void logout() {
        currentUser = null;
    }
}
