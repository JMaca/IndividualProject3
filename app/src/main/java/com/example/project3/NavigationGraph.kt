package com.example.project3

import BottomNavigationItems
import LoginScreen
import SignUpScreen
import SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onBottomBarVisibilityChanged: (Boolean) -> Unit
) {
    NavHost(navController, startDestination = BottomNavigationItems.Welcome.route) {
        composable(BottomNavigationItems.Welcome.route) {
            onBottomBarVisibilityChanged(false)
            SplashScreen(navController = navController)
        }
        composable(BottomNavigationItems.LogScreen.route) {
            onBottomBarVisibilityChanged(true)
            LoginScreen(navController = navController)
        }
        composable(BottomNavigationItems.SignupScreen.route) {
            onBottomBarVisibilityChanged(true)
            SignUpScreen(navController = navController)
        }
        composable(BottomNavigationItems.GameScreen.route) {
            onBottomBarVisibilityChanged(true)
            GameScreen(navController = navController)
        }
    }
}
