package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(showNotification: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "conversation") {
        composable("conversation") {
            ConversationScreen(navController, SampleData.conversationSample)
        }
        composable("other") {
            OtherScreen(navController, showNotification)
        }
    }
}
