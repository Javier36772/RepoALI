package com.gastosapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastosapp.adapter.ExpenseAdapter;
import com.gastosapp.network.ApiService;
import com.gastosapp.network.RetrofitClient;
import com.gastosapp.network.model.ApiResponse;
import com.gastosapp.model.Expense;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpensesListActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener {

    private EditText etSearch;
    private TextView tvTotal;
    private TextView tvCount;
    private RecyclerView rvExpenses;
    private View emptyView;
    private ProgressBar progressBar;
    private ExpenseAdapter adapter;
    private List<Expense> allExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);

        initViews();
        setupRecyclerView();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        tvTotal = findViewById(R.id.tvTotal);
        tvCount = findViewById(R.id.tvCount);
        rvExpenses = findViewById(R.id.rvExpenses);
        emptyView = findViewById(R.id.emptyView);
        progressBar = findViewById(R.id.progressBar);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        btnBack.setOnClickListener(v -> finish());

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExpenses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(this);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvExpenses.setAdapter(adapter);
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getExpenses(null, null, null, null, null, null)
                .enqueue(new retrofit2.Callback<ApiResponse<List<Expense>>>() {
                    @Override
                    public void onResponse(retrofit2.Call<ApiResponse<List<Expense>>> call,
                            retrofit2.Response<ApiResponse<List<Expense>>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            allExpenses = response.body().getData();
                            adapter.setExpenses(allExpenses);
                            updateSummary(allExpenses);
                            updateEmptyState(allExpenses.isEmpty());
                        } else if (response.code() == 401) {
                            Toast.makeText(ExpensesListActivity.this, "Sesión expirada", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ExpensesListActivity.this, LoginActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ApiResponse<List<Expense>>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ExpensesListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void filterExpenses(String query) {
        if (query.trim().isEmpty()) {
            adapter.setExpenses(allExpenses);
            updateSummary(allExpenses);
            updateEmptyState(allExpenses.isEmpty());
            return;
        }

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getExpenses(null, null, null, query, null, null)
                .enqueue(new retrofit2.Callback<ApiResponse<List<Expense>>>() {
                    @Override
                    public void onResponse(retrofit2.Call<ApiResponse<List<Expense>>> call,
                            retrofit2.Response<ApiResponse<List<Expense>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Expense> filtered = response.body().getData();
                            adapter.setExpenses(filtered);
                            updateSummary(filtered);
                            updateEmptyState(filtered.isEmpty());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ApiResponse<List<Expense>>> call, Throwable t) {
                        // Silently ignore search errors or show toast
                    }
                });
    }

    private void updateSummary(List<Expense> expenses) {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        tvTotal.setText(formatCurrency(total));

        int count = expenses.size();
        tvCount.setText(count + " " + (count == 1 ? "gasto" : "gastos"));
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            rvExpenses.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            rvExpenses.setVisibility(View.VISIBLE);
        }
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        return format.format(amount);
    }

    @Override
    public void onExpenseClick(Expense expense) {
        Intent intent = new Intent(this, ExpenseDetailActivity.class);
        intent.putExtra("expense", expense);
        startActivity(intent);
    }
}
