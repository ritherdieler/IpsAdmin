package com.dscorp.ispadmin.observability

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.accessibility.AccessibilityEvent
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import java.lang.ref.WeakReference

class ObservabilityUiTracker(
    private val uiCapture: ObservabilityUiCapture
) {

    private var trackedActivity: WeakReference<Activity>? = null
    private var originalCallback: Window.Callback? = null
    private var focusListener: ViewTreeFocusListener? = null
    private var accessibilityDelegate: View.AccessibilityDelegate? = null

    fun attach(activity: Activity) {
        detach()
        trackedActivity = WeakReference(activity)
        val window = activity.window
        val previous = window.callback ?: return
        originalCallback = previous
        val gestureDetector = GestureDetector(
            activity,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    emitTouch(activity, e, ObsUiEventType.CLICK)
                    return false
                }

                override fun onLongPress(e: MotionEvent) {
                    emitTouch(activity, e, ObsUiEventType.LONG_CLICK)
                }
            }
        )
        window.callback = object : Window.Callback by previous {
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(event)
                return previous.dispatchTouchEvent(event)
            }

            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                return previous.dispatchKeyEvent(event)
            }
        }

        val decor = window.decorView
        val listener = ViewTreeFocusListener()
        focusListener = listener
        decor.viewTreeObserver.addOnGlobalFocusChangeListener(listener)

        val a11yDelegate = object : View.AccessibilityDelegate() {
            override fun sendAccessibilityEventUnchecked(host: View, event: AccessibilityEvent) {
                emitAccessibilityTextChange(event)
                super.sendAccessibilityEventUnchecked(host, event)
            }

            override fun onRequestSendAccessibilityEvent(
                host: ViewGroup,
                child: View,
                event: AccessibilityEvent
            ): Boolean {
                emitAccessibilityTextChange(event)
                return super.onRequestSendAccessibilityEvent(host, child, event)
            }
        }
        accessibilityDelegate = a11yDelegate
        decor.accessibilityDelegate = a11yDelegate
    }

    fun detach() {
        val activity = trackedActivity?.get()
        val window = activity?.window
        val previous = originalCallback
        if (window != null && previous != null) {
            window.callback = previous
        }
        val decor = window?.decorView
        val listener = focusListener
        if (decor != null && listener != null) {
            decor.viewTreeObserver.removeOnGlobalFocusChangeListener(listener)
        }
        if (decor != null && accessibilityDelegate != null) {
            decor.accessibilityDelegate = null
        }
        trackedActivity = null
        originalCallback = null
        focusListener = null
        accessibilityDelegate = null
    }

    private fun emitAccessibilityTextChange(event: AccessibilityEvent) {
        val payload = ObservabilityAccessibilityText.fromEvent(
            eventType = event.eventType,
            text = event.text,
            contentDescription = event.contentDescription,
            className = event.className
        ) ?: return
        uiCapture.capture(
            type = ObsUiEventType.TEXT_CHANGE,
            target = payload.target,
            value = payload.value,
            data = mapOf("source" to "accessibility")
        )
    }

    private fun emitTouch(activity: Activity, event: MotionEvent, type: String) {
        val target = findTarget(activity.window.decorView, event.rawX, event.rawY)
        if (target is EditText) {
            maybeAttachTextWatcher(target)
        }
        val composeTag = ObservabilityComposeGestureLocator.locate(
            target ?: activity.window.decorView,
            event.rawX,
            event.rawY
        )
        val viewLabel = describe(target)
        val label = composeTag
            ?: viewLabel.takeUnless { ObservabilityComposeTarget.isGenericHost(it) }
            ?: viewLabel
        if (
            ObservabilityComposeTarget.isGenericHost(label) &&
            ObservabilityComposeTarget.isComposeHost(target?.javaClass?.name)
        ) {
            return
        }
        val value = when (target) {
            is CompoundButton -> target.isChecked.toString()
            is EditText -> target.text?.toString()
            else -> null
        }
        val resolvedType = when {
            target is CheckBox && type == ObsUiEventType.CLICK -> ObsUiEventType.CHECKBOX
            target is Switch && type == ObsUiEventType.CLICK -> ObsUiEventType.SWITCH
            else -> type
        }
        val data = LinkedHashMap<String, Any?>()
        data["x"] = event.rawX.toInt()
        data["y"] = event.rawY.toInt()
        data["class"] = target?.javaClass?.simpleName
        if (!composeTag.isNullOrBlank()) {
            data["tag"] = composeTag
            data["source"] = "compose"
        }
        uiCapture.capture(
            type = resolvedType,
            target = label,
            value = value,
            data = data
        )
    }

    private fun maybeAttachTextWatcher(editText: EditText) {
        if (editText.getTag(TEXT_WATCHER_TAG) != null) return
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                uiCapture.capture(
                    type = ObsUiEventType.TEXT_CHANGE,
                    target = describe(editText),
                    value = s?.toString()
                )
            }
        }
        editText.addTextChangedListener(watcher)
        editText.setTag(TEXT_WATCHER_TAG, watcher)
    }

    private fun findTarget(root: View, rawX: Float, rawY: Float): View? {
        val location = IntArray(2)
        root.getLocationOnScreen(location)
        val x = rawX - location[0]
        val y = rawY - location[1]
        return hitTest(root, x, y)
    }

    private fun hitTest(view: View, x: Float, y: Float): View? {
        if (view.visibility != View.VISIBLE) return null
        if (x < 0 || y < 0 || x > view.width || y > view.height) return null
        if (view is ViewGroup) {
            for (i in view.childCount - 1 downTo 0) {
                val child = view.getChildAt(i)
                val childHit = hitTest(
                    child,
                    x - child.left + view.scrollX,
                    y - child.top + view.scrollY
                )
                if (childHit != null) return childHit
            }
        }
        return view
    }

    private inner class ViewTreeFocusListener :
        android.view.ViewTreeObserver.OnGlobalFocusChangeListener {
        override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
            if (newFocus == null) return
            if (newFocus is EditText) {
                maybeAttachTextWatcher(newFocus)
            }
            uiCapture.capture(
                type = ObsUiEventType.FOCUS,
                target = describe(newFocus),
                data = mapOf("class" to newFocus.javaClass.simpleName)
            )
        }
    }

    companion object {
        private val TEXT_WATCHER_TAG = "obs_ui_text_watcher".hashCode()

        fun describe(view: View?): String {
            if (view == null) return "unknown"
            val content = view.contentDescription?.toString()?.takeIf { it.isNotBlank() }
            val idName = runCatching {
                if (view.id != View.NO_ID) view.resources.getResourceEntryName(view.id) else null
            }.getOrNull()
            val text = (view as? EditText)?.text?.toString()?.takeIf { it.isNotBlank() }
                ?: (view as? android.widget.TextView)?.text?.toString()?.takeIf { it.isNotBlank() }
            return listOfNotNull(content, idName, text, view.javaClass.simpleName).first()
        }
    }
}
