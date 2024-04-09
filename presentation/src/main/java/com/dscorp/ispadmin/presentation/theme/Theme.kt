import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dscorp.gigafiberperu.ui.theme.Background
import com.dscorp.gigafiberperu.ui.theme.Error
import com.dscorp.gigafiberperu.ui.theme.ErrorContainer
import com.dscorp.gigafiberperu.ui.theme.InverseOnSurface
import com.dscorp.gigafiberperu.ui.theme.InversePrimary
import com.dscorp.gigafiberperu.ui.theme.InverseSurface
import com.dscorp.gigafiberperu.ui.theme.OnBackground
import com.dscorp.gigafiberperu.ui.theme.OnError
import com.dscorp.gigafiberperu.ui.theme.OnErrorContainer
import com.dscorp.gigafiberperu.ui.theme.OnPrimary
import com.dscorp.gigafiberperu.ui.theme.OnPrimaryContainer
import com.dscorp.gigafiberperu.ui.theme.OnSecondary
import com.dscorp.gigafiberperu.ui.theme.OnSecondaryContainer
import com.dscorp.gigafiberperu.ui.theme.OnSurface
import com.dscorp.gigafiberperu.ui.theme.OnSurfaceVariant
import com.dscorp.gigafiberperu.ui.theme.OnTertiary
import com.dscorp.gigafiberperu.ui.theme.OnTertiaryContainer
import com.dscorp.gigafiberperu.ui.theme.Outline
import com.dscorp.gigafiberperu.ui.theme.OutlineVariant
import com.dscorp.gigafiberperu.ui.theme.Primary
import com.dscorp.gigafiberperu.ui.theme.PrimaryContainer
import com.dscorp.gigafiberperu.ui.theme.Scrim
import com.dscorp.gigafiberperu.ui.theme.Secondary
import com.dscorp.gigafiberperu.ui.theme.SecondaryContainer
import com.dscorp.gigafiberperu.ui.theme.Surface
import com.dscorp.gigafiberperu.ui.theme.SurfaceTint
import com.dscorp.gigafiberperu.ui.theme.SurfaceVariant
import com.dscorp.gigafiberperu.ui.theme.Tertiary
import com.dscorp.gigafiberperu.ui.theme.TertiaryContainer

val myColorScheme = ColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = SurfaceTint,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = Outline,
    outlineVariant = OutlineVariant,
    scrim = Scrim
)

@Composable
fun MyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = myColorScheme,
        typography = myTypography,
        content = content
    )
}

fun setStatusBarLightOrDark(window: Window, isLightMode: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = window.decorView.systemUiVisibility
        if (isLightMode) {
            // Light status bar (dark icons and text)
            window.decorView.systemUiVisibility =
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            // Dark status bar (light icons and text)
            window.decorView.systemUiVisibility =
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}
