package com.gastosapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gastosapp.network.ApiService;
import com.gastosapp.network.RetrofitClient;
import com.gastosapp.network.model.ApiResponse;
import com.gastosapp.model.Expense;
import android.content.Intent;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpenseDetailActivity extends AppCompatActivity {

    private Expense expense;
    private boolean isEditing = false;
    private boolean isLoading = false;
    private Calendar selectedDate;

    // View mode views
    private LinearLayout viewModeLayout;
    private TextView tvDescription;
    private TextView tvAmount;
    private TextView tvCategory;
    private TextView tvDate;
    private Button btnEdit;
    private Button btnDelete;

    // Edit mode views
    private LinearLayout editModeLayout;
    private EditText etDescription;
    private EditText etAmount;
    private Spinner spinnerCategory;
    private EditText etDate;
    private Button btnSave;
    private Button btnCancel;

    private static final String[] CATEGORIES = {
            "Alimentos",
            "Transporte",
            "Entretenimiento",
            "Salud",
            "Servicios",
            "Otros"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        expense = (Expense) getIntent().getSerializableExtra("expense");
        if (expense == null) {
            finish();
            return;
        }

        initViews();
        setupCategorySpinner();
        setupDatePicker();
        displayExpense();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // View mode
        viewModeLayout = findViewById(R.id.viewModeLayout);
        tvDescription = findViewById(R.id.tvDescription);
        tvAmount = findViewById(R.id.tvAmount);
        tvCategory = findViewById(R.id.tvCategory);
        tvDate = findViewById(R.id.tvDate);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Edit mode
        editModeLayout = findViewById(R.id.editModeLayout);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnEdit.setOnClickListener(v -> switchToEditMode());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnSave.setOnClickListener(v -> handleUpdate());
        btnCancel.setOnClickListener(v -> switchToViewMode());

        selectedDate = Calendar.getInstance();
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        etDate.setText(sdf.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        etDate.setFocusable(false);
    }

    private void displayExpense() {
        tvDescription.setText(expense.getDescription());
        tvAmount.setText(formatCurrency(expense.getAmount()));
        tvCategory.setText(expense.getCategory());
        tvDate.setText(formatDateLong(expense.getDate()));
    }

    private void switchToEditMode() {
        isEditing = true;
        viewModeLayout.setVisibility(View.GONE);
        editModeLayout.setVisibility(View.VISIBLE);

        // Populate edit fields
        etDescription.setText(expense.getDescription());
        etAmount.setText(String.valueOf(expense.getAmount()));

        // Set category
        int categoryIndex = Arrays.asList(CATEGORIES).indexOf(expense.getCategory());
        if (categoryIndex >= 0) {
            spinnerCategory.setSelection(categoryIndex);
        }

        // Set date
        etDate.setText(expense.getDate());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(expense.getDate());
            if (date != null) {
                selectedDate.setTime(date);
            }
        } catch (ParseException e) {
            // Use current date if parse fails
        }
    }

    private void switchToViewMode() {
        isEditing = false;
        editModeLayout.setVisibility(View.GONE);
        viewModeLayout.setVisibility(View.VISIBLE);
    }

    private void handleUpdate() {
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = CATEGORIES[spinnerCategory.getSelectedItemPosition()];
        String date = etDate.getText().toString();

        if (description.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "El monto debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(date);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.updateExpense(expense.getId(), expense).enqueue(new retrofit2.Callback<ApiResponse<Expense>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<Expense>> call,
                    retrofit2.Response<ApiResponse<Expense>> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ExpenseDetailActivity.this, "¡Gasto actualizado exitosamente!", Toast.LENGTH_SHORT)
                            .show();
                    displayExpense();
                    switchToViewMode();
                } else {
                    Toast.makeText(ExpenseDetailActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponse<Expense>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ExpenseDetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar gasto?")
                .setMessage("Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> handleDelete())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void handleDelete() {
        setLoading(true);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.deleteExpense(expense.getId()).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<Void>> call,
                    retrofit2.Response<ApiResponse<Void>> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ExpenseDetailActivity.this, "¡Gasto eliminado exitosamente!", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } else {
                    Toast.makeText(ExpenseDetailActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponse<Void>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ExpenseDetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        btnSave.setEnabled(!loading);
        btnSave.setText(loading ? "Guardando..." : "Guardar");
        btnDelete.setEnabled(!loading);
        btnEdit.setEnabled(!loading);
        btnCancel.setEnabled(!loading);
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        return format.format(amount);
    }

    private String formatDateLong(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }
}
