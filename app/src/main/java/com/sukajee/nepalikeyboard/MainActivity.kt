package com.sukajee.nepalikeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text

import com.sukajee.nepalikeyboard.core.ui.theme.NepaliKeyboardTheme
import com.sukajee.nepalikeyboard.feature.settings.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NepaliKeyboardTheme {
                SettingsScreen()
            }
        }
    }
}