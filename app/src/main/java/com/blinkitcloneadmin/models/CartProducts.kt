package com.blinkitcloneadmin.models

data class CartProducts (

    val productId: String = "random",   // cant apply nullability check here.
    val productTitle: String ? = null,
    val productQuantity: String ? = null,
    val productPrice: String ? = null,
    val productCount: Int ? = null,
    val productStock: Int ? = null,
    val productImage: String ? = null,
    val productCategory: String ? = null,
    var adminUid: String ? = null,
    var productType: String ? = null
)