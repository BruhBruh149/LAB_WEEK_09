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
var sharedStudentData = ""
@Composable
fun SimpleApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            SimpleHome(
                onNavigate = { data ->
                    sharedStudentData = data
                    navController.navigate("result")
                }
            )
        }
        composable("result") {
            SimpleResultContent(data = sharedStudentData)
        }
    }
}
@Composable
fun SimpleHome(onNavigate: (String) -> Unit) {
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            PrimaryTextButton(text = stringResource(id = R.string.button_click)) {
                val trimmedName = inputField.value.name.trim()
                if (trimmedName.isNotBlank()) {
                    listData.add(Student(trimmedName))
                    inputField.value = Student("") // reset input
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryTextButton(text = stringResource(id = R.string.button_navigate)) {
                onNavigate(listData.toString()) // pindah ke ResultContent
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(listData) { student ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OnBackgroundItemText(text = student.name)
                }
            }
        }
    }
}

@Composable
fun SimpleResultContent(data: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText(text = "RESULT CONTENT")
        OnBackgroundItemText(text = "Data from Home:")
        OnBackgroundItemText(text = data)
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewSimpleHome() {
    LAB_WEEK_09Theme {
        SimpleHome(onNavigate = { })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleResultContent() {
    LAB_WEEK_09Theme {
        SimpleResultContent(
            data = "[Student(name=Tanu), Student(name=Tina), Student(name=Tono)]"
        )
    }
}
