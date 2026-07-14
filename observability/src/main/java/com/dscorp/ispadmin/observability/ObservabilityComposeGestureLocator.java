package com.dscorp.ispadmin.observability;

import android.view.View;
import android.view.ViewGroup;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.geometry.Rect;
import androidx.compose.ui.layout.ModifierInfo;
import androidx.compose.ui.node.LayoutNode;
import androidx.compose.ui.node.Owner;
import androidx.compose.ui.semantics.SemanticsConfiguration;
import androidx.compose.ui.semantics.SemanticsModifier;
import androidx.compose.ui.semantics.SemanticsPropertyKey;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("KotlinInternalInJava")
public final class ObservabilityComposeGestureLocator {

  private ObservabilityComposeGestureLocator() {}

  private static final ObservabilityComposeBoundsHelper BOUNDS = new ObservabilityComposeBoundsHelper();

  @Nullable
  public static String locate(@Nullable View root, float x, float y) {
    if (root == null) {
      return null;
    }
    try {
      View composeHost = findComposeHost(root);
      if (!(composeHost instanceof Owner)) {
        return null;
      }
      return locateInOwner((Owner) composeHost, x, y);
    } catch (Throwable ignored) {
      return null;
    }
  }

  @Nullable
  private static String locateInOwner(@NotNull Owner owner, float x, float y) {
    Queue<LayoutNode> queue = new LinkedList<>();
    queue.add(owner.getRoot());

    String targetTag = null;
    String lastKnownTag = null;
    String deepestTag = null;

    while (!queue.isEmpty()) {
      LayoutNode node = queue.poll();
      if (node == null) {
        continue;
      }

      if (node.isPlaced() && contains(node, x, y)) {
        boolean isInteractive = false;
        boolean nodeDefinesTag = false;
        List<ModifierInfo> modifiers = node.getModifierInfo();
        for (ModifierInfo modifierInfo : modifiers) {
          Modifier modifier = modifierInfo.getModifier();
          if (modifier instanceof SemanticsModifier) {
            SemanticsConfiguration config =
                ((SemanticsModifier) modifier).getSemanticsConfiguration();
            for (Map.Entry<? extends SemanticsPropertyKey<?>, ?> entry : config) {
              String key = entry.getKey().getName();
              if (ObservabilityComposeTagReader.INSTANCE.isInteractiveActionName(key)) {
                isInteractive = true;
              }
              String tag =
                  ObservabilityComposeTagReader.INSTANCE.tagFromSemanticsName(key, entry.getValue());
              if (tag != null) {
                lastKnownTag = tag;
                nodeDefinesTag = true;
              }
            }
          } else {
            String type = modifier.getClass().getCanonicalName();
            if (ObservabilityComposeTagReader.INSTANCE.isInteractiveModifierClass(type)) {
              isInteractive = true;
            } else if (ObservabilityComposeTagReader.INSTANCE.isTestTagElementClass(type)) {
              String tag = readTestTagElement(modifier);
              if (tag != null) {
                lastKnownTag = tag;
                nodeDefinesTag = true;
              }
            }
          }
        }
        if (nodeDefinesTag) {
          deepestTag = lastKnownTag;
        }
        if (isInteractive) {
          targetTag = lastKnownTag;
        }
      }
      queue.addAll(node.getZSortedChildren().asMutableList());
    }
    return targetTag != null ? targetTag : deepestTag;
  }

  private static boolean contains(@NotNull LayoutNode node, float x, float y) {
    Rect bounds = BOUNDS.boundsInWindow(node);
    if (bounds == null) {
      return false;
    }
    return x >= bounds.getLeft()
        && x <= bounds.getRight()
        && y >= bounds.getTop()
        && y <= bounds.getBottom();
  }

  @Nullable
  private static String readTestTagElement(@NotNull Modifier modifier) {
    try {
      Field tagField = modifier.getClass().getDeclaredField("tag");
      tagField.setAccessible(true);
      Object value = tagField.get(modifier);
      if (value instanceof String) {
        String tag = (String) value;
        return tag.isEmpty() ? null : tag;
      }
    } catch (Throwable ignored) {
      // ignored
    }
    return null;
  }

  @Nullable
  private static View findComposeHost(@Nullable View view) {
    View current = view;
    while (current != null) {
      if (current instanceof Owner
          || current.getClass().getName().contains("AndroidComposeView")) {
        return current;
      }
      Object parent = current.getParent();
      current = parent instanceof View ? (View) parent : null;
    }
    return findComposeHostInTree(view != null ? view.getRootView() : null);
  }

  @Nullable
  private static View findComposeHostInTree(@Nullable View root) {
    if (root == null) {
      return null;
    }
    if (root instanceof Owner || root.getClass().getName().contains("AndroidComposeView")) {
      return root;
    }
    if (root instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) root;
      for (int i = 0; i < group.getChildCount(); i++) {
        View found = findComposeHostInTree(group.getChildAt(i));
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }
}
