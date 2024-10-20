package com.blinkitcloneadmin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blinkitcloneadmin.R
import com.blinkitcloneadmin.utils.Utils
import com.blinkitcloneadmin.adapter.AdapterCartProducts
import com.blinkitcloneadmin.databinding.FragmentOrderDetailBinding
import com.blinkitcloneadmin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {

    private val viewModel : AdminViewModel by viewModels()
    private lateinit var binding: FragmentOrderDetailBinding
    private var status = 0
    private var currentStatus = 0
    private var orderId = ""
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

        Utils.setStatusBarColor(requireContext(), R.color.yellow , requireActivity())
        getValues()
        settingStatus(status)
        lifecycleScope.launch { getOrderedProducts() }
        onChangeStatusButtonClicked()
        onBackButtonClicked()

        return binding.root
    }

    private fun onChangeStatusButtonClicked() {
        binding.btnChangeStatus.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { menu ->
                when(menu.itemId) {
                    R.id.menuReceived -> {
                        currentStatus = 1
                        if(currentStatus > status) {
                            status = 1
                            settingStatus(1)
                            viewModel.updateOrderStatus(orderId, 1)
                        }
                        else {
                            Utils.showToast(requireContext(), "Order is already received...")
                        }
                        true
                    }

                    R.id.menuDispatched -> {
                        currentStatus = 2
                        if(currentStatus > status) {
                            status = 2
                            settingStatus(2)
                            viewModel.updateOrderStatus(orderId, 2)
                        }
                        else {
                            Utils.showToast(requireContext(), "Order is already dispatched...")
                        }
                        true
                    }

                    R.id.menuDelivered -> {
                        currentStatus = 3
                        if(currentStatus > status) {
                            status = 3
                            settingStatus(3)
                            viewModel.updateOrderStatus(orderId, 3)
                        }
                        true
                    }

                    else -> { false }
                }
            }
        }
    }

    private suspend fun getOrderedProducts() {
        viewModel.getOrderedProduct(orderId).collect{cartList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartList)
        }
    }

    private fun settingStatus(status: Int) {
        val blueColor = ContextCompat.getColorStateList(requireContext(), R.color.blue)

        binding.iv1.backgroundTintList = blueColor
        if (this.status >= 1) {
            binding.iv2.backgroundTintList = blueColor
            binding.view1.backgroundTintList = blueColor
        }
        if (this.status >= 2) {
            binding.iv3.backgroundTintList = blueColor
            binding.view2.backgroundTintList = blueColor
        }
        if (this.status >= 3) {
            binding.iv4.backgroundTintList = blueColor
            binding.view3.backgroundTintList = blueColor
        }

    }

    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()
        binding.tvUserAddress.text = bundle.getString("userAddress").toString()
    }

    private fun onBackButtonClicked() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
        }
    }
}