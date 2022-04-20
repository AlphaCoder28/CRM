package com.goldmedal.crm.ui.ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.TicketActivityData
import com.goldmedal.crm.databinding.HistoryTabFragmentBinding
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM = "ticket_id"
private const val ARG_PARAM1 = "ticket_status"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ActivityTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActivityTabFragment : Fragment() , KodeinAware, ApiStageListener<Any> {


    private var ticketId:Int = -1
    private var ticketStatus:String = ""
    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()

    private lateinit var viewModel: TicketViewModel
    private var _binding: HistoryTabFragmentBinding? = null
    private val binding get() = _binding!!


//    private var requestsList: ArrayList<LeaveRequestsData>? = null
private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: TicketActivityItem
//<!--added by akshay-->
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ticketId = it.getInt(ARG_PARAM)
            ticketStatus = it.getString(ARG_PARAM1,"")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = HistoryTabFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)

       // attFragmentBinding.viewmodel = viewModel

        viewModel.apiListener = this
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.getTicketActivity(ticketId = ticketId)
            }
        })
    }

//    private fun List<TicketActivityData?>.toLeaveRequests(): List<TicketActivityItem?> {
//        return this.map {
//            TicketActivityItem(it)
//        }
//    }


    private fun bindUI(list: List<TicketActivityData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(list)
        }
    }


    private fun initRecyclerView(list : List<TicketActivityData?>) {
        if (!list.isNullOrEmpty()) {
            mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.rvList.layoutManager = mLayoutManager
            mAdapter = TicketActivityItem(list,ticketStatus)
            binding.rvList.adapter = mAdapter
        }



    }
//    private fun initRecyclerView(toLeaveRecord: List<TicketActivityItem?>) {
//        mAdapter = GroupAdapter<GroupieViewHolder>().apply {
//            addAll(toLeaveRecord)
//        }
//
//        binding.rvList.apply {
//            layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
////            mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
////            setHasFixedSize(true)
//       //     addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
//            adapter = mAdapter
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ActivityTabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(ticketID: Int, ticketStatus: String?) =
            ActivityTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, ticketID )
                    putString(ARG_PARAM1, ticketStatus)
                }
            }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.viewCommon.hide()


        val data = _object as List<TicketActivityData?>
        if (data.isNullOrEmpty()) {
            binding.viewCommon.showNoData()
        }
        bindUI(data)
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (isNetworkError) {
            binding.viewCommon.showNoInternet()
        } else {
            binding.viewCommon.showNoData()
        }

       // bindUI(ArrayList())
        binding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }
}