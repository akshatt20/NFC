package com.example.hce

data class LoginResponse(
    val message: String,
    val employee_id: String?,
    val uid: String?,
    val key: String?,
    val email: String?,
    val status: String?,
    val dob: String?,
    val phone: String?,
    val desigination: String?,
    val department: String?
)
