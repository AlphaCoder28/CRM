package com.goldmedal.crm.ui.dashboard.home

import android.content.Context
import android.location.Location
import android.os.Handler
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.GetAppointmentsData
import com.goldmedal.crm.databinding.AppointmentItemBinding
import com.goldmedal.crm.ui.ticket.TicketViewDetailsActivity
import com.goldmedal.crm.util.getLocationFromAddress
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.crm.util.toast
import com.google.android.gms.maps.model.LatLng
import com.xwray.groupie.viewbinding.BindableItem
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class AppointmentsItem(
    private val ticketAppointment: GetAppointmentsData?,
    private val context: Context,
    private val callBackListener: IStatusListener?,
    latitude: Double?,
    longitude: Double?
) : BindableItem<AppointmentItemBinding>() {

    var custLatLng: LatLng? = null
    var custAddressLat: String? = "0.0"
    var custAddressLong: String? = "0.0"
    var currentLat = latitude
    var currentLng = longitude

    override fun bind(viewBinding: AppointmentItemBinding, position: Int) {

        viewBinding.apply {

            txtProductIssues.isSelected = true
            txtProductName.isSelected = true
            txtTicketNo.isSelected = true
            txtCustomerName.text = ticketAppointment?.CustomerName
            txtTicketNo.text = ticketAppointment?.Tktno
            txtProductName.text = ticketAppointment?.ProductName
            txtProductIssues.text = ticketAppointment?.ProductIssues
            txtTimeSlot.text = ticketAppointment?.AppointmentDate + ", " + ticketAppointment?.TimeSlot
            txtDateHeader.text = ticketAppointment?.AppointmentDate

            custLatLng = getLocationFromAddress(context,ticketAppointment?.CustAddress?:"-")

            if(custLatLng != null){
                custAddressLat = custLatLng?.latitude.toString()
                custAddressLong = custLatLng?.longitude.toString()
            }

            val result = FloatArray(1)
            Location.distanceBetween(
                currentLat ?: 0.0,
                currentLng ?: 0.0,
                custAddressLat!!.toDouble(),
                custAddressLong!!.toDouble(),
                result
            )
            val distanceInMeters = result[0]

            if(distanceInMeters > 0 && currentLat!! > 0.0 && currentLng!! > 0.0){
                val distanceInKm = String.format("%.2f", distanceInMeters/1000).toDouble()
                txtDistance.text = "Customer is $distanceInKm km away"
            }else{
                txtDistance.text = "Customer distance not found"
            }

            if (ticketAppointment?.isTicketAccepted == true) {
                txtAcceptStatus.visibility = View.INVISIBLE
                imvAcceptStatus.visibility = View.INVISIBLE
            }else {
                txtAcceptStatus.visibility = View.VISIBLE
                imvAcceptStatus.visibility = View.VISIBLE
            }

            rootLayout.setOnClickListener {
                try {
                    it.isClickable = false
                    Executors.newSingleThreadScheduledExecutor().schedule({
                        it.isClickable = true
                    }, 2, TimeUnit.SECONDS)

                    if (ticketAppointment?.isTicketAccepted == true) {
                        callBackListener?.observeCheckInStatus(ticketAppointment.TicketID)
                    }else{
                        context.toast("Please accept ticket no ${ticketAppointment?.Tktno} before visit")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }


            layoutViewDetails.setOnClickListener {
                TicketViewDetailsActivity.start(context, ticketAppointment?.TicketID
                        ?: -1, ticketAppointment?.Tktno)
            }


        }
    }

    override fun getLayout() = R.layout.appointment_item

    override fun initializeViewBinding(view: View): AppointmentItemBinding = AppointmentItemBinding.bind(view)
}