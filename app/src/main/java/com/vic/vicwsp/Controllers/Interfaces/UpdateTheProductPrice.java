package com.vic.vicwsp.Controllers.Interfaces;

import android.os.Parcelable;

public interface UpdateTheProductPrice extends Parcelable {

    void updatePrice(int productId, String price);

}
