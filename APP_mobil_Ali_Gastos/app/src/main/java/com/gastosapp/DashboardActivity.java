package com.gastosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gastosapp.adapter.ExpenseAdapter;
import com.gastosapp.network.ApiService;
import com.gastosapp.network.RetrofitClient;
import com.gastosapp.network.model.SummaryResponse;
import com.gastosapp.model.Expense;
import com.gastosapp.model.User;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener {

    private User currentUser;
    private TextView tvUserName;
    private TextView tvTotalMonth;
    private RecyclerView rvRecentExpenses;
    private View emptyView;
    private ProgressBar progressBar;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) {
            // Redirect to welcome if no user
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Refresh data when returning to this screen
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvTotalMonth = findViewById(R.id.tvTotalMonth);
        rvRecentExpenses = findViewById(R.id.rvRecentExpenses);
        emptyView = findViewById(R.id.emptyView);
        progressBar = findViewById(R.id.progressBar);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        Button btnViewAll = findViewById(R.id.btnViewAll);

        tvUserName.setText(currentUser.getName());

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            startActivity(intent);
        });

        btnViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExpensesListActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(this);
        rvRecentExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvRecentExpenses.setAdapter(adapter);
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getSummary().enqueue(new retrofit2.Callback<SummaryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SummaryResponse> call, retrofit2.Response<SummaryResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    SummaryResponse summaryResponse = response.body();
                    if (summaryResponse.isSuccess()) {
                        SummaryResponse.Data data = summaryResponse.getData();

                        // El API ya devuelve el total formateado como string o decimal
                        String totalStr = data.getCurrentMonthTotal();
                        tvTotalMonth.setText("$" + totalStr);

                        List<Expense> expenses = data.getRecentExpenses();
                        adapter.setExpenses(expenses);

                        if (expenses.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            rvRecentExpenses.setVisibility(View.GONE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            rvRecentExpenses.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (response.code() == 401) {
                    // Token inválido o expirado
                    Toast.makeText(DashboardActivity.this, "Sesión expirada", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SummaryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
