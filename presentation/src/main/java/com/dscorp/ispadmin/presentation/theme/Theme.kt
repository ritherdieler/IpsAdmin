import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dscorp.ispadmin.presentation.theme.Background
import com.dscorp.ispadmin.presentation.theme.Error
import com.dscorp.ispadmin.presentation.theme.ErrorContainer
import com.dscorp.ispadmin.presentation.theme.InverseOnSurface
import com.dscorp.ispadmin.presentation.theme.InversePrimary
import com.dscorp.ispadmin.presentation.theme.InverseSurface
import com.dscorp.ispadmin.presentation.theme.OnBackground
import com.dscorp.ispadmin.presentation.theme.OnError
import com.dscorp.ispadmin.presentation.theme.OnErrorContainer
import com.dscorp.ispadmin.presentation.theme.OnPrimary
import com.dscorp.ispadmin.presentation.theme.OnPrimaryContainer
import com.dscorp.ispadmin.presentation.theme.OnSecondary
import com.dscorp.ispadmin.presentation.theme.OnSecondaryContainer
import com.dscorp.ispadmin.presentation.theme.OnSurface
import com.dscorp.ispadmin.presentation.theme.OnSurfaceVariant
import com.dscorp.ispadmin.presentation.theme.OnTertiary
import com.dscorp.ispadmin.presentation.theme.OnTertiaryContainer
import com.dscorp.ispadmin.presentation.theme.Outline
import com.dscorp.ispadmin.presentation.theme.OutlineVariant
import com.dscorp.ispadmin.presentation.theme.Primary
import com.dscorp.ispadmin.presentation.theme.PrimaryContainer
import com.dscorp.ispadmin.presentation.theme.Scrim
import com.dscorp.ispadmin.presentation.theme.Secondary
import com.dscorp.ispadmin.presentation.theme.SecondaryContainer
import com.dscorp.ispadmin.presentation.theme.Surface
import com.dscorp.ispadmin.presentation.theme.SurfaceTint
import com.dscorp.ispadmin.presentation.theme.SurfaceVariant
import com.dscorp.ispadmin.presentation.theme.Tertiary
import com.dscorp.ispadmin.presentation.theme.TertiaryContainer

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
