package com.example.upa_app.presentation.sessiondetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.upa_app.presentation.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session_detail, container, false)
    }
}