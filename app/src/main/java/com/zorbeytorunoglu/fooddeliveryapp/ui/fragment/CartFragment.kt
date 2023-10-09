package com.zorbeytorunoglu.fooddeliveryapp.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.zorbeytorunoglu.fooddeliveryapp.R
import com.zorbeytorunoglu.fooddeliveryapp.databinding.FragmentCartBinding
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.GroupedCartFood
import com.zorbeytorunoglu.fooddeliveryapp.ui.adapter.CartAdapter
import com.zorbeytorunoglu.fooddeliveryapp.ui.dialog.OrderCompleteDialog
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.CartFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartAdapterListener {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartFragmentViewModel by viewModels<CartFragmentViewModel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.mapLoadingAnimation.visibility = View.VISIBLE

        binding.cartRecyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        val gList = viewModel.groupAndCalculateTotal(viewModel.cartLiveData.value)

        if (gList.isEmpty()) {
            binding.sadImageView.visibility = View.VISIBLE
            binding.sadTextView.visibility = View.VISIBLE
        } else {
            binding.sadImageView.visibility = View.GONE
            binding.sadTextView.visibility = View.GONE
        }

        binding.cartTotalPrice.text = "₺${gList.sumOf { g -> g.totalPrice }}"

        val adapter = CartAdapter(requireContext(), gList.toMutableList(), viewModel, viewLifecycleOwner)

        adapter.setListener(this)

        binding.cartRecyclerView.adapter = adapter

        binding.backImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(
                CartFragmentDirections.actionCartFragmentToMainFragment()
            )
        }

        binding.checkoutButton.setOnClickListener {
            if (viewModel.cartLiveData.value.isNullOrEmpty()) {
                Snackbar.make(it, "You have nothing in your cart.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                onPlaceOrder()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        loadMapWithPermission(savedInstanceState).launch(Manifest.permission.ACCESS_FINE_LOCATION)

        return binding.root
    }

    override fun onCartEmpty() {
        binding.sadImageView.visibility = View.VISIBLE
        binding.sadTextView.visibility = View.VISIBLE
        binding.cartTotalPrice.text = "₺0"
        binding.cartRecyclerView.adapter = CartAdapter(requireContext(), emptyList<GroupedCartFood>().toMutableList(),viewModel,viewLifecycleOwner)
    }

    private fun onPlaceOrder() {
        binding.cartTotalPrice.text = "₺0"
        viewModel.clearCart()
        binding.cartRecyclerView.adapter = CartAdapter(requireContext(), emptyList<GroupedCartFood>().toMutableList(),viewModel,viewLifecycleOwner)
        val completeDialog = OrderCompleteDialog()
        completeDialog.show(parentFragmentManager, "OrderCompleteDialog")
    }

    override fun onFoodAdd(price: Double) {
        val newTotal = binding.cartTotalPrice.text.toString().substring(1).toDouble() + price
        binding.cartTotalPrice.text = "₺$newTotal"
    }

    override fun onFoodRemove(price: Double) {
        val newTotal = binding.cartTotalPrice.text.toString().substring(1).toDouble() - price

        if (newTotal == 0.0) return

        binding.cartTotalPrice.text = "₺$newTotal"
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