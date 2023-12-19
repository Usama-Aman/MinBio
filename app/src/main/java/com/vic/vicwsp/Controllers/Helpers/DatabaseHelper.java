package com.vic.vicwsp.Controllers.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vic.vicwsp.Models.CartModel;
import com.vic.vicwsp.Models.User;
import com.vic.vicwsp.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "MINBIO";

    // Table Names
    private static final String TABLE_USER = "users";
    private static final String TABLE_CART = "cart";

    // Common column names
    private static final String id = "id";
    private static final String name = "name";
    private static final String lastName = "lastName";
    private static final String email = "email";
    private static final String companyName = "companyName";
    private static final String siretNo = "siretNo";
    private static final String phone = "phone";
    private static final String language = "language";
    private static final String countryCode = "countryCode";
    private static final String is_merchant = "isMerchant";
    private static final String lat = "lat";
    private static final String lng = "lng";
    private static final String userAddress = "userAddress";
    private static final String negoAutoAccept = "negoAutoAccept";
    private static final String kbisPath = "kbisPath";
    private static final String idPath = "idPath";
    private static final String bankDetailId = "bankDetailId";

    // Common column names
    private static final String session_id = "session_id";
    private static final String product_id = "product_id";
    private static final String marchant_id = "marchant_id";
    private static final String quantity = "quantity";
    private static final String price = "price";
    private static final String product_name = "product_name";
    private static final String product_description = "product_description";
    private static final String marchant_name = "marchant_name";
    private static final String product_image = "product_image";
    private static final String stock = "stock";
    private static final String unit = "unit";
    private static final String discount = "discount";
    private static final String vat = "vat";
    private static final String saleCase = "saleCase";
    private static final String subUnit = "subUnit";
    private static final String deliveryType = "deliveryType";
    private static final String sellerCompanyName = "companyName";
    private static final String varietyName = "varietyName";


    // User Table Create Statements
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + id + " INTEGER PRIMARY KEY," +
            name + " TEXT," +
            lastName + " TEXT," +
            email + " TEXT," +
            companyName + " TEXT," +
            siretNo + " TEXT," +
            phone + " TEXT," +
            language + " TEXT," +
            countryCode + " TEXT," +
            is_merchant + " INTEGER," +
            lat + " TEXT," +
            lng + " TEXT," +
            userAddress + " TEXT," +
            negoAutoAccept + " INTEGER," +
            kbisPath + " TEXT," +
            idPath + " TEXT," +
            bankDetailId + " INTEGER)";

    // CartModel Table Create Statements
    private static final String CREATE_TABLE_CART = "CREATE TABLE "
            + TABLE_CART + "(" +
            session_id + " INTEGER," +
            product_id + " INTEGER," +
            marchant_id + " INTEGER," +
            quantity + " TEXT," +
            price + " TEXT," +
            product_name + " TEXT," +
            product_description + " TEXT," +
            marchant_name + " TEXT," +
            product_image + " TEXT," +
            stock + " TEXT," +
            unit + " TEXT," +
            discount + " TEXT," +
            vat + " TEXT," +
            saleCase + " TEXT," +
            subUnit + " TEXT," +
            deliveryType + " TEXT," +
            sellerCompanyName + " TEXT," +
            varietyName + " TEXT)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);

        // create new tables
        onCreate(db);
    }

    public void dropCart() {
        // on upgrade drop older tables
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);

        // create new tables
        onCreateCart(db);
    }

    private void onCreateCart(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CART);
    }

    public long insertUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(id, user.getId());
        values.put(name, user.getName());
        values.put(lastName, user.getLastName());
        values.put(email, user.getEmail());
        values.put(companyName, user.getCompanyName());
        values.put(siretNo, user.getSiretNo());
        values.put(phone, user.getPhone());
        values.put(language, user.getLang());
        values.put(countryCode, user.getCountryCode());
        values.put(lat, user.getLat());
        values.put(lng, user.getLng());
        values.put(userAddress, user.getAddress());
        values.put(is_merchant, user.getIs_merchant());
        values.put(negoAutoAccept, user.getNegoAutoAccept());
        values.put(kbisPath, user.getKbisPath());
        values.put(idPath, user.getIdPath());
        values.put(bankDetailId, user.getBankDetailId());

        long check = db.insert(TABLE_USER, null, values);
        db.close();

        if (check != -1)
            return -1;
        else
            return 1;
    }

    public long insertCart(CartModel cart) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(session_id, cart.getSession_id());
        values.put(product_id, cart.getProduct_id());
        values.put(marchant_id, cart.getMarchant_id());

        values.put(quantity, AppUtils.convertStringToFloat(String.format(Locale.ENGLISH,"%.2f", cart.getQuantity())));
        values.put(price, cart.getPrice());
        values.put(product_name, cart.getProduct_name());
        values.put(product_description, cart.getProduct_description());
        values.put(marchant_name, cart.getMarchant_name());
        values.put(product_image, cart.getProduct_image());
        values.put(stock, String.valueOf(cart.getStock()));
        values.put(unit, cart.getUnit());
        values.put(discount, (cart.getDiscount()));
        values.put(vat, (cart.getVat()));
        values.put(saleCase, (cart.getSaleCase()));
        values.put(subUnit, (cart.getSubUnit()));
        values.put(deliveryType, (cart.getDelivery_type()));
        values.put(sellerCompanyName, cart.getCompanyName());
        values.put(varietyName, cart.getProductVariety());

        long check = db.insert(TABLE_CART, null, values);
        db.close();

        if (check != -1)
            return -1;
        else
            return 1;
    }


    // code to get the single user
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[]{DatabaseHelper.id,
                        name, lastName, email, companyName, siretNo, phone, language, countryCode, lat, lng, userAddress, is_merchant, negoAutoAccept
                        , kbisPath, idPath, bankDetailId}, DatabaseHelper.id + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8),
                cursor.getString(9), cursor.getString(10),
                cursor.getString(11), Integer.parseInt(cursor.getString(12)),
                Integer.parseInt(cursor.getString(13)), cursor.getString(14),
                cursor.getString(15), cursor.getInt(16));

        return user;
    }

    // code to get the single cart Item
    public CartModel getSingleCartItem(int productId, int merchantId) {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery("select * from cart where product_id ='" + productId +
                "' and marchant_id = '" + merchantId + "'", null);

        if (cursor != null)
            cursor.moveToFirst();

        CartModel c = new CartModel(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
                AppUtils.convertStringToFloat(cursor.getString(3)), cursor.getString(4),
                cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8), AppUtils.convertStringToDouble(cursor.getString(9)),
                cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17));

        cursor.close();

        return c;
    }


    // code to update the single contact
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(id, user.getId());
        values.put(name, user.getName());
        values.put(lastName, user.getLastName());
        values.put(email, user.getEmail());
        values.put(companyName, user.getCompanyName());
        values.put(siretNo, user.getSiretNo());
        values.put(phone, user.getPhone());
        values.put(language, user.getLang());
        values.put(countryCode, user.getCountryCode());
        values.put(lat, user.getLat());
        values.put(lng, user.getLng());
        values.put(userAddress, user.getAddress());
        values.put(is_merchant, user.getIs_merchant());
        values.put(negoAutoAccept, user.getNegoAutoAccept());
        values.put(kbisPath, user.getKbisPath());
        values.put(idPath, user.getIdPath());
        values.put(bankDetailId, user.getBankDetailId());

        // updating row
        return db.update(TABLE_USER, values, id + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    // code to update the single contact
    public int updateCart(CartModel cart) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(session_id, cart.getSession_id());
        values.put(product_id, cart.getProduct_id());
        values.put(marchant_id, cart.getMarchant_id());
        values.put(quantity, cart.getQuantity());
        values.put(price, cart.getPrice());
        values.put(product_name, cart.getProduct_name());
        values.put(product_description, cart.getProduct_description());
        values.put(marchant_name, cart.getMarchant_name());
        values.put(product_image, cart.getProduct_image());
        values.put(stock, String.valueOf(cart.getStock()));
        values.put(unit, cart.getUnit());
        values.put(discount, (cart.getDiscount()));
        values.put(vat, (cart.getVat()));
        values.put(saleCase, (cart.getSaleCase()));
        values.put(subUnit, (cart.getSubUnit()));
        values.put(deliveryType, (cart.getDelivery_type()));
        values.put(sellerCompanyName, cart.getCompanyName());
        values.put(varietyName, cart.getProductVariety());


        // updating row
        return db.update(TABLE_CART, values, product_id + " = ? AND " + marchant_id + " = ?",
                new String[]{String.valueOf(cart.getProduct_id()), String.valueOf(cart.getMarchant_id())});
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, id + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void deleteCart(CartModel cart) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, product_id + " = ? AND " + marchant_id + " = ?",
                new String[]{String.valueOf(cart.getProduct_id()), String.valueOf(cart.getMarchant_id())});
        db.close();
    }

    // code to get all contacts in a list view
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(12), cursor.getString(10),
                        cursor.getString(11), Integer.parseInt(cursor.getString(9)),
                        Integer.parseInt(cursor.getString(13)), cursor.getString(14),
                        cursor.getString(15), cursor.getInt(16));

                userList.add(user);
            } while (cursor.moveToNext());
        }

        // return contact list
        return userList;
    }

    public ArrayList<CartModel> getAllCartData() {
        ArrayList<CartModel> cartList = new ArrayList<CartModel>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CART;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                float qty = AppUtils.convertStringToFloat(cursor.getString(3));

                CartModel cart = new CartModel(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
                        AppUtils.convertStringToFloat(String.format(Locale.ENGLISH,"%.2f", qty)), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        AppUtils.convertStringToDouble(cursor.getString(9)),
                        cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13),
                        cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17));
                cartList.add(cart);
            } while (cursor.moveToNext());
        }

        // return contact list
        return cartList;
    }


}
