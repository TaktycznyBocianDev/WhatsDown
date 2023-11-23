package com.example.whatsthere.ui


import com.example.whatsthere.DestinationScreen
import com.example.whatsthere.R

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen){
    PROFILE(R.drawable.baseline_profile, DestinationScreen.Profile),
    CHATLIST(R.drawable.baseline_chat, DestinationScreen.ChatList),
    STATUSLIST(R.drawable.baseline_status, DestinationScreen.StatusList)
}