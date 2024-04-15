
package com.example.hce

import android.content.Context
import android.content.Intent
import android.net.ParseException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.text.SimpleDateFormat
import android.widget.TextView
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var emp: TextView
    private lateinit var name: TextView
    private lateinit var des: TextView
    private lateinit var dept: TextView
    private lateinit var user: TextView
    private lateinit var number: TextView
    private lateinit var dob: TextView
    private lateinit var logoutB:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize TextViews
        emp = findViewById(R.id.emp)
        name = findViewById(R.id.name)
        des = findViewById(R.id.des)
        dept = findViewById(R.id.dept)
        user = findViewById(R.id.user)
        number = findViewById(R.id.number)
        dob = findViewById(R.id.dob)
        logoutB = findViewById(R.id.logoutB)

        // Retrieve data from SharedPreferences
        val sharedPref = applicationContext.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
        val empValue = sharedPref.getString("employee_id", "")
        val nameValue = sharedPref.getString("name", "")
        val desValue = sharedPref.getString("desigination", "")
        val deptValue = sharedPref.getString("department", "")
        val userValue = sharedPref.getString("email", "")
        val numberValue = sharedPref.getString("phone", "")
        val dobValue = sharedPref.getString("dob", "")

        // Set data to TextViews
        emp.text = "Employee ID: $empValue"
        name.text = "Name: $nameValue"
        des.text = "Designation: $desValue"
        dept.text = "Department: $deptValue"
        user.text = "User: $userValue"
        number.text = "Phone Number: $numberValue"

        // Check if dobValue is not empty or null before parsing
        if (!dobValue.isNullOrEmpty()) {
            try {
                // Format date of birth
                val dobDate = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.getDefault()).parse(dobValue)
                val formattedDob = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dobDate)
                dob.text = "Date of Birth: $formattedDob"
            } catch (e: ParseException) {
                // Handle parsing error
                e.printStackTrace()
                // Set an appropriate message or handle the error as per your requirement
                dob.text = "Date of Birth: N/A"
            }
        } else {
            // Set an appropriate message for empty or null dobValue
            dob.text = "Date of Birth: N/A"
        }

        logoutB.setOnClickListener {
            val sharedPref = applicationContext.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            // Navigate to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Finish the current activity
            finish()
        }
    }

}
