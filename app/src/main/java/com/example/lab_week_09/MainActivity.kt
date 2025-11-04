package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    SimpleApp(navController = navController)
                }
            }
        }
    }
}

data class Student(
    var name: String
)
private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var sharedStudentData = ""
var sharedStudentJson = ""

@Composable
fun SimpleApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            SimpleHome(
                onNavigate = { data, jsonData ->
                    sharedStudentData = data
                    sharedStudentJson = jsonData
                    navController.navigate("result")
                }
            )
        }
        composable("result") {
            SimpleResultContent(data = sharedStudentData, jsonData = sharedStudentJson)
        }
    }
}

@Composable
fun SimpleHome(onNavigate: (String, String) -> Unit) {
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }
    val inputField = remember { mutableStateOf(Student("")) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))
        TextField(
            value = inputField.value.name,
            onValueChange = { newText ->
                inputField.value = inputField.value.copy(name = newText)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = { OnBackgroundItemText(text = "Enter student name") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            PrimaryTextButton(text = stringResource(id = R.string.button_click)) {
                val trimmedName = inputField.value.name.trim()
                if (trimmedName.isNotEmpty() && trimmedName.isNotBlank()) {
                    listData.add(Student(trimmedName))
                    inputField.value = Student("")
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryTextButton(text = stringResource(id = R.string.button_navigate)) {
                val jsonData = convertToJson(listData)
                onNavigate(listData.toString(), jsonData)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OnBackgroundItemText(text = "Total Students: ${listData.size}")

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(listData) { student ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OnBackgroundItemText(text = student.name)
                }
            }
        }
    }
}

@Composable
fun SimpleResultContent(data: String, jsonData: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText(text = "RESULT CONTENT")

        Spacer(modifier = Modifier.height(16.dp))

        OnBackgroundTitleText(text = "Original Data:")
        OnBackgroundItemText(text = data)

        Spacer(modifier = Modifier.height(24.dp))
        OnBackgroundTitleText(text = "JSON Data (Moshi):")
        OnBackgroundItemText(text = jsonData)

        Spacer(modifier = Modifier.height(24.dp))

        OnBackgroundTitleText(text = "Parsed from JSON:")
        val parsedList = parseFromJson(jsonData)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(parsedList) { student ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OnBackgroundItemText(text = "• ${student.name}")
                }
            }
        }
    }
}
private fun convertToJson(studentList: List<Student>): String {
    return try {
        val type = Types.newParameterizedType(List::class.java, Student::class.java)
        val adapter = moshi.adapter<List<Student>>(type)
        adapter.toJson(studentList) ?: "[]"
    } catch (e: Exception) {
        "[]"
    }
}
private fun parseFromJson(json: String): List<Student> {
    return try {
        val type = Types.newParameterizedType(List::class.java, Student::class.java)
        val adapter = moshi.adapter<List<Student>>(type)
        adapter.fromJson(json) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleHome() {
    LAB_WEEK_09Theme {
        SimpleHome(onNavigate = { _, _ -> })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleResultContent() {
    LAB_WEEK_09Theme {
        SimpleResultContent(
            data = "[Student(name=Tanu), Student(name=Tina), Student(name=Tono)]",
            jsonData = "[{\"name\":\"Tanu\"},{\"name\":\"Tina\"},{\"name\":\"Tono\"}]"
        )
    }
}