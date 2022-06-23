package com.goldmedal.crm.data.network

import com.goldmedal.crm.data.model.AddedInvoiceItemData
import com.goldmedal.crm.data.network.GlobalConstant.BASE_URL
import com.goldmedal.crm.data.network.responses.*
import com.goldmedal.crm.ui.dashboard.Manager.Data
import com.goldmedal.crm.ui.dashboard.Manager.ManagerMonthWiseData
import com.goldmedal.crm.ui.dashboard.Manager.ManagerTicketCountData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface MyApi {

    //for manager https://goldapi.goldmedalindia.in/api/crm/v1.0/Profile/GetTicketCountForServiceCenterManagerDashboard
//    {
//  "UserID": 1,
//  "ServiceCenterID": 22
//}

    @FormUrlEncoded
    @POST("Profile/GetTicketCountForServiceCenterManagerDashboard")
    suspend fun getManagerTicketCount(
        @Field("UserID") intUserID: Int,
        @Field("ServiceCenterID") intServiceCenterID: Int
    ): Response<ManagerTicketCountData>


    @FormUrlEncoded
    @POST("Profile/GetTicketCountMonthWiseForDashBoardServiceManagerDashboard")
    suspend fun getManagerDashboard(
        @Field("UserID") intUserID: Int,
        @Field("ServiceCenterID") intServiceCenterID: Int
    ): Response<ManagerMonthWiseData>

    /*
    *
    *
    * API'S HAS BEEN ORDERED AS PER APP FLOW FOR CONVENIENCE
    *
    *
    *
    * */

    // - - - - - Authenticate user API - - - - - -



    
    @FormUrlEncoded
    @POST("Login/authenticateLoginID")
    suspend fun authenticateLogin(
            @Field("UserName") strCin: String,
            @Field("deviceid") strDeviceId: String,
            @Field("DeviceType") strDeviceType: String,
            @Field("ModelType") strModelType: String,
            @Field("OsVersion") strOSVersion: String,
            @Field("NotificationId") strFCMToken: String
    ): Response<SessionResponse>


      // - - - - - GET OTP API - - - - - -
     @FormUrlEncoded
    @POST("Login/authenticateLoginID")
    suspend fun getOtp(
            @Field("UserName") strCin: String,
            @Field("deviceid") strDeviceId: String,
            @Field("DeviceType") strDeviceType: String,
            @Field("ModelType") strModelType: String,
            @Field("OsVersion") strOSVersion: String,
            @Field("NotificationId") strFCMToken: String
    ): Response<GetOtpResponse>


    // - - - - - Authenticate password API - - - - - -
    @FormUrlEncoded
    @POST("Login/authenticatePassword")
    suspend fun authenticatePassword(
            @Field("LogNo") logNo: Int,
            @Field("SessionId") strSessionId: String,
            @Field("Password") strPassword: String

    ): Response<LoginResponse>



// - - - - - verify otp API - - - - - -
    @FormUrlEncoded
    @POST("Login/verifyOtp")
    suspend fun verifyOtp(
            @Field("MobileNo") strMobileNo: String,
            @Field("Otp") strOtp: String,
            @Field("RequestNo") strRequestNo: String,
            @Field("Deviceid") strDeviceId: String,
            @Field("DeviceType") strDeviceType: String,
            @Field("ModelType") strModelType: String,
            @Field("OsVersion") strOSVersion: String,
            @Field("NotificationId") strFCMToken: String
    ): Response<LoginResponse>

    // - - - - -  API to get profile details  - - - - - - - -
    @FormUrlEncoded
    @POST("Profile/getProfileDetails")
    suspend fun profileDetail(
        @Field("Userid") userId: Int
    ): Response<ProfileDetailResponse>

    // - - - - -  API to get tickets count for service engineer  - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getTicketCountForServiceEngineer")
    suspend fun getTicketsCount(
            @Field("Userid") userId: Int,
            @Field("Fromdate") fromDate: String,
            @Field("Todate") toDate: String
    ): Response<GetTicketsCountResponse>


    // - - - - -  API to get today's appointment for service engineer  - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getTodaysAppointmentofServiceEngineer")
    suspend fun getAppointments(
            @Field("Userid") userId: Int,
            @Field("Fromdate") fromDate: String,
            @Field("Todate") toDate: String
    ): Response<GetAppointmentsResponse>


    // - - - - -  API to get ticket details for service engineer  - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getTicketDetailsForServiceEngineer")
    suspend fun getTicketDetailsForEngineer(
            @Field("Userid") userId: Int,
            @Field("Fromdate") fromDate: String,
            @Field("Todate") toDate: String,
            @Field("Type") type: Int,
            @Field("SearchBy") searchBy: String
    ): Response<GetTicketDetailsForEngineerResponse>


    // - - - - -  API to get ticket history  - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getMonthlyTotalClosedTickets")
    suspend fun getTicketHistory(
        @Field("Userid") userId: Int
    ): Response<TicketHistoryResponse>




    // - - - - -  API to get customer contacts  - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getCustomerContacts")
    suspend fun getCustomerContacts(
            @Field("Userid") userId: Int,
            @Field("SearchBy") searchBy: String
    ): Response<CustomerContactsResponse>


    // - - - - -  API to get customer products  - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getCustomerProducts")
    suspend fun getCustomerProducts(
        @Field("CustId") customerId: Int,
        @Field("SearchBy") searchBy: String
    ): Response<CustomerProductsResponse>



    // - - - - -  API to get tickets by products  - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getTicketsByProducts")
    suspend fun getTicketsByProducts(
        @Field("CustId") customerId: Int,
        @Field("ProductId") productId: Int
    ): Response<TicketsByProductsResponse>





    // - - - - -  API to get dashboard details  - - - - - - - -
    @FormUrlEncoded
    @POST("Profile/getTicketsCountsForDashboard")
    suspend fun dashboardDetail(
        @Field("Userid") userId: Int
    ): Response<DashboardResponse>


    // - - - - - - API to get all assigned tickets list - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getAllAssignedTicket")
    suspend fun getAllAssignedTickets(
        @Field("Userid") userId: Int
    ): Response<GetAssignedTicketResponse>


    // - - - - - -- - API to accept/reject ticket  - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/acceptTicket")
    suspend fun acceptTicket(
        @Field("Userid") userid: Int,
        @Field("TicketID") ticketID: Int,
        @Field("Latitude") latitude: String,
        @Field("Longitude") longitude: String,
        @Field("Location") location: String
    ): Response<AcceptRejectResponse>


    @FormUrlEncoded
    @POST("Ticket/rejectTicket")
    suspend fun rejectTicket(
        @Field("Userid") userid: Int,
        @Field("TicketID") ticketID: Int,
        @Field("ReasonID") reasonID: Int,
        @Field("Pincode") pincode: String,
        @Field("StateID") stateID: Int,
        @Field("DistrictID") districtID: Int,
        @Field("City") strArea: String,
        @Field("AddressLine1") strAddressLine1: String,
        @Field("AddressLine2") strAddressLine2: String,
        @Field("AddressLine3") strAddressLine3: String,
        @Field("RejectRemark") strRejectRemark: String,
        @Field("Type") type: Int,
        @Field("Latitude") latitude: String,
        @Field("Longitude") longitude: String,
        @Field("Location") location: String
    ): Response<AcceptRejectResponse>

    // - - - - - -- - API to state & district from pincode  (Ticket Unacceptance Dialog)- - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getPincodewiseStateAndDistrict")
    suspend fun getPincodeWiseStateDistrict(
        @Field("Userid") userid: Int,
        @Field("Pincode") strPincode: String
    ): Response<PincodeWiseStateDistrictResponse>


    // - - - - - - API to get accepted ticket list  - -- - - - - -
    @FormUrlEncoded
    @POST("Ticket/getAllTickets")
    suspend fun getAcceptedTicketList(
        @Field("Userid") userId: Int,
        @Field("Fromdate") fromDate: String,
        @Field("Todate") toDate: String,
        @Field("StatusBy") statusBy: Int,
        @Field("SearchBy") searchBy: String
    ): Response<GetAcceptedTicketResponse>

    //  - - -- - - - API to get accepted ticket detail - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getTicketDetails")
    suspend fun getTicketDetails(
        @Field("Userid") userId: Int,
        @Field("TicketID") ticketID: Int
    ): Response<GetTicketDetailsResponse>


    //  - - -- - - - API to check-in - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/checkIn")
    suspend fun checkIn(
        @Field("Userid") userId: Int,
        @Field("TicketID") ticketID: Int,
        @Field("Latitude") latitude: String,
        @Field("Longitude") longitude: String,
        @Field("Location") location: String,
        @Field("isGeofenceLock") isGeofenceLock: Boolean
    ): Response<AcceptRejectResponse>



    // - - - - - - API to scan QR code  - - - - - -
    @FormUrlEncoded
    @POST("Ticket/GetProductDetailsByScanQRCode")
    suspend fun scanQrCode(
        @Field("Slno") slNo: String,
        @Field("Qrcode") strQrCode: String,
        @Field("CustomerID") custId: Int,
        @Field("Master") master: Boolean
    ): Response<QrResponse>

    // - - - - - - API to search QR code  - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getProductDetailsFromQRCode")
    suspend fun searchQrCode(
            @Field("Qrcode") strQrCode: String,
            @Field("CustomerID") custId: Int,
            @Field("Type") type: Int,
            @Field("TicketID") tktID: Int,
            @Field("Master") master: Boolean
    ): Response<QrResponse>

//    // - - - - - - API to scan QR code  - - - - - -
//    @FormUrlEncoded
//    @POST("Ticket/GetProductDetailsByScanQRCodeCopy")
//    suspend fun scanQrCode(
//        @Field("Slno") slNo: String,
//        @Field("Qrcode") strQrCode: String,
//        @Field("CustomerID") custId: Int,
//        @Field("Master") master: Boolean
//    ): Response<QrResponse>
//
//    // - - - - - - API to search QR code  - - - - - -
//    @FormUrlEncoded
//    @POST("Ticket/getProductDetailsFromQRCodeCopy")
//    suspend fun searchQrCode(
//        @Field("Qrcode") strQrCode: String,
//        @Field("CustomerID") custId: Int,
//        @Field("Type") type: Int,
//        @Field("TicketID") tktID: Int,
//        @Field("Master") master: Boolean
//    ): Response<QrResponse>


    // - - - - - - API to get product symptoms list  - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getSymptomsList")
    suspend fun getProductSymptomsList(
        @Field("CategoryID") categoryId: Int
    ): Response<ProductSymptomsResponse>



    // - - - - - - API to get Time-Slot list  - - - - - -

    @POST("Ticket/getTimeSlot")
    suspend fun getTimeSlot(
    ): Response<TimeSlotResponse>


//    // - - - - - - API to get closed otp request  - - - - - -
//    @FormUrlEncoded
//    @POST("Login/sendClosedOtp")
//    suspend fun sendClosedOtp(
//            @Field("MobileNo") strMobileNo: String,
//            @Field("TicketID") intTicketId: Int,
//            @Field("TicketNo") strTicketNo: String,
//            @Field("UserID") intUserId: Int,
//            @Field("CustName") strCustName: String
//    ): Response<ClosedOtpResponse>

    // - - - - - - API to get closed otp request  - - - - - -
    @FormUrlEncoded
    @POST("Login/sendClosedOtpCopy")
    suspend fun sendClosedOtp(
        @Field("MobileNo") strMobileNo: String,
        @Field("TicketID") intTicketId: Int,
        @Field("TicketNo") strTicketNo: String,
        @Field("UserID") intUserId: Int,
        @Field("CustName") strCustName: String,
        @Field("ReSend") resendOtp: Int,
        @Field("DeviceID") strDeviceId: String
    ): Response<ClosedOtpResponse>


    //  -- - - - -- API to update visit status  - - - - - - - -
    @FormUrlEncoded
   // @POST("Ticket/updateTicketStatus")
    @POST("Ticket/updateTicketStatus")
    suspend fun updateVisitStatus(
        @Field("UserID") userID: Int,
        @Field("TicketID") ticketID: Int,
        @Field("ActionID") actionID: Int,
        @Field("DivisionID") divisionID: Int,
        @Field("CategoryID") categoryID: Int,
        @Field("ProductID") productID: Int,
        @Field("ProductQRCode") strItemQRCode: String,
        @Field("ProductEANNo") strItemEANNo: String,
        @Field("ProductEANRemark") strItemEANNoRemark: String,
        @Field("IsWarrenty") isWarranty : Boolean,
        @Field("BillProof") strBillProofImg: String,
        @Field("DateOfPurchase") strPurchaseDate: String,
        @Field("Symtoms") strProductSymptoms: String,
        @Field("RescheduleDate") strRescheduleDate: String,
        @Field("RescheduleTimeSlotId") timeSlotId: Int,
        @Field("RescheduleReason") strRescheduleReason: String,
        @Field("UpdateStatusRemark") strUpdateStatusRemark: String,
        @Field("ServiceCharge") serviceCharge: Int,
        @Field("Latitude") latitude: String,
        @Field("Longitude") longitude: String,
        @Field("Location") location: String,
        @Field("ProductImage") productImage: String,
        @Field("QRImage") qrImage: String,
        @Field("SelfieImage") selfieImage: String,
        @Field("IsGoodFeedback") isGoodFeedback: String,
        @Field("IsChekoutOutofPremises") isOutOfPremises: Boolean,
        @Field("OutPremisesRemark") outPremisesRemark: String,
        @Field("CheckOutDistance") checkOutDistance: String,
        @Field("IsNoRepair") isNoRepair: Boolean,
        @Field("IsProductReplaced") isProductReplaced: Boolean,
        @Field("ReplacementQRCode") replacementQRCode: String

    ): Response<UpdateVisitStatusResponse>

    // - - - - - -  API for Ticket Activity - - - - - - - - - - -

    @FormUrlEncoded
    @POST("Ticket/getTicketActivityDetails")
    suspend fun getTicketActivity(
        @Field("TicketID") userId: Int

    ): Response<TicketActivityResponse>


    // - - - - - -  API for update profile photo - - - - - - - - - - -
    @FormUrlEncoded
    @POST("Login/profilePictureUpdate")
    suspend fun updateProfilePhoto(
        @Field("ProfilePhotoLink") profilePhotoLink: String,
        @Field("UserId") userId: Int
    ): Response<UpdateProfilePhotoResponse>


    // - - - - - -  API for Notification Feeds - - - - - - - - - - -
    @FormUrlEncoded
    @POST("events/getNotification")
    suspend fun fetchNotifications(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<List<NotificationFeedsResponse>>


    //  - - -- - - - API to get used item and part detail - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/itemAndPartWiseData")
    suspend fun getItemAndPartDetails(
            @Field("ItemSlno") itemSlno: Int,
            @Field("SearchBy") searchBy: String
    ): Response<UsedItemAndPartResponse>

    //  - - -- - - - API to get used part and item detail - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/PartAndItemWiseData")
    suspend fun getPartAndItemDetails(
            @Field("PartSlno") partSlno: Int,
            @Field("SearchBy") searchBy: String
    ): Response<UsedPartAndItemResponse>

    //  - - -- - - - API to get invoice item detail - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getItemForInvoice")
    suspend fun getInvoiceItemDetail(
        @Field("SearchBy") searchBy: String,
        @Field("UserID") userID: Int,
        @Field("CustomerID") custId: Int,
        @Field("TicketID") tktId: Int,
        @Field("Type") scanType: Int,
        @Field("QRCode") qrCdoe: String,
        @Field("ProductId") productID: Int
    ): Response<GetItemForInvoiceResponse>

    //  - - -- - - - API to generate invoice for items selected - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/addUpdateItemInvoice")
    suspend fun generateInvoiceForItems(
        @Field("Slno") slno: Int,
        @Field("CustomerID") custId: Int,
        @Field("TicketID") tktId: Int,
        @Field("Status") status: String,
        @Field("TaxType") taxType: Int,
        @Field("TaxAmount1") taxAmount1: Double,
        @Field("TaxAmount2") taxAmount2: Double,
        @Field("PreTaxAmount") preTaxAmount: Double,
        @Field("Discount") discount: Double,
        @Field("AfterDiscountAmount") afterDiscountAmount: Double,
        @Field("FinalTotal") finalTotal: Double,
        @Field("UserID") userID: Int,
        @Field("LogNo") logNo: Int,
        @Field("ApplicationID") applicationID: Int,
        @Field("InvoiceItemDetails") invoiceItemDetail: JSONArray
    ): Response<UpdateVisitStatusResponse>


    //  - - -- - - - API to get invoice history list - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getInvoiceList")
    suspend fun getInvoiceList(
        @Field("Userid") userID: Int,
        @Field("SearchBy") searchBy: String
    ): Response<GetInvoiceListResponse>


    //  - - -- - - - API to get edit invoice list - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getEditInvoiceData")
    suspend fun getEditInvoiceData(
        @Field("InvoiceID") invoiceID: Int
    ): Response<EditInvoiceResponse>


    //  - - -- - - - API to update invoice - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/UpdateInvoiceData")
    suspend fun updateInvoiceData(
        @Field("Slno") slNo: Int,
        @Field("PaymentMethod") paymentMethod: String,
        @Field("PaymentStatus") paymentStatus: Boolean,
        @Field("Status") status: String,
        @Field("Userid") userID: Int
    ): Response<UpdateInvoiceResponse>

    //  - - -- - - - API to get parts requirement (pass only ticketID for details else 0 , pass only custid for ticket list else 0, pass only user id for cust list else 0) - - - - - - - -
    @FormUrlEncoded
    @POST("Ticket/getCustomerTicketData")
    suspend fun getPartRequirementData(
        @Field("Userid") userID: Int,
        @Field("CustomerID") custId: Int,
        @Field("TicketID") tktId: Int
    ): Response<GetPartsRequirementResponse>


// - - - - parts list - - - - -
    @FormUrlEncoded
    @POST("Ticket/getItemStock")
    suspend fun getItemStockParts(
        @Field("Userid") userID: Int,
        @Field("RequestNO") requestNo: String,
        @Field("SearchBy") searchBy: String
    ): Response<SelectPartListResponse>


    // - - - - parts list by user - - - - -
    @FormUrlEncoded
    @POST("Ticket/getRequestedItemByUser")
    suspend fun getPartsRequirementByUser(
        @Field("RequestNO") requestNo: String
    ): Response<SelectPartListResponse>

    // - - - - submit parts requirement list - - - - -
    @FormUrlEncoded
    @POST("Ticket/requestForPart")
    suspend fun submitPartsRequest(
        @Field("Userid") userID: Int,
        @Field("TicketID") tktId: Int,
        @Field("CustomerID") custId: Int,
        @Field("PartID") partsItemDetails: JSONArray
    ): Response<SubmitPartsReqResponse>


    // - - - - API to get parts requirement list - - - - -
    @FormUrlEncoded
    @POST("Ticket/getRequestPartData")
    suspend fun getRequestPartData(
        @Field("Userid") userID: Int
    ): Response<GetRequestPartListResponse>

    // - - - - API to update app from backend - - - - -
    @FormUrlEncoded
  //  @POST("UpdateCRM")
    @POST()
    suspend fun updateLocalApp(
        @Url baseUrl: String?,
        @Field("ClientSecret") clientSecret: String,
        @Field("Url") url: String
    ): Response<UpdateAppResponse>


    companion object {
        operator fun invoke(
                networkConnectionInterceptor: NetworkConnectionInterceptor
        ): MyApi {
            val logging = HttpLoggingInterceptor()

            logging.level = HttpLoggingInterceptor.Level.BODY
            val okkHttpclient = OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(networkConnectionInterceptor)
                    .addInterceptor(logging)
                    .build()

            return Retrofit.Builder()
                    .client(okkHttpclient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MyApi::class.java)
        }
    }

}
