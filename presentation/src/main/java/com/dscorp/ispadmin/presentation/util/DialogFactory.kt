package com.dscorp.ispadmin.presentation.util

import android.app.AlertDialog
import android.content.Context

class DialogFactory: IDialogFactory {
        override fun createSuccessDialog(context: Context,mensaje :String): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Operacion exitosa")
            builder.setMessage( mensaje)
            builder.setPositiveButton("Aceptar") { _, _ -> }
            return builder.create()
        }

        override fun createErrorDialog(context: Context, error:String): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Ocurrío un error")
            builder.setMessage(error)
            builder.setPositiveButton("Aceptar") { _, _ -> }
            return builder.create()
        }
    }



