package com.zorbeytorunoglu.fooddeliveryapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.zorbeytorunoglu.fooddeliveryapp.R

class OrderCompleteDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.order_received_dialog, container, false)

        rootView.setOnClickListener {
            dismiss()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = true
    }

}