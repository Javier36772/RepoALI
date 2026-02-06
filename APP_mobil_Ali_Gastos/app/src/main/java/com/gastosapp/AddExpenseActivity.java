package com.gastosapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gastosapp.network.ApiService;
import com.gastosapp.network.RetrofitClient;
import com.gastosapp.network.model.ApiResponse;
import com.gastosapp.model.Expense;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etDescription;
    private EditText etAmount;
    private Spinner spinnerCategory;
    private EditText etDate;
    private Button btnSave;
    private boolean isLoading = false;
    private Calendar selectedDate;

    private static final String[] CATEGORIES = {
            "Selecciona una categoría",
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
        setContentView(R.layout.activity_add_expense);

        initViews();
        setupCategorySpinner();
        setupDatePicker();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> handleSave());

        // Set default date to today
        selectedDate = Calendar.getInstance();
        updateDateDisplay();
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
                        updateDateDisplay();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        etDate.setFocusable(false);
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void handleSave() {
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        String date = etDate.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa una descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoryPosition == 0) {
            Toast.makeText(this, "Por favor selecciona una categoría", Toast.LENGTH_SHORT).show();
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

        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setCategory(CATEGORIES[categoryPosition]);
        expense.setDate(date);

        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.createExpense(expense).enqueue(new retrofit2.Callback<ApiResponse<Expense>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<Expense>> call,
                    retrofit2.Response<ApiResponse<Expense>> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(AddExpenseActivity.this, "¡Gasto creado exitosamente!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(AddExpenseActivity.this, "Sesión expirada", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddExpenseActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddExpenseActivity.this, "Error al guardar el gasto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponse<Expense>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AddExpenseActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        btnSave.setEnabled(!loading);
        btnSave.setText(loading ? "Guardando..." : "Guardar");
        etDescription.setEnabled(!loading);
        etAmount.setEnabled(!loading);
        spinnerCategory.setEnabled(!loading);
        etDate.setEnabled(!loading);
    }
}
