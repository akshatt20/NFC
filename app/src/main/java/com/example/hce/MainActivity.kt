package com.example.hce

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var showUID: TextView
    private lateinit var showemployeeID: TextView
    private lateinit var showKey: TextView


    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if data exists in SharedPreferences
        val sharedPref = applicationContext.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("uid", null)
        val employeeId = sharedPref.getString("employee_id", null)
        val key = sharedPref.getString("key", null)
        // If data exists, redirect to ProfileActivity
        if (uid != null && employeeId != null && key != null) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish() // Optional, to prevent user from going back to MainActivity using back button
            return
        }

        // If no data exists, continue with MainActivity initialization
        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        showUID = findViewById(R.id.showUID)
        showemployeeID = findViewById(R.id.showemployeeID)
        showKey = findViewById(R.id.showKey)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginEmployee(email, password) { result ->
                    when (result) {
                        is Result.Success -> {
                            val loginResponse = result.data
                            // Assuming loginResponse contains uid and employeeId b
                            val editor = sharedPref.edit()
                            editor.putString("uid", loginResponse.uid)
                            editor.putString("key", loginResponse.key)
                            editor.putString("email", loginResponse.email)
                            editor.putString("status", loginResponse.status)
                            editor.putString("dob", loginResponse.dob)
                            editor.putString("phone", loginResponse.phone)
                            editor.putString("department", loginResponse.department)

                            editor.putString("desigination", loginResponse.desigination)
                            editor.putString("employee_id", loginResponse.employee_id)
                            editor.apply()

                            Log.d("MainActivity", "Login successful: ${loginResponse.message}")
                            // Redirect to ProfileActivity
                            val intent = Intent(this, ProfileActivity::class.java)
                            startActivity(intent)
                            finish() // Optional, to prevent user from going back to MainActivity using back button
                        }

                        is Result.Error -> {
                            val error = result.exception
                            Log.e("MainActivity", "Error: ${error.message}")
                        }
                        else -> {
                            // Handle any unexpected cases here
                            Log.e("MainActivity", "Unexpected case in 'when' expression")
                        }
                    }
                }
            }
        }
    }


    private fun loginEmployee(email: String, password: String, onResult: (Result<LoginResponse>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.149.190:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(EmployeeApi::class.java)
        val loginRequest = LoginRequest(email, password)

        api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@MainActivity,"Login succeus",Toast.LENGTH_LONG).show()

                    onResult(Result.Success(response.body()!!))
                } else {
                    onResult(Result.Error(Exception("Error: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(Result.Error(Exception("Error: ${t.message}")))
            }
        })
    }


}
