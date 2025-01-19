package com.example.cgpacalculatorapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cgpacalculatorapp.ui.theme.CGPACALCULATORAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CGPACALCULATORAPPTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "input_screen") {
        composable("input_screen") { Cgpa(navController) }
        composable("result_screen/{cgpa}") { backStackEntry ->
            val cgpa = backStackEntry.arguments?.getString("cgpa") ?: "0.00"
            CgpaResultScreen(cgpa = cgpa)
        }
    }
}

@Composable
fun Cgpa(navController: NavController) {
    var subjects by remember { mutableStateOf(mutableListOf<String>()) }
    var grades by remember { mutableStateOf(mutableListOf<String>()) }
    var credits by remember { mutableStateOf(mutableListOf<String>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA)) // Light Blue background
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "CGPA Calculator \n Chandigarh University",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(fontSize = 22.sp, color = Color.Black, fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
            item { Divider(color = Color.Gray, thickness = 1.dp) }

            // Dynamically add subjects and their fields
            for (index in subjects.indices) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            SubjectText(subject = subjects[index])
                            Spacer(modifier = Modifier.height(10.dp))
                            GradeTextField(
                                grade = grades.getOrElse(index) { "" },
                                onValueChange = { newGrade ->
                                    grades = grades.toMutableList().apply { set(index, newGrade) }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            CreditTextField(
                                credit = credits.getOrElse(index) { "" },
                                onValueChange = { newCredit ->
                                    credits = credits.toMutableList().apply { set(index, newCredit) }
                                }
                            )
                        }
                    }
                }
            }

            // Button to add a new subject
            item {
                IconButton(
                    onClick = {
                        val newSubject = "Subject ${subjects.size + 1}"
                        subjects = subjects.toMutableList().apply { add(newSubject) }
                        grades = grades.toMutableList().apply { add("") }
                        credits = credits.toMutableList().apply { add("") }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Subject",
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF00796B)
                    )
                }
            }

            // Add the Calculate Button
            item {
                Button(
                    onClick = {
                        val cgpa = calculateCgpa(grades, credits)
                        Log.d("CGPA", "Calculated CGPA: $cgpa") // Log the calculated CGPA for debugging
                        navController.navigate("result_screen/$cgpa")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Calculate", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CgpaResultScreen(cgpa: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Your CGPA is: $cgpa",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
    }
}

@Composable
fun SubjectText(subject: String) {
    Text(
        text = subject,
        style = TextStyle(fontSize = 16.sp, color = Color.Black),
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeTextField(grade: String, onValueChange: (String) -> Unit) {
    TextField(
        value = grade,
        onValueChange = { text -> onValueChange(text) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        label = { Text(text = "Enter Grade", color = Color.White, fontSize = 12.sp) },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color(0xFF00796B)
        ),
        shape = RoundedCornerShape(15.dp),
        textStyle = TextStyle(fontSize = 12.sp, color = Color.White)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditTextField(credit: String, onValueChange: (String) -> Unit) {
    TextField(
        value = credit,
        onValueChange = { text -> onValueChange(text) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        label = { Text(text = "Enter Credit", color = Color.White, fontSize = 12.sp) },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color(0xFF004D40)
        ),
        shape = RoundedCornerShape(15.dp),
        textStyle = TextStyle(fontSize = 12.sp, color = Color.White)
    )
}

fun calculateCgpa(grades: List<String>, credits: List<String>): String {
    val gradePointsMap = mapOf(
        "A+" to 10f,
        "A" to 9f,
        "B+" to 8f,
        "B" to 7f,
        "C+" to 6f,
        "C" to 5f,
        "D" to 4f
    )

    val gradePoints = grades.map { grade -> gradePointsMap[grade.uppercase()] ?: 0f }
    val creditPoints = credits.map { it.toFloatOrNull() ?: 0f }
    val totalCredits = creditPoints.sum()

    val weightedSum = gradePoints.zip(creditPoints).fold(0f) { acc, pair ->
        acc + (pair.first * pair.second)
    }

    return if (totalCredits > 0) String.format("%.2f", weightedSum / totalCredits) else "0.00"
}

@Preview(showBackground = true)
@Composable
fun CgpaPreview() {
    CGPACALCULATORAPPTheme {
        AppNavigation()
    }
}
