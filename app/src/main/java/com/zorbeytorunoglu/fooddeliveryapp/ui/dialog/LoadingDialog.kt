package com.zorbeytorunoglu.fooddeliveryapp.ui.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zorbeytorunoglu.fooddeliveryapp.R

class LoadingDialog(context: Context, layoutInflater: LayoutInflater) {

    private val dialog: AlertDialog

    init {
        dialog = MaterialAlertDialogBuilder(context).setView(
            layoutInflater.inflate(R.layout.loading_dialog, null)
        ).setCancelable(false).setBackground(ColorDrawable(android.graphics.Color.TRANSPARENT)).create()
    }

    fun show() = dialog.show()
    fun dismiss() = dialog.dismiss()

}