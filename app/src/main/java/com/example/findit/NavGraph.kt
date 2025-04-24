package com.example.findit

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findit.MyTreasuresScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                userId = 1,
                onShowMap = { navController.navigate("map") },
                onAddTreasure = { navController.navigate("add") },
                onMyTreasures = { navController.navigate("myTreasures") },
                onFindTreasure = { navController.navigate("find") },
                onShowRanking = { navController.navigate("ranking") }//,
//                onShowOSM = { navController.navigate("openMap") }
            )
        }
        composable("map") {
            MapScreen(navController)
        }

        composable("add") {
            AddTreasureScreen(navController)
        }

        composable("myTreasures") {
            MyTreasuresScreen(navController)
        }

        composable("find") {
            FindTreasureScreen(navController)
        }

        composable("ranking") {
            RankingScreen(navController)
        }
//        composable("openMap") {
//            OpenStreetMapScreen(navController)
//        }

//     composable("camera?userId={userId}&treasureId={treasureId}") { backStackEntry ->
//    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
//    val treasureId = backStackEntry.arguments?.getString("treasureId")?.toIntOrNull() ?: return@composable
//    CameraCaptureScreen(navController, userId, treasureId)
//}
        composable(
            route = "camera?userId={userId}&treasureId={treasureId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("treasureId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            val treasureId = backStackEntry.arguments?.getInt("treasureId") ?: return@composable
            CameraCaptureScreen(navController, userId, treasureId)
        }



    }
}
