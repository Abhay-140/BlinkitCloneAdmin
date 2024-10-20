package com.blinkitcloneadmin.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blinkitcloneadmin.utils.Constants
import com.blinkitcloneadmin.R
import com.blinkitcloneadmin.utils.Utils
import com.blinkitcloneadmin.activities.AuthMainActivity
import com.blinkitcloneadmin.adapter.AdapterProduct
import com.blinkitcloneadmin.databinding.FragmentHomeBinding
import com.blinkitcloneadmin.adapter.CategoriesAdapter
import com.blinkitcloneadmin.databinding.EditProductLayoutBinding
import com.blinkitcloneadmin.models.Category
import com.blinkitcloneadmin.models.Product
import com.blinkitcloneadmin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    val viewModel : AdminViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterProduct: AdapterProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        Utils.setStatusBarColor(requireContext(), R.color.yellow, requireActivity())
        setCategories()
        searchProducts()
        getAllTheProducts("All")
        onLogOut()

        return binding.root
    }

    private fun onLogOut() {
        binding.tbHomeFragment.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menuLogout -> {
                    logOutUser()
                    true
                }

                else -> { false }
            }
        }
    }

    private fun logOutUser() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()
        builder.setTitle("Log out")
            .setMessage("Do you want to log out?")
            .setPositiveButton("Yes") {_,_ ->
                viewModel.logOutUser()
                startActivity(
                    Intent(
                        requireContext(), AuthMainActivity::class.java
                    )
                )
                requireActivity().finish()
            }
            .setNegativeButton("No"){_,_ ->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }

    private fun searchProducts() {
        binding.searchEt.addTextChangedListener( object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.filter?.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun getAllTheProducts(category: String) {

        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category).collect{
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvTEXT.visibility = View.VISIBLE
                }
                else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvTEXT.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(::onEditButtonClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList((it))
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun onEditButtonClicked(product : Product) {
        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
            etProductTitle.setText(product.productTitle)
            etProductQuality.setText(product.productQuantity.toString())
            etProductUnit.setText(product.productUnit)
            etProductPrice.setText(product.productPrice.toString())
            etProductStock.setText(product.productStock.toString())
            etProductCategory.setText(product.productCategory)
            etProductType.setText(product.productType)
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()

        editProduct.btnEdit.setOnClickListener {
            editProduct.apply {
                etProductTitle.isEnabled = true
                etProductQuality.isEnabled = true
                etProductUnit.isEnabled = true
                etProductPrice.isEnabled = true
                etProductStock.isEnabled = true
                etProductCategory.isEnabled = true
                etProductType.isEnabled = true
            }
        }

        setAutoCompleteTextViews(editProduct)

        editProduct.btnSave.setOnClickListener {

            lifecycleScope.launch {
                product.productTitle = editProduct.etProductTitle.text.toString()
                product.productUnit = editProduct.etProductUnit.text.toString()
                product.productCategory = editProduct.etProductCategory.text.toString()
                product.productType = editProduct.etProductType.text.toString()
                product.productStock = editProduct.etProductStock.text.toString().toInt()
                product.productQuantity = editProduct.etProductQuality.text.toString().toInt()
                product.productPrice = editProduct.etProductPrice.text.toString().toInt()
                viewModel.savingUpdateProducts(product)
            }
            Utils.showToast(requireContext(), "Saved changes!")
            alertDialog.dismiss()
        }
    }

    private fun setAutoCompleteTextViews(editProduct: EditProductLayoutBinding) {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val category =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductsCategory)
        val productType =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductTypes)

        editProduct.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }
    }

    private fun setCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constants.allProductsCategoryIcon.size) {
            categoryList.add(
                Category(
                    Constants.allProductsCategory[i],
                    Constants.allProductsCategoryIcon[i]
                )
            )
        }
        binding.rvCategories.adapter = CategoriesAdapter(categoryList, ::onCategoryClicked)
    }

    private fun onCategoryClicked(category: Category) {
        getAllTheProducts(category.category)
    }
}