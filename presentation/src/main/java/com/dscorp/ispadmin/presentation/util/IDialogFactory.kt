package com.dscorp.ispadmin.presentation.util

import android.app.AlertDialog
import android.content.Context

interface IDialogFactory {
    fun createSuccessDialog(context:Context,mensaje:String):AlertDialog
    fun createErrorDialog(context: Context):AlertDialog
}