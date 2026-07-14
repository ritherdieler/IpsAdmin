# Keep Compose internals used by ObservabilityComposeGestureLocator / BoundsHelper
-keepclassmembers class androidx.compose.ui.node.LayoutNode {
    *** layoutDelegate;
}
-keepclassmembers class androidx.compose.ui.platform.TestTagElement {
    *** tag;
}
-keep class androidx.compose.ui.node.Owner { *; }
-keep class androidx.compose.ui.node.LayoutNode { *; }
-keep class androidx.compose.ui.platform.TestTagElement { *; }
