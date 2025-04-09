package com.dscorp.ispadmin.presentation.composeComponents

import MyTheme
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.widget.ArrayAdapter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout


/**
 * Un componente Composable que envuelve MaterialAutoCompleteTextView para usar en Jetpack Compose.
 *
 * @param items Lista genérica de elementos para mostrar en el dropdown
 * @param label Etiqueta que se mostrará encima del campo
 * @param selectedItem Elemento seleccionado actualmente
 * @param onItemSelected Callback invocado cuando se selecciona un elemento
 * @param onSelectionCleared Callback invocado cuando se borra la selección
 * @param modifier Modificador para aplicar al componente
 * @param hint Texto de sugerencia cuando no hay selección
 */
@Composable
fun <T> MyAutoCompleteTextViewCompose(
    items: List<T>,
    label: String,
    selectedItem: T? = null,
    onItemSelected: (T) -> Unit,
    onSelectionCleared: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = ""
) {
    val context = LocalContext.current
    var internalSelectedItem by remember { mutableStateOf(selectedItem) }
    var ignoreNextTextChange by remember { mutableStateOf(false) }

    // Almacenar una referencia local a la lista actual para usarla en onItemClick
    val currentItems = items

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { originalContext ->
            // Envolvemos el contexto con el estilo deseado
            val styledContext = ContextThemeWrapper(
                originalContext,
                com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox_ExposedDropdownMenu
            )

            // Crear el TextInputLayout usando el styledContext
            val textInputLayout = TextInputLayout(
                styledContext,
                null,
                com.google.android.material.R.attr.textInputOutlinedExposedDropdownMenuStyle // Asegura que tome la apariencia de menú desplegable
            ).apply {
                this.hint = label
                // Si lo deseas, puedes forzar la caja a Outline
                // boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            }

            // Crear el AutoCompleteTextView con el mismo styledContext
            val autoCompleteTextView = MaterialAutoCompleteTextView(styledContext).apply {
                id = android.R.id.text1

                // Configurar el adaptador con los items
                setupAdapter(styledContext, currentItems)

                // Establecer texto inicial si hay un item seleccionado
                internalSelectedItem?.let {
                    ignoreNextTextChange = true
                    setText(it.toString(), false)
                    // Colocar el cursor al final del texto
                    setSelection(text.length)
                }

                // Configurar el listener para detectar selecciones
                setOnItemClickListener { _, _, position, _ ->
                    // Verificamos que la lista no esté vacía y que la posición sea válida
                    if (currentItems.isNotEmpty() && position < currentItems.size) {
                        val selectedValue = currentItems[position]
                        internalSelectedItem = selectedValue
                        ignoreNextTextChange = true
                        val selectedText = selectedValue.toString()
                        setText(selectedText, false)
                        // Colocar el cursor al final del texto seleccionado
                        setSelection(selectedText.length)
                        onItemSelected(selectedValue)
                    }
                }

                // Configurar el listener para detectar cambios de texto
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {}

                    override fun afterTextChanged(s: Editable?) {
                        if (ignoreNextTextChange) {
                            ignoreNextTextChange = false
                            return
                        }

                        val currentText = s?.toString() ?: ""
                        val selectedText = internalSelectedItem?.toString() ?: ""

                        // Si el texto actual no coincide con el elemento seleccionado, anular la selección
                        if (currentText != selectedText) {
                            internalSelectedItem = null
                            onSelectionCleared()
                        }
                    }
                })

                // Configurar el hint
                if (hint.isNotEmpty() && internalSelectedItem == null) {
                    setHint(hint)
                }
            }

            // Agregar el AutoCompleteTextView al layout
            textInputLayout.addView(autoCompleteTextView)
            textInputLayout
        },
        update = { textInputLayout ->
            // Actualizar el adaptador con los items actuales
            val autoCompleteTextView =
                textInputLayout.findViewById<MaterialAutoCompleteTextView>(android.R.id.text1)

            // Importante: Siempre actualizar el adaptador con la lista actual
            autoCompleteTextView.setupAdapter(context, currentItems)

            // Actualizar el texto seleccionado si cambió externamente
            if (internalSelectedItem != selectedItem) {
                internalSelectedItem = selectedItem
                ignoreNextTextChange = true
                val newText = selectedItem?.toString() ?: ""
                autoCompleteTextView.setText(newText, false)
                // Colocar el cursor al final del texto
                autoCompleteTextView.setSelection(newText.length)
            }
        }
    )

    // Limpiar recursos cuando el componente se destruye
    DisposableEffect(Unit) {
        onDispose { }
    }
}

// Extensión para simplificar la configuración del adaptador
private fun <T> MaterialAutoCompleteTextView.setupAdapter(context: Context, items: List<T>) {
    val stringItems = items.map { it.toString() }
    val adapter = ArrayAdapter(
        context,
        android.R.layout.simple_dropdown_item_1line,
        stringItems
    )
    setAdapter(adapter)
}

@Preview
@Composable
private fun AtoCompletePreview() {
    MyTheme {
        var selected by remember { mutableStateOf<String?>(null) }
        Column {
            MyAutoCompleteTextViewCompose(
                items = listOf("Opción 1", "Opción 2", "Opción 3"),
                label = "Seleccione una opción",
                selectedItem = selected,
                onItemSelected = { selected = it },
                onSelectionCleared = {
                    selected = null
                }
            )
            Text("Elemento seleccionado: ${selected ?: "Ninguno"}")
        }
    }
}
