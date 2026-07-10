package tv.kinovio.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import tv.kinovio.app.data.SettingsStore
import tv.kinovio.app.ui.KinovioApp
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installCrashCapture()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF6AE4FF),
                    secondary = Color(0xFF9D4DFF),
                    background = Color(0xFF04070F),
                    surface = Color(0xFF0D1A36)
                )
            ) {
                KinovioApp()
            }
        }
    }

    private fun installCrashCapture() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                val writer = StringWriter()
                throwable.printStackTrace(PrintWriter(writer))
                SettingsStore(applicationContext).saveLastCrash(
                    "Thread: ${thread.name}\n${writer}"
                )
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }
}
