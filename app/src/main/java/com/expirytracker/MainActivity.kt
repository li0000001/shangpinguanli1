package com.expirytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.expirytracker.ui.screens.AddProductScreen
import com.expirytracker.ui.screens.ProductListScreen
import com.expirytracker.ui.theme.ExpiryTrackerTheme
import com.expirytracker.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpiryTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpiryTrackerNavigation()
                }
            }
        }
    }
}

@Composable
fun ExpiryTrackerNavigation() {
    val navController = rememberNavController()
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as ExpiryTrackerApp
    
    val viewModel: ProductViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProductViewModel(application.repository) as T
            }
        }
    )
    
    NavHost(navController = navController, startDestination = "product_list") {
        composable("product_list") {
            ProductListScreen(
                viewModel = viewModel,
                onAddProduct = { navController.navigate("add_product") }
            )
        }
        
        composable("add_product") {
            AddProductScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
