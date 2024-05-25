package com.lomolo.giggy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // PostHog product analytics setup
        val posthogConfig = PostHogAndroidConfig(
            apiKey = POSTHOG_API_KEY,
            host = POSTHOG_HOST,
        )
        PostHogAndroid.setup(this, posthogConfig)

        enableEdgeToEdge()
        setContent {
            GiggyTheme {
                Scaffold {innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        GiggyApplication()
                    }
                }
            }
        }
    }

    companion object {
        const val POSTHOG_API_KEY=BuildConfig.POSTHOG_PROJECT_API_KEY
        const val POSTHOG_HOST=BuildConfig.POSTHOG_API_HOST
    }
}