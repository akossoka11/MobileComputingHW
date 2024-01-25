package com.example.myapplication

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun OtherScreen(navController: NavController) {
    // Composable for your other view
    // You can add a button to navigate back to the conversation screen
    Button(onClick = {
        // Navigate to "conversation" and pop the back stack up to that destination
        navController.navigate("conversation") {
            popUpTo("conversation") {
                inclusive = true
            }
        }
    }) {
        Text("Go back to Conversation Screen")
    }
}