package com.vic.vicwsp.Models.Response.FavoriteUpdate;

import android.os.Parcel;
import android.os.Parcelable;

import com.vic.vicwsp.Models.Response.ProductDetailUpdated.File;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Detail implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("seller_id")
    @Expose
    private Integer sellerId;
    @SerializedName("seller_name")
    @Expose
    private String sellerName;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("product_variety")
    @Expose
    private String productVariety;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("pro_description")
    @Expose
    private String proDescription;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("minprice")
    @Expose
    private String minprice;
    @SerializedName("maxprice")
    @Expose
    private String maxprice;
    @SerializedName("stock")
    @Expose
    private Float stock;
    @SerializedName("sale_case")
    @Expose
    private Float saleCase;
    @SerializedName("is_bio")
    @Expose
    private Integer isBio;
    @SerializedName("is_nego")
    @Expose
    private Integer isNego;
    @SerializedName("vat")
    @Expose
    private String vat;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("pro_class")
    @Expose
    private String proClass;
    @SerializedName("sub_unit")
    @Expose
    private String subUnit;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("origin_flag")
    @Expose
    private String originFlag;
    @SerializedName("is_favourite")
    @Expose
    private Integer isFavourite;
    @SerializedName("files")
    @Expose
    private ArrayList<File> files = null;

    protected Detail(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            sellerId = null;
        } else {
            sellerId = in.readInt();
        }
        sellerName = in.readString();
        companyName = in.readString();
        productVariety = in.readString();
        productName = in.readString();
        proDescription = in.readString();
        price = in.readString();
        minprice = in.readString();
        maxprice = in.readString();
        if (in.readByte() == 0) {
            stock = null;
        } else {
            stock = in.readFloat();
        }
        if (in.readByte() == 0) {
            saleCase = null;
        } else {
            saleCase = in.readFloat();
        }
        if (in.readByte() == 0) {
            isBio = null;
        } else {
            isBio = in.readInt();
        }
        if (in.readByte() == 0) {
            isNego = null;
        } else {
            isNego = in.readInt();
        }
        vat = in.readString();
        discount = in.readString();
        unit = in.readString();
        size = in.readString();
        proClass = in.readString();
        subUnit = in.readString();
        imagePath = in.readString();
        originFlag = in.readString();
        if (in.readByte() == 0) {
            isFavourite = null;
        } else {
            isFavourite = in.readInt();
        }
        files = in.createTypedArrayList(File.CREATOR);
    }

    public static final Creator<Detail> CREATOR = new Creator<Detail>() {
        @Override
        public Detail createFromParcel(Parcel in) {
            return new Detail(in);
        }

        @Override
        public Detail[] newArray(int size) {
            return new Detail[size];
        }
    };

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProductVariety() {
        return productVariety;
    }

    public void setProductVariety(String productVariety) {
        this.productVariety = productVariety;
    }

    public String getProDescription() {
        return proDescription;
    }

    public void setProDescription(String proDescription) {
        this.proDescription = proDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMinprice() {
        return minprice;
    }

    public void setMinprice(String minprice) {
        this.minprice = minprice;
    }

    public String getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(String maxprice) {
        this.maxprice = maxprice;
    }

    public Float getStock() {
        return stock;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }

    public Float getSaleCase() {
        return saleCase;
    }

    public void setSaleCase(Float saleCase) {
        this.saleCase = saleCase;
    }

    public Integer getIsBio() {
        return isBio;
    }

    public void setIsBio(Integer isBio) {
        this.isBio = isBio;
    }

    public Integer getIsNego() {
        return isNego;
    }

    public void setIsNego(Integer isNego) {
        this.isNego = isNego;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProClass() {
        return proClass;
    }

    public void setProClass(String proClass) {
        this.proClass = proClass;
    }

    public String getSubUnit() {
        return subUnit;
    }

    public void setSubUnit(String subUnit) {
        this.subUnit = subUnit;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOriginFlag() {
        return originFlag;
    }

    public void setOriginFlag(String originFlag) {
        this.originFlag = originFlag;
    }

    public Integer getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Integer isFavourite) {
        this.isFavourite = isFavourite;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (sellerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(sellerId);
        }
        dest.writeString(sellerName);
        dest.writeString(companyName);
        dest.writeString(productVariety);
        dest.writeString(productName);
        dest.writeString(proDescription);
        dest.writeString(price);
        dest.writeString(minprice);
        dest.writeString(maxprice);
        if (stock == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(stock);
        }
        if (saleCase == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(saleCase);
        }
        if (isBio == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isBio);
        }
        if (isNego == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isNego);
        }
        dest.writeString(vat);
        dest.writeString(discount);
        dest.writeString(unit);
        dest.writeString(size);
        dest.writeString(proClass);
        dest.writeString(subUnit);
        dest.writeString(imagePath);
        dest.writeString(originFlag);
        if (isFavourite == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isFavourite);
        }
        dest.writeTypedList(files);
    }
}

