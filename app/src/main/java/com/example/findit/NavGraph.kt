package com.example.findit

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(), userId: Int) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                userId = userId,
                onShowMap = { navController.navigate("map") },
                onAddTreasure = { navController.navigate("add") },
                onMyTreasures = { navController.navigate("myTreasures") },
                onFindTreasure = { navController.navigate("find") },
                onShowRanking = { navController.navigate("ranking") }
            )
        }
        composable("map") {
            MapScreen(navController)
        }

        composable("add") {
            AddTreasureScreen(navController)
        }

        composable("myTreasures") {
            MyTreasuresScreen(navController, userId)
        }

        composable("find") {
            FindTreasureScreen(navController, userId)
        }

        composable("ranking") {
            RankingScreen(navController)
        }

        composable(
            route = "camera?userId={userId}&treasureId={treasureId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("treasureId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userIdParam = backStackEntry.arguments?.getInt("userId") ?: return@composable
            val treasureId = backStackEntry.arguments?.getInt("treasureId") ?: return@composable
            CameraCaptureScreen(navController, userIdParam, treasureId)
        }

        composable("addManual") { AddTreasureManualScreen(navController) }
        composable("addFromMap") { AddTreasureFromMapScreen(navController) }
    }
}
