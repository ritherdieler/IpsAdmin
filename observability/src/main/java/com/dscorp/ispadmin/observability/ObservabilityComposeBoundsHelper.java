package com.dscorp.ispadmin.observability;

import androidx.compose.ui.geometry.Rect;
import androidx.compose.ui.layout.LayoutCoordinatesKt;
import androidx.compose.ui.node.LayoutNode;
import androidx.compose.ui.node.LayoutNodeLayoutDelegate;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("KotlinInternalInJava")
final class ObservabilityComposeBoundsHelper {

  private Field layoutDelegateField;

  ObservabilityComposeBoundsHelper() {
    try {
      Class<?> clazz = Class.forName("androidx.compose.ui.node.LayoutNode");
      layoutDelegateField = clazz.getDeclaredField("layoutDelegate");
      layoutDelegateField.setAccessible(true);
    } catch (Exception ignored) {
      layoutDelegateField = null;
    }
  }

  @Nullable
  Rect boundsInWindow(@NotNull LayoutNode node) {
    if (layoutDelegateField == null) {
      return null;
    }
    try {
      LayoutNodeLayoutDelegate delegate =
          (LayoutNodeLayoutDelegate) layoutDelegateField.get(node);
      if (delegate == null) {
        return null;
      }
      return LayoutCoordinatesKt.boundsInWindow(delegate.getOuterCoordinator().getCoordinates());
    } catch (Throwable ignored) {
      return null;
    }
  }
}
