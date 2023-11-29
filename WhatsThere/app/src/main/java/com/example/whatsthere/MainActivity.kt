package com.example.whatsthere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whatsthere.ui.ChatListScreen
import com.example.whatsthere.ui.LoginScreen
import com.example.whatsthere.ui.ProfileScreen
import com.example.whatsthere.ui.SignUpScreen
import com.example.whatsthere.ui.SingleChatScreen
import com.example.whatsthere.ui.SingleStatusScreen
import com.example.whatsthere.ui.StatusListScreen
import com.example.whatsthere.ui.theme.WhatsThereTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(val route: String) {
    object Signup : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatList")
    object SingleChat : DestinationScreen("singleChat/{chatId}") {
        fun createRoute(id: String) = "singleChat/$id"
    }
    object StatusList : DestinationScreen("statusList")
    object SingleStatus : DestinationScreen("singleStatus/{statusId}") {
        fun createRoute(id: String?) = "status/$id"
    }
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatsThereTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    ChatAppNavigation()

                }
            }
        }
    }
}

@Composable
fun ChatAppNavigation(){
    val navController = rememberNavController()
    val vm = hiltViewModel<CAViewModel>()

    NotificationMessage(vm = vm)
    
    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route){
        composable(DestinationScreen.Signup.route){
            SignUpScreen(navController, vm)
        }
        composable(DestinationScreen.Login.route){
            LoginScreen(navController, vm)
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.StatusList.route) {
            StatusListScreen(navController = navController)
        }
        composable(DestinationScreen.SingleStatus.route){
            SingleStatusScreen(statusId = "123")
        }
        composable(DestinationScreen.ChatList.route) {
            ChatListScreen(navController = navController)
        }
        composable(DestinationScreen.SingleChat.route){
            SingleChatScreen(chatId = "123")
        }
        
    }


}