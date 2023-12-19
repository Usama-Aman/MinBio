
package com.vic.vicwsp.Models.Response.ProductDetailUpdated;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubProduct implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_variety")
    @Expose
    private String productVariety;
    @SerializedName("pro_description")
    @Expose
    private String pro_description;
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
    @SerializedName("origin_flag")
    @Expose
    private String originFlag;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("is_favourite")
    @Expose
    private Integer isFavourite;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("sub_unit")
    @Expose
    private String sub_unit;
    @SerializedName("pro_class")
    @Expose
    private String subProductClass;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("files")
    @Expose
    private ArrayList<File> files = new ArrayList<>();
    @SerializedName("company_name")
    @Expose
    private String company_name;
    @Expose
    private int hasStock;

    public SubProduct(Integer id, String productVariety, String pro_description, String price, String minprice, String maxprice, Float stock, Float saleCase, Integer isBio, Integer isNego, String vat, String discount, String originFlag, String imagePath, Integer isFavourite, String unit, String sub_unit, String subProductClass, String size, ArrayList<File> files, String company_name, int hasStock) {
        this.id = id;
        this.productVariety = productVariety;
        this.pro_description = pro_description;
        this.price = price;
        this.minprice = minprice;
        this.maxprice = maxprice;
        this.stock = stock;
        this.saleCase = saleCase;
        this.isBio = isBio;
        this.isNego = isNego;
        this.vat = vat;
        this.discount = discount;
        this.originFlag = originFlag;
        this.imagePath = imagePath;
        this.isFavourite = isFavourite;
        this.unit = unit;
        this.sub_unit = sub_unit;
        this.subProductClass = subProductClass;
        this.size = size;
        this.files = files;
        this.company_name = company_name;
        this.hasStock = hasStock;
    }

    protected SubProduct(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        productVariety = in.readString();
        pro_description = in.readString();
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
        originFlag = in.readString();
        imagePath = in.readString();
        if (in.readByte() == 0) {
            isFavourite = null;
        } else {
            isFavourite = in.readInt();
        }
        unit = in.readString();
        sub_unit = in.readString();
        subProductClass = in.readString();
        size = in.readString();
        files = in.createTypedArrayList(File.CREATOR);
        company_name = in.readString();
        hasStock = in.readInt();
    }

    public static final Creator<SubProduct> CREATOR = new Creator<SubProduct>() {
        @Override
        public SubProduct createFromParcel(Parcel in) {
            return new SubProduct(in);
        }

        @Override
        public SubProduct[] newArray(int size) {
            return new SubProduct[size];
        }
    };

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getSubProductClass() {
        return subProductClass;
    }

    public void setSubProductClass(String subProductClass) {
        this.subProductClass = subProductClass;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSub_unit() {
        return sub_unit;
    }

    public void setSub_unit(String sub_unit) {
        this.sub_unit = sub_unit;
    }

    public static Creator<SubProduct> getCREATOR() {
        return CREATOR;
    }

    public String getPro_description() {
        return pro_description;
    }

    public void setPro_description(String pro_description) {
        this.pro_description = pro_description;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getHasStock() {
        return hasStock;
    }

    public void setHasStock(int hasStock) {
        this.hasStock = hasStock;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductVariety() {
        return productVariety;
    }

    public void setProductVariety(String productVariety) {
        this.productVariety = productVariety;
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

    public String getOriginFlag() {
        return originFlag;
    }

    public void setOriginFlag(String originFlag) {
        this.originFlag = originFlag;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Integer isFavourite) {
        this.isFavourite = isFavourite;
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
        dest.writeString(productVariety);
        dest.writeString(pro_description);
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
        dest.writeString(originFlag);
        dest.writeString(imagePath);
        if (isFavourite == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isFavourite);
        }
        dest.writeString(unit);
        dest.writeString(sub_unit);
        dest.writeString(subProductClass);
        dest.writeString(size);
        dest.writeTypedList(files);
        dest.writeString(company_name);
        dest.writeInt(hasStock);
    }
}
