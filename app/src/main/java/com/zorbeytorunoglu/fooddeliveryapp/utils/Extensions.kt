package com.zorbeytorunoglu.fooddeliveryapp.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.zorbeytorunoglu.fooddeliveryapp.ui.dialog.LoadingDialog

fun Fragment.showSnackbar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.handleLoadingDialog(isLoading: Boolean, loadingDialog: LoadingDialog) {
    if (isLoading) {
        loadingDialog.show()
    } else {
        loadingDialog.dismiss()
    }
}

fun Fragment.requestPermission2(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) = registerForActivityResult(
    ActivityResultContracts.RequestPermission()) { isGranted ->

    if (isGranted)
        onPermissionGranted()
    else
        onPermissionDenied

}