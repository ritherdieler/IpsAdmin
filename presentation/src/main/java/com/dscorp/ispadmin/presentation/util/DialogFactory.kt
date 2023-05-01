package com.dscorp.ispadmin.presentation.util

import android.app.AlertDialog
import android.content.Context

class DialogFactory : IDialogFactory {
    override fun createSuccessDialog(
        context: Context,
        mensaje: String,
        onPositiveCallback: (() -> Unit)?
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Operacion exitosa")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar") { d, _ ->
            if (onPositiveCallback != null) {
                onPositiveCallback.invoke()
            } else {
                d.dismiss()
            }
        }
        return builder.create()
    }

    override fun createErrorDialog(context: Context, error: String): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Ocurrío un error")
        builder.setMessage(error)
        builder.setPositiveButton("Aceptar") { d, _ -> d.dismiss() }
        return builder.create()
    }
}
