package com.soyu.together_k.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.soyu.together_k.MainActivity

import com.soyu.together_k.R
import com.soyu.together_k.data.TogetherData

class ChatFragment(val main: MainActivity) : Fragment() {
    lateinit var tv_recvmsg: TextView
    lateinit var et_sendmsg: EditText
    lateinit var sv_recvmsgscroll: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        tv_recvmsg = view.findViewById(R.id.tv_recvmsg) as TextView
        et_sendmsg = view.findViewById(R.id.et_sendmsg) as EditText
        sv_recvmsgscroll = view.findViewById(R.id.sv_recvmsgscroll) as ScrollView

        val bt_chatexit = view.findViewById(R.id.bt_chatexit) as Button
        bt_chatexit.text = "[ ${main.getRoomName()} ] 방 나가기"
        bt_chatexit.setOnClickListener {
            main.requestChatexit()
        }
        val bt_chatmember = view.findViewById(R.id.bt_chatmember) as Button
        bt_chatmember.setOnClickListener {
            main.requestChatMemberList()
        }
        val bt_sendmsg = view.findViewById(R.id.bt_sendmsg) as Button
        bt_sendmsg.setOnClickListener {
            if(!et_sendmsg.text.toString().equals("")) {
                main.requestChatMessageSend(et_sendmsg.text.toString())
                et_sendmsg.setText("")
            }
        }

        return view
    }

    fun responseMemberList(data: String) {
        Log.e("!!!", "data :: $data")
        var memberlists: Array<String> = data.split("\\|".toRegex()).toTypedArray()
        var memberlist: String = ""
        for(member in memberlists) {
            Log.e("!!!", "member :: $member")
            if(!member.equals("")) {
                memberlist += member +", "
            }
        }
        tv_recvmsg.append("[ 채팅 참가자 ] ${memberlist.substring(0, memberlist.length-2)}\n")
        scrollToEnd()
    }

    private fun scrollToEnd() {
        sv_recvmsgscroll.post {
            sv_recvmsgscroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun recvmessage(message: TogetherData) {
        tv_recvmsg.append("${message.getData()}\n")
        scrollToEnd()
    }

}
