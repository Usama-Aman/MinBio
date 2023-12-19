package com.vic.vicwsp.Controllers.Interfaces;

import com.vic.vicwsp.Models.Response.ProductDetailUpdated.Seller;

public interface SendingDataToProductView {

    void sendingDataToProductView(int lisPosition, int expandListPosition, Seller seller);
}
