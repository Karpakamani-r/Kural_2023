package com.w2c.kural.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.w2c.kural.databinding.FragmentNotificationEducationBinding
import com.w2c.kural.utils.NOTIFICATION_REQ_CODE
import com.w2c.kural.utils.POST_NOTIFICATIONS

class NoticeFragment : DialogFragment() {
    private lateinit var binding: FragmentNotificationEducationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationEducationBinding.inflate(inflater)
        initView()
        return binding.root
    }

    fun initView() {
        binding.ivClose.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnOk.setOnClickListener {
            findNavController().popBackStack()
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(POST_NOTIFICATIONS),
                NOTIFICATION_REQ_CODE
            )
        }
    }
}