package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

data class Message(val author: String, val body: String)

@Preview(name = "Light Mode")
@Composable
fun PreviewMessageCard() {
    MyApplicationTheme {
        Surface {
            MessageCard(msg = Message("Fanis", "Hey, take a look at my new monitor!"))
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    MyApplicationTheme {
        AppNavigation()
    }
}