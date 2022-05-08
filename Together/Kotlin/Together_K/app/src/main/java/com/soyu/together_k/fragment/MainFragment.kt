package com.soyu.together_k.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import com.soyu.together_k.MainActivity
import com.soyu.together_k.R

class MainFragment(val main: MainActivity) : Fragment() {
    lateinit var lv_roomlist: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        lv_roomlist = view.findViewById(R.id.lv_roomlist) as ListView
        lv_roomlist.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            Log.e("!!!", "lv_roomlist onItemClickListener");
            main.requestChatjoin(i)
        }

        val bt_roomcreate = view.findViewById(R.id.bt_roomcreate) as Button
        bt_roomcreate.setOnClickListener {

            main.addRoomCreateFagment(RoomCreateFragment(main))
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        main.requestChatlist()
    }


}
