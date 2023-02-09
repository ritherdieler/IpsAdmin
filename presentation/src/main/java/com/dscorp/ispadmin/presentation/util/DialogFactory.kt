package com.dscorp.ispadmin.presentation.util

import android.app.AlertDialog
import android.content.Context

class DialogFactory: IDialogFactory {
        override fun createSuccessDialog(context: Context,mensaje :String): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Registro Exitoso")
            builder.setMessage( mensaje)
            builder.setPositiveButton("Aceptar") { _, _ -> }
            return builder.create()
        }

        override fun createErrorDialog(context: Context): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("El registro no fue procesado ")
            builder.setMessage("ERROR EN EL REGISTRO")
            builder.setPositiveButton("Aceptar") { _, _ -> }
            return builder.create()
        }
    }



