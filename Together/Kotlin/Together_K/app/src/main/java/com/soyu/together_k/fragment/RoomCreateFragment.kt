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
class RoomCreateFragment(val main: MainActivity) : Fragment() {
    lateinit var roomCreateFragment: RoomCreateFragment
    lateinit var et_roomname: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomCreateFragment = this;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_room_create, container, false)

        et_roomname = view.findViewById(R.id.et_roomname) as EditText
        val bt_back_roomcreate = view.findViewById(R.id.bt_back_roomcreate) as Button
        bt_back_roomcreate.setOnClickListener {
            main.removeFragment(roomCreateFragment)
        }
        val bt_roomjoin = view.findViewById(R.id.bt_roomjoin) as Button
        bt_roomjoin.setOnClickListener {
            main.requestChatjoin(et_roomname.text.toString())
        }


        return view
    }
}
