package com.gastosapp.network;

import com.gastosapp.network.model.ApiResponse;
import com.gastosapp.network.model.LoginRequest;
import com.gastosapp.network.model.LoginResponse;
import com.gastosapp.network.model.RegisterRequest;
import com.gastosapp.network.model.SummaryResponse;
import com.gastosapp.model.Expense;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("expenses")
    Call<ApiResponse<List<Expense>>> getExpenses(
            @Query("category") String category,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("search") String search,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset);

    @GET("expenses/summary")
    Call<SummaryResponse> getSummary();

    @POST("expenses")
    Call<ApiResponse<Expense>> createExpense(@Body Expense expense);

    @PUT("expenses/{id}")
    Call<ApiResponse<Expense>> updateExpense(@Path("id") String id, @Body Expense expense);

    @DELETE("expenses/{id}")
    Call<ApiResponse<Void>> deleteExpense(@Path("id") String id);

    @GET("categories")
    Call<ApiResponse<List<String>>> getCategories();
}
