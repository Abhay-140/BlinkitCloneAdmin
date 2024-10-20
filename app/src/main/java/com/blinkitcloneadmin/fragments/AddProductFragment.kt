package com.blinkitcloneadmin.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blinkitcloneadmin.utils.Constants
import com.blinkitcloneadmin.R
import com.blinkitcloneadmin.utils.Utils
import com.blinkitcloneadmin.activities.AdminMainActivity
import com.blinkitcloneadmin.adapter.AdapterSelectedImage
import com.blinkitcloneadmin.databinding.FragmentAddProductBinding
import com.blinkitcloneadmin.models.Product
import com.blinkitcloneadmin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {

    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAddProductBinding
    private val imagesUris: ArrayList<Uri> = arrayListOf()
    val selectedImage =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUri ->
            val fiveImages = listOfUri.take(5)
            imagesUris.clear()
            imagesUris.addAll(fiveImages)

            binding.rvProductImages.adapter = AdapterSelectedImage(imagesUris)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater)

        setAutoCompleteTextViews()
        onImageSelectClicked()
        onAddButtonClicked()
        return binding.root
    }

    private fun onAddButtonClicked() {
        binding.btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(), "Uploading images....")
            val productTitle = binding.etProductTitle.text.toString()
            val productQuantity = binding.etProductQuality.text.toString()
            val productUnit = binding.etProductUnit.text.toString()
            val productPrice = binding.etProductPrice.text.toString()
            val productStock = binding.etProductStock.text.toString()
            val productCategory = binding.etProductCategory.text.toString()
            val productType = binding.etProductType.text.toString()

            if (productCategory.isEmpty() || productType.isEmpty() || productTitle.isEmpty() || productQuantity.isEmpty() || productStock.isEmpty() || productUnit.isEmpty() || productPrice.isEmpty()) {
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(), "Empty fields are not allowed")
                }
            } else if (imagesUris.isEmpty()) {
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(), "Empty fields are not allowed")
                }
            } else {
                val product = Product(productTitle = productTitle,
                    productCategory = productCategory,
                    productPrice = productPrice.toInt(),
                    productStock = productStock.toInt(),
                    productQuantity = productQuantity.toInt(),
                    productType = productType,
                    productUnit = productUnit,
                    itemCount = 0,
                    adminUid = Utils.getCurrentUserId(),
                    productRandomId = Utils.getRandomId()
                )
                saveImage(product)
            }
        }
    }

    private fun saveImage(product: Product) {
        viewModel.saveImageInDB(imagesUris)
        lifecycleScope.launch {
            viewModel.isImageUploaded.collect {
                if (it) {
                    Utils.apply {
                        hideDialog()
                        showToast(requireContext(), "image saved")
                    }
                    getUrls(product)
                }
            }
        }
    }

    private fun getUrls(product: Product) {
        Utils.showDialog(requireContext(), "Publishing product....")

        lifecycleScope.launch {
            viewModel.downloadedUrls.collect {
                val urls = it
                product.productImageUris = urls
                saveProduct(product)
            }
        }
    }

    private fun saveProduct(product: Product) {
        viewModel.saveProduct(product)
        lifecycleScope.launch {
            viewModel.isProductSaved.collect {
                if (it) {
                    Utils.hideDialog()
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    Utils.showToast(requireContext(), "Your product is live")
                }
            }
        }
    }

    private fun onImageSelectClicked() {
        binding.btnSelectImage.setOnClickListener {
            selectedImage.launch("image/*")
        }
    }

    private fun setAutoCompleteTextViews() {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val category =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductsCategory)
        val productType =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductTypes)

        binding.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }
    }

}