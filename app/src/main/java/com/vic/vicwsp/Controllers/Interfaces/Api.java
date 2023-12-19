package com.vic.vicwsp.Controllers.Interfaces;

import com.vic.vicwsp.Models.Request.LoginRequest;
import com.vic.vicwsp.Models.Response.AddProduct.AddProductModel;
import com.vic.vicwsp.Models.Response.BankDetail.BankDetailResponse;
import com.vic.vicwsp.Models.Response.CartOrder.CartOrderModel;
import com.vic.vicwsp.Models.Response.ChartData.ProductDetailChartData;
import com.vic.vicwsp.Models.Response.Chat.ChatModel;
import com.vic.vicwsp.Models.Response.Comments.CommentsModel;
import com.vic.vicwsp.Models.Response.Complaint.ComplaintChat.ComplaintChatResponse;
import com.vic.vicwsp.Models.Response.Complaint.ComplaintChat.ComplaintSingleChatResponse;
import com.vic.vicwsp.Models.Response.Complaint.ComplaintList.CompliantResponse;
import com.vic.vicwsp.Models.Response.CreditNotes.CreditNotesModel;
import com.vic.vicwsp.Models.Response.CreditNotesDetails.CreditNotesDetailModel;
import com.vic.vicwsp.Models.Response.PrivacyPolicy.PrivacyPolicyModel;
import com.vic.vicwsp.Models.Response.SaveCards.SaveCardsModel;
import com.vic.vicwsp.Models.Response.SendComplaintResponse.ComplaintModel;
import com.vic.vicwsp.Models.Response.DriversTrackingList.DriverTrackingListModel;
import com.vic.vicwsp.Models.Response.EditProduct.EditProductModel;
import com.vic.vicwsp.Models.Response.FavoriteUpdate.FavoriteModel;
import com.vic.vicwsp.Models.Response.GradientsResearch.Gradients;
import com.vic.vicwsp.Models.Response.Login.Login;
import com.vic.vicwsp.Models.Response.NegoUpdated.NegoUpdated;
import com.vic.vicwsp.Models.Response.Notifications.NotificationModel;
import com.vic.vicwsp.Models.Response.OrdersHistory.OrderHistory;
import com.vic.vicwsp.Models.Response.Outstanding.OutstandingModel;
import com.vic.vicwsp.Models.Response.ProductDetailUpdated.ProductDetailModel;
import com.vic.vicwsp.Models.Response.ProductsPagination.ProductsPagination;
import com.vic.vicwsp.Models.Response.SalesForm.SalesFormModel;
import com.vic.vicwsp.Models.Response.SearchData.SearchDataModel;
import com.vic.vicwsp.Models.Response.Support.SupportChat.SupportChatResponse;
import com.vic.vicwsp.Models.Response.Support.SupportChat.SupportSingleChatResponse;
import com.vic.vicwsp.Models.Response.Support.SupportList.SupportResponse;
import com.vic.vicwsp.Models.Response.Support.SupportResponse.TopicsModel;

import org.json.JSONArray;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @Headers({"Accept:application/json"})
    @POST("register")
    @Multipart
    Call<ResponseBody> register(@Header("localization") String language,
                                @Part("name") RequestBody name,
                                @Part("last_name") RequestBody lastName,
                                @Part("email") RequestBody email,
                                @Part("company_name") RequestBody company_name,
                                @Part("password") RequestBody password,
                                @Part("password_confirmation") RequestBody password_confirmation,
                                @Part("siret_no") RequestBody siret_no,
                                @Part("country_code") RequestBody country_code,
                                @Part("phone") RequestBody phone,
                                @Part("iso2") RequestBody iso2,
                                @Part MultipartBody.Part kbis_file,
                                @Part MultipartBody.Part id_file);

    @Headers({"Accept:application/json"})
    @POST("login")
    Call<Login> login(@Header("localization") String language, @Body LoginRequest loginRequest);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("resend_otp")
    Call<ResponseBody> resendOtp(@Header("localization") String language, @Field("phone") String phone);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("confirm_otp")
    Call<ResponseBody> confirmOtp(@Header("localization") String language, @Field("phone") String phone, @Field("otp") String otp);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("password_email")
    Call<ResponseBody> forgotPassword(@Header("localization") String language, @Field("email") String email);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("resend_email")
    Call<ResponseBody> resendEmail(@Header("localization") String language, @Field("email") String email);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("confirm_email")
    Call<ResponseBody> confirmEmail(@Header("localization") String language, @Field("email") String email);


    @Multipart
    @Headers({"Accept:application/json"})
    @POST("user/profile")
    Call<ResponseBody> saveProfile(@Header("Authorization") String authHeader,
                                   @Part("email") RequestBody email,
                                   @Part("name") RequestBody name,
                                   @Part("last_name") RequestBody lastName,
                                   @Part("phone") RequestBody phone,
                                   @Part("company_name") RequestBody companyName,
                                   @Part("siret_no") RequestBody siretNo,
                                   @Part("country_code") RequestBody countryCode,
                                   @Part("iso2") RequestBody iso2,
                                   @Part("lat") RequestBody lat,
                                   @Part("lng") RequestBody lng,
                                   @Part("address") RequestBody address,
                                   @Part("nego_auto_accept") RequestBody nego_auto_accept,
                                   @Part("password") RequestBody password,
                                   @Part("password_confirmation") RequestBody confirmPassword,
                                   @Part MultipartBody.Part kbis_file,
                                   @Part MultipartBody.Part id_file);


    @Multipart
    @Headers({"Accept:application/json"})
    @POST("user/profile")
    Call<ResponseBody> saveProfileWithoutPassword(@Header("Authorization") String authHeader,
                                                  @Part("email") RequestBody email,
                                                  @Part("name") RequestBody name,
                                                  @Part("last_name") RequestBody lastName,
                                                  @Part("phone") RequestBody phone,
                                                  @Part("company_name") RequestBody companyName,
                                                  @Part("siret_no") RequestBody siretNo,
                                                  @Part("country_code") RequestBody countryCode,
                                                  @Part("iso2") RequestBody iso2,
                                                  @Part("lat") RequestBody lat,
                                                  @Part("lng") RequestBody lng,
                                                  @Part("address") RequestBody address,
                                                  @Part("nego_auto_accept") RequestBody nego_auto_accept,
                                                  @Part MultipartBody.Part kbis_file,
                                                  @Part MultipartBody.Part id_file);

    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("reset_password")
    Call<ResponseBody> resetPassword(@Field("password") String password,
                                     @Field("password_confirmation") String password_confirmation,
                                     @Field("email") String email,
                                     @Field("otp") String otp);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("user/logout")
    Call<ResponseBody> logout(@Header("Authorization") String authHeader,
                              @Field("device_id") String deviceId,
                              @Field("user_type") String userType);

    @Headers({"Accept:application/json"})
    @GET("user/categories")
    Call<ResponseBody> getCategories(@Header("Authorization") String authHeader);

    @Headers({"Accept:application/json"})
    @GET("user/favourite")
    Call<FavoriteModel> getFavourite(@Header("Authorization") String authHeader, @Query("page") int page);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/favourite")
    Call<ResponseBody> setFavorite(@Header("Authorization") String authHeader, @Field("id") int id);


    @Headers({"Accept:application/json"})
    @GET("user/productdetail/{id}")
    Call<ProductDetailModel> getProductDetail(@Header("Authorization") String authHeader, @Path("id") int id);

    @Headers({"Accept:application/json"})
    @GET("user/bio/{id}")
    Call<ProductDetailModel> getBioProductDetail(@Header("Authorization") String authHeader, @Path("id") int id);

    @Headers({"Accept:application/json"})
    @GET("user/products/{categoryId}")
    Call<ProductsPagination> getPagination(@Header("Authorization") String authHeader, @Path("categoryId") int id);

    @Headers({"Accept:application/json"})
    @GET("user/merchantproducts")
    Call<ProductsPagination> getMerchantProducts(@Header("Authorization") String authHeader, @Query("page") int page);

    @Headers({"Accept:application/json"})
    @GET("user/bio")
    Call<ProductsPagination> getBioProducts(@Header("Authorization") String authHeader);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/orderproducts")
    Call<CartOrderModel> checkCart(@Header("Authorization") String authHeader,
                                   @Field("products") JSONArray jsonArray,
                                   @Field("lat") double lat,
                                   @Field("lng") double lng,
                                   @Field("address") String address,
                                   @Field("stripe_token") String stripe_token,
                                   @Field("delivery_type") String delivery_type,
                                   @Field("order_type") String order_type,
                                   @Field("is_coverage") int is_coverage,
                                   @Field("payment_type") String payment_type,
                                   @Field("delivery_date") String delivery_date,
                                   @Field("is_saved_card") int is_saved_card,
                                   @Field("overdraft_logs") String overdraft_logs);

    @Headers({"Accept:application/json"})
    @GET("user/settings")
    Call<ResponseBody> getSettings(@Header("Authorization") String authHeader);


    @Headers({"Accept:application/json"})
    @GET("user/orders")
    Call<OrderHistory> getOrdersHistory(@Header("Authorization") String authHeader, @Query("page") int page);


    @Headers({"Accept:application/json"})
    @GET("user/ordersreceived")
    Call<OrderHistory> getReceivedOrders(@Header("Authorization") String authHeader, @Query("page") int page);

    @Headers({"Accept:application/json"})
    @GET("user/orders")
    Call<OrderHistory> getOverDraftOrders(@Header("Authorization") String authHeader, @Query("status") String status);


    @Headers({"Accept:application/json"})
    @GET("user/gradients/{productId}")
    Call<Gradients> getGradients(@Header("Authorization") String authHeader, @Path("productId") int productId);


    @Headers({"Accept:application/json"})
    @GET("user/searchdata")
    Call<SearchDataModel> getResearchData(@Header("Authorization") String authHeader);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/searchproduct")
    Call<FavoriteModel> searchProduct(@Header("Authorization") String authHeader,
                                      @Field("product") int productId,
                                      @Field("merchant") int merchant,
                                      @Field("origin") int origin,
                                      @Field("minprice") String minprice,
                                      @Field("maxprice") String maxprice,
                                      @Query("page") int page);

    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("user/addproducts")
    Call<ResponseBody> addProductWithId(@Header("Authorization") String authHeader,
                                        @Field("id") int id,
                                        @Field("product_id") int productId,
                                        @Field("is_bio") int isBio,
                                        @Field("is_nego") int isNego,
                                        @Field("price") double price,
                                        @Field("stock") float stock,
                                        @Field("minprice") double minprice,
                                        @Query("sale_case") float sale_case,
                                        @Query("vat") String vat,
                                        @Query("vat_id") int vatId,
                                        @Query("discount") double discount,
                                        @Query("size_id") int sieId,
                                        @Query("class_id") int classId,
                                        @Query("unit_id") int unitId,
                                        @Query("subunit_id") String subunit_id,
                                        @Query("country_id") int countryId,
                                        @Field("product_variety") String name,
                                        @Field("pro_description") String detail);


    //Editing product in variety // Edit Case
    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("user/addproducts")
    Call<ResponseBody> addProductWithoutId(@Header("Authorization") String authHeader,
                                           @Field("product_id") int productId,
                                           @Field("is_bio") int isBio,
                                           @Field("is_nego") int isNego,
                                           @Field("price") double price,
                                           @Field("stock") float stock,
                                           @Field("minprice") double minprice,
                                           @Query("sale_case") float sale_case,
                                           @Query("vat") String vat,
                                           @Query("vat_id") int vatId,
                                           @Query("discount") double discount,
                                           @Query("size_id") int sieId,
                                           @Query("class_id") int classId,
                                           @Query("country_id") int countryId,
                                           @Query("unit_id") int unitId,
                                           @Query("subunit_id") String subunit_id,
                                           @Field("product_variety") String name,
                                           @Field("pro_description") String detail);

    @Headers({"Accept:application/json"})
    @GET("user/catproducts")
    Call<SalesFormModel> getSalesFormData(@Header("Authorization") String authHeader);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/changelang")
    Call<ResponseBody> changeLanguage(@Header("Authorization") String authHeader,
                                      @Field("lang") String language);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/ordernego")
    Call<NegoUpdated> sendProposal(@Header("Authorization") String authHeader,
                                   @Field("product_id") int product_id,
                                   @Field("price") double price,
                                   @Field("quantity") float quantity,
                                   @Field("merchant_id") int merchant_id,
                                   @Field("lat") double lat,
                                   @Field("lng") double lng,
                                   @Field("address") String address,
                                   @Field("delivery_type") String delivery_type,
                                   @Field("delivery_date") String delivery_date);

    @Headers({"Accept:application/json"})
    @GET("user/proposals/{id}")
    Call<NegoUpdated> getProposals(@Header("Authorization") String authHeader,
                                   @Path("id") int id,
                                   @Query("type") String type);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/proposals")
    Call<NegoUpdated> postProposals(@Header("Authorization") String authHeader,
                                    @Field("order_id") int order_id,
                                    @Field("price") double price,
                                    @Field("is_merchant") int isMerchant);

    @Headers({"Accept:application/json"})
    @GET("user/acceptproposal/{id}/{is_merchant}")
    Call<ResponseBody> acceptProposal(@Header("Authorization") String authHeader,
                                      @Path("id") int id,
                                      @Path("is_merchant") int is_merchant);


    @Headers({"Accept:application/json"})
    @GET("user/proposalpayment/{id}")
    Call<ResponseBody> getProposalPayment(@Header("Authorization") String authHeader,
                                          @Path("id") int id);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/proposalpayment")
    Call<ResponseBody> postPaymentProposal(@Header("Authorization") String authHeader,
                                           @Field("order_id") int order_id,
                                           @Field("stripe_token") String token,
                                           @Field("order_type") String order_type,
                                           @Field("payment_type") String payment_type,
                                           @Field("is_saved_card") int is_saved_card);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/fcmdevices")
    Call<ResponseBody> sendDeviceToken(@Header("Authorization") String authHeader,
                                       @Field("token") String token,
                                       @Field("type") String type,
                                       @Field("app_mode") String appMode,
                                       @Field("device_id") String deviceId,
                                       @Field("user_type") String userType);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/deliverycharges")
    Call<ResponseBody> getDeliveryCharges(@Header("Authorization") String authHeader,
                                          @Field("merchant_ids") String merchant,
                                          @Field("lat") String lat,
                                          @Field("lng") String lng);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/reviews")
    Call<ResponseBody> setRating(@Header("Authorization") String authHeader,
                                 @Field("comment") String comment,
                                 @Field("order_id") int order_id,
                                 @Field("pricing") double pricing,
                                 @Field("quality") double quality,
                                 @Field("availability") double availability,
                                 @Field("relation") double relation,
                                 @Field("trustrelation") double trustrelation);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/userreviews")
    Call<ResponseBody> setMerchantRating(@Header("Authorization") String authHeader,
                                         @Field("comment") String comment,
                                         @Field("order_id") int order_id,
                                         @Field("rating") double rating);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/driverreviews")
    Call<ResponseBody> giveDriverRating(@Header("Authorization") String authHeader,
                                        @Field("comment") String comment,
                                        @Field("order_id") int order_id,
                                        @Field("rating") double rating,
                                        @Field("driver_id") int driverId);

    @Headers({"Accept:application/json"})
    @GET("user/editproducts/{id}")
    Call<EditProductModel> getEditProduct(@Header("Authorization") String authHeader,
                                          @Path("id") int id);


    @Headers({"Accept:application/json"})
    @GET("user/rejectproposal/{id}")
    Call<ResponseBody> rejectProposal(@Header("Authorization") String authHeader,
                                      @Path("id") int id);

    @Headers({"Accept:application/json"})
    @GET("user/countries")
    Call<ResponseBody> getCountries(@Header("Authorization") String authHeader);


    @Headers({"Accept:application/json"})
    @GET("user/sizes")
    Call<ResponseBody> getSizes(@Header("Authorization") String authHeader);


    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/priceevareport")
    Call<ProductDetailChartData> getChartData(@Header("Authorization") String authHeader,
                                              @Field("product_id") int productId);


    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/getquote")
    Call<OutstandingModel> getQuote(@Header("Authorization") String authHeader,
                                    @Field("products") JSONArray jsonArray);


    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/orderproducts")
    Call<CartOrderModel> checkQuote(@Header("Authorization") String authHeader,
                                    @Field("products") JSONArray jsonArray,
                                    @Field("lat") double lat,
                                    @Field("lng") double lng,
                                    @Field("address") String address,
                                    @Field("delivery_type") String delivery_type);

    @Headers({"Accept:application/json"})
    @GET("user/reviews/{id}")
    Call<CommentsModel> getComments(@Header("Authorization") String authHeader,
                                    @Path("id") int id,
                                    @Query("page") int page);

    @Headers({"Accept:application/json"})
    @GET("user/addproducts/{id}")
    Call<AddProductModel> getProductList(@Header("Authorization") String authHeader,
                                         @Path("id") int id);

    @Headers({"Accept:application/json"})
    @DELETE("user/deleteproducts/{id}")
    Call<ResponseBody> deleteSubProduct(@Header("Authorization") String authHeader,
                                        @Path("id") int id);

    @Headers({"Accept:application/json"})
    @DELETE("user/files/{id}")
    Call<ResponseBody> deleteFiles(@Header("Authorization") String authHeader,
                                   @Path("id") int id);

    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/files")
    Call<ResponseBody> sendFiles(@Header("Authorization") String authHeader,
                                 @Part("subproduct_id") RequestBody subProductId,
                                 @Part MultipartBody.Part part);


    @Headers({"Accept:application/json"})
    @GET("user/trackorder/{id}")
    Call<DriverTrackingListModel> getNavigationLatLng(@Header("Authorization") String authHeader,
                                                      @Path("id") int id);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/getcoverage")
    Call<ResponseBody> getCoverage(@Header("Authorization") String authHeader,
                                   @Field("lat") String lat,
                                   @Field("lng") String lng,
                                   @Field("delivery_type") String delivery_type,
                                   @Field("products") JSONArray jsonArray);

    @Headers({"Accept:application/json"})
    @GET("user/pendingpayments")
    Call<ResponseBody> getPendingPayment(@Header("Authorization") String authHeader);

    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/coveragepayment")
    Call<ResponseBody> coveragePayment(@Header("Authorization") String authHeader,
                                       @Field("order_id") int orderId,
                                       @Field("stripe_token") String token,
                                       @Field("is_saved_card") int is_saved_card,
                                       @Field("overdraft_logs") String overdraft_logs);

    @Headers({"Accept:application/json"})
    @GET("user/notifications")
    Call<NotificationModel> getNotificationsFromApi(@Header("Authorization") String authHeader, @Query("page") int page);

    @Headers({"Accept:application/json"})
    @GET("user/readall/notifications")
    Call<ResponseBody> clearAllNotifications(@Header("Authorization") String authHeader);

    @Headers({"Accept:application/json"})
    @DELETE("user/notifications/{id}")
    Call<ResponseBody> deleteNotification(@Header("Authorization") String authHeader,
                                          @Path("id") int id);

    //Get Nego Chat Messages btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @GET("user/chat")
    Call<ChatModel> getChatMessages(@Header("Authorization") String authHeader,
                                    @Query("order_id") int orderId,
                                    @Query("page") int page);

    // Get Chat messages btw buyer and driver of a specific order
    @Headers({"Accept:application/json"})
    @GET("user/driverbuyerchat")
    Call<ChatModel> getBuyerDriverMessages(@Header("Authorization") String authHeader,
                                           @Query("order_id") int orderId,
                                           @Query("page") int page);

    // Get Chat messages btw seller and driver of a specific order
    @Headers({"Accept:application/json"})
    @GET("user/driversellerchat")
    Call<ChatModel> getSellerDriverMessages(@Header("Authorization") String authHeader,
                                            @Query("order_id") int orderId,
                                            @Query("page") int page);

    //    Post nego chat messages  btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/chats")
    Call<ResponseBody> postTextMessage(@Header("Authorization") String authHeader,
                                       @Part("order_id") RequestBody orderId,
                                       @Part("type") RequestBody type,
                                       @Part("duration") RequestBody duration,
                                       @Part("message") RequestBody message);

    //   Post Chat Text messages btw buyer and driver of a specific order
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/driverbuyerchat")
    Call<ResponseBody> postTextMessageBuyerDriver(@Header("Authorization") String authHeader,
                                                  @Part("order_id") RequestBody orderId,
                                                  @Part("type") RequestBody type,
                                                  @Part("duration") RequestBody duration,
                                                  @Part("message") RequestBody message);

    //   Post Chat Text messages btw seller and driver of a specific order
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/driversellerchat")
    Call<ResponseBody> postTextMessageSellerDriver(@Header("Authorization") String authHeader,
                                                   @Part("order_id") RequestBody orderId,
                                                   @Part("type") RequestBody type,
                                                   @Part("duration") RequestBody duration,
                                                   @Part("message") RequestBody message);

    //   post nego file chat messages  btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/chats")
    Call<ResponseBody> postFileMessage(@Header("Authorization") String authHeader,
                                       @Part("order_id") RequestBody orderId,
                                       @Part("type") RequestBody type,
                                       @Part("duration") RequestBody duration,
                                       @Part MultipartBody.Part file);

    //   Post Chat File messages btw buyer and driver of a specific order
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/driversellerchat")
    Call<ResponseBody> postFileMessageSellerDriver(@Header("Authorization") String authHeader,
                                                   @Part("order_id") RequestBody orderId,
                                                   @Part("type") RequestBody type,
                                                   @Part("duration") RequestBody duration,
                                                   @Part MultipartBody.Part file);

    //   Post Chat File messages btw Seller and driver of a specific order
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/driverbuyerchat")
    Call<ResponseBody> postFileMessageBuyerDriver(@Header("Authorization") String authHeader,
                                                  @Part("order_id") RequestBody orderId,
                                                  @Part("type") RequestBody type,
                                                  @Part("duration") RequestBody duration,
                                                  @Part MultipartBody.Part file);


    @Headers({"Accept:application/json"})
    @GET("user/productstock/{id}")
    Call<ResponseBody> getUpdateStockAndSaleCase(@Header("Authorization") String authHeader,
                                                 @Path("id") int id);


    @Headers({"Accept:application/json"})
    @GET("user/radiation/status")
    Call<ResponseBody> getRadiationStatus(@Header("Authorization") String authHeader);

    @Headers({"Accept:application/json"})
    @GET("user/bank/detail")
    Call<BankDetailResponse> getBankDetail(@Header("Authorization") String authHeader);

    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/bank/detail")
    Call<ResponseBody> updateBankDetail(@Header("Authorization") String authHeader,
                                        @Part("account_name") RequestBody account_name,
                                        @Part("swift_number") RequestBody swift_number,
                                        @Part("iban") RequestBody iban,
                                        @Part MultipartBody.Part bank_detail_photo);


    /********  Complaints Section  ********/

    @Headers({"Accept:application/json"})
    @GET("user/complaints")
    Call<CompliantResponse> getComplaints(@Header("Authorization") String authHeader, @Query("page") int page);


    //Get Nego Chat Messages btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @GET("user/complaints/{complaintId}")
    Call<ComplaintChatResponse> getComplaintMessages(@Header("Authorization") String authHeader,
                                                     @Path("complaintId") int complaintId);

    //   post nego file chat messages  btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/complaints/comment")
    Call<ComplaintSingleChatResponse> postComplaintMessage(@Header("Authorization") String authHeader,
                                                           @Part("compalint_id") RequestBody orderId,
                                                           @Part("comment") RequestBody comment);

    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/complaints/comment")
    Call<ComplaintSingleChatResponse> postComplaintMessageFile(@Header("Authorization") String authHeader,
                                                               @Part("compalint_id") RequestBody orderId,
                                                               @Part("comment") RequestBody comment,
                                                               @Part MultipartBody.Part file);

    @Headers({"Accept:application/json"})
    @GET("user/complaints/metadata")
    Call<ComplaintModel> getComplaintData(@Header("Authorization") String authHeader);


    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/complaints")
    Call<ResponseBody> postComplaintData(@Header("Authorization") String authHeader,
                                         @Part("order_id") RequestBody orderId,
                                         @Part("item") RequestBody itemId,
                                         @Part("complaint_type_id") RequestBody complaintTypeId,
                                         @Part("comment") RequestBody comment,
                                         @Part("is_credit_note") RequestBody is_credit_note,
                                         @Part MultipartBody.Part[] files);

    /********  Support Section  ********/

    @Headers({"Accept:application/json"})
    @GET("user/support")
    Call<SupportResponse> getSupportList(@Header("Authorization") String authHeader, @Query("page") int page);


    //Get Nego Chat Messages btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @GET("user/support/{supportId}")
    Call<SupportChatResponse> getSupportMessages(@Header("Authorization") String authHeader,
                                                 @Path("supportId") int supportId);

    //   post nego file chat messages  btw Buyer and Seller
    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/support/comment")
    Call<SupportSingleChatResponse> postSupportMessage(@Header("Authorization") String authHeader,
                                                       @Part("support_id") RequestBody orderId,
                                                       @Part("comment") RequestBody comment);

    @Headers({"Accept:application/json"})
    @Multipart
    @POST("user/support/comment")
    Call<SupportSingleChatResponse> postSupportMessageFile(@Header("Authorization") String authHeader,
                                                           @Part("support_id") RequestBody orderId,
                                                           @Part("comment") RequestBody comment,
                                                           @Part MultipartBody.Part file);

    @Headers({"Accept:application/json"})
    @GET("user/support/topics")
    Call<TopicsModel> getSupportData(@Header("Authorization") String authHeader);


    @Headers({"Accept:application/json"})
    @FormUrlEncoded
    @POST("user/support")
    Call<ResponseBody> postSupportData(@Header("Authorization") String authHeader,
                                       @Field("support_topic_id") int supportId,
                                       @Field("email") String email,
                                       @Field("comment") String comment,
                                       @Field("type") String type);

    @Headers({"Accept:application/json"})
    @GET("pages/{key}")
    Call<PrivacyPolicyModel> getPrivacyPolicy(@Path("key") String key);


    @Headers({"Accept:application/json"})
    @GET("user/cards")
    Call<SaveCardsModel> getUserCards(@Header("Authorization") String authHeader);


    @Headers({"Accept:application/json"})
    @DELETE("user/cards/{id}")
    Call<ResponseBody> deleteUserCard(@Header("Authorization") String authHeader,
                                      @Path("id") int id);


    @Headers({"Accept:application/json"})
    @GET("user/creditnotes")
    Call<CreditNotesModel> getCreditNotes(@Header("Authorization") String authHeader,
                                          @Query("page") int page);

    @Headers({"Accept:application/json"})
    @GET("user/creditnotes/{id}")
    Call<CreditNotesDetailModel> getCreditNoteDetail(@Header("Authorization") String authHeader,
                                                     @Path("id") int id);


    @FormUrlEncoded
    @Headers({"Accept:application/json"})
    @POST("user/creditnotes/payment")
    Call<ResponseBody> postCreditPayment(@Header("Authorization") String authHeader,
                                         @Field("credit_note_id") int credit_note_id,
                                         @Field("stripe_token") String token,
                                         @Field("is_saved_card") int is_saved_card);

//    Common.showDialog(SignUp.this);
//    Api api = RetrofitClient.getClient().create(Api.class);
//    Call<ResponseBody> call = api.;
//        call.enqueue(new Callback<ResponseBody>() {
//        @Override
//        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//            Common.dissmissDialog();
//            try {
//
//                if (response.isSuccessful()) {
//
//
//
//
//                } else {
//                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                    showToast(SignUp.this, jsonObject.getString("message"), false);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ResponseBody> call, Throwable t) {
//            Common.dissmissDialog();
//            Log.d("Response", "onFailure: " + t.getMessage());
//        }
//    });

}
