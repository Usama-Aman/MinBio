package com.vic.vicwsp.Controllers.Interfaces;

import android.os.Parcel;
import android.os.Parcelable;

public interface UpdateProductDetailPrice extends Parcelable {

    void updateProductDetailPrice(int productId,String price);

    @Override
    int describeContents();

    @Override
    void writeToParcel(Parcel dest, int flags);
}
