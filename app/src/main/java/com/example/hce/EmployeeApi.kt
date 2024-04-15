package com.example.hce

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface EmployeeApi {
    @POST("api/employees/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}