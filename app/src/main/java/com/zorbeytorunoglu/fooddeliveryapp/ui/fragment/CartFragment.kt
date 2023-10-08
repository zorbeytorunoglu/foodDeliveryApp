package com.zorbeytorunoglu.fooddeliveryapp.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.zorbeytorunoglu.fooddeliveryapp.R
import com.zorbeytorunoglu.fooddeliveryapp.databinding.FragmentCartBinding
import com.zorbeytorunoglu.fooddeliveryapp.ui.adapter.CartAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.mapLoadingAnimation.visibility = View.VISIBLE

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        loadMapWithPermission(savedInstanceState).launch(Manifest.permission.ACCESS_FINE_LOCATION)

        binding.cartRecyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        binding.cartRecyclerView.adapter = CartAdapter(requireContext())

        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun loadMapWithPermission(bundle: Bundle?) = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mapFragment.getMapAsync { map ->
                        map.addMarker(MarkerOptions().position(latLng))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
                        binding.mapLoadingAnimation.visibility = View.GONE
                    }
                    mapFragment.onCreate(bundle)
                }

            }

        }
    }

}