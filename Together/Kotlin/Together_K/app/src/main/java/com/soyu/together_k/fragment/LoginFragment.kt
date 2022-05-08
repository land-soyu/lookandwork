package com.soyu.together_k.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.soyu.together_k.MainActivity
import com.soyu.together_k.R

class LoginFragment(val main:MainActivity) : Fragment() {

    lateinit var et_loginid: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        et_loginid = view.findViewById(R.id.et_loginid) as EditText
        val btn_click_me = view.findViewById(R.id.bt_login) as Button
        btn_click_me.setOnClickListener {
            var id = if(et_loginid.text.toString().equals("")) "soyu" else et_loginid.text.toString()
            Log.e("!!!", "bt_login setOnClickListener id :: "+id)
            main.userIdLogin(id)
        }
        return view
    }


}
