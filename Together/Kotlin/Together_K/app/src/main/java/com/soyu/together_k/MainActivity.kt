package com.soyu.together_k

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.soyu.together_k.data.TogetherData
import com.soyu.together_k.fragment.ChatFragment
import com.soyu.together_k.fragment.LoginFragment
import com.soyu.together_k.fragment.MainFragment
import com.soyu.together_k.fragment.RoomCreateFragment
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private val CLOSURE_STATUS_NORMAL = 1000
    private val CLOSURE_STATUS_LOGIN_FAIL_ID_DUPLICATE = 4901
    private val CLOSURE_STATUS_LOGIN_FAIL_DB_ERROR = 4902
    private val CLOSURE_STATUS_APP_DESTORY = 4999

    private lateinit var webSocket: WebSocket

    lateinit var context: Context

    private var loginId: String = ""
    private var loginSeq: Long = 0
    private var roomName: String = ""
    private var roomSeq: Long = 0

    private lateinit var layout_title: LinearLayout
    private lateinit var tv_title: TextView

    lateinit var mainFragment: MainFragment
    lateinit var chatFragment: ChatFragment
    lateinit var roomCreateFragment: RoomCreateFragment
    lateinit var roomlist: Array<String>

    private var roomCreateFragmentFlag: Boolean = false

    fun getLoginId(): String { return loginId }
    fun getLoginSeq(): Long { return loginSeq }
    fun getRoomName(): String { return roomName }
    fun getRoomSeq(): Long { return roomSeq }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        layout_title = findViewById(R.id.layout_title) as LinearLayout
        tv_title = findViewById(R.id.tv_title) as TextView

        viewLoginFragment()
    }
    override fun onDestroy() {
        if(::webSocket.isInitialized) {
            webSocket?.close(CLOSURE_STATUS_APP_DESTORY, "Android App Destory")
        }
        super.onDestroy()
    }

    fun replaceContentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainview, fragment)
            .commit()
    }
    fun addContentFragment(fragment: Fragment) {
        Log.e("!!", "addContentFragment")
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.show_from_right, R.animator.hide_to_rigth)
            .add(R.id.mainview, fragment)
            .commit()

        if (roomCreateFragmentFlag) {
            roomCreateFragmentFlag = false
            supportFragmentManager.beginTransaction()
                .remove(roomCreateFragment).commit()
        }

    }

    fun websocketConnection() {
        Log.e("!!!", "websocketConnection")

        var client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            //.sslSocketFactory()
            .build()
        var request: Request = Request.Builder().url("ws://211.216.5.235:4877").build()
        var wsListener: EchoWebSocketListener = EchoWebSocketListener(this)
        webSocket = client.newWebSocket(request, wsListener) // this provide to make 'Open ws connection'
    }

    fun userIdLogin(id: String) {
        CloseKeyboard()
        loginId = id
        websocketConnection()
    }
    fun userIdLoginFail(code: Int, reason: String?) {
        webSocket.close(code, reason)
    }
    private fun LoginComplete() {
        Log.e("!!!", "LoginComplete")
        Log.e("!!!", "LoginId :: "+loginId)
        Log.e("!!!", "loginSeq :: "+loginSeq)
        viewMainFragment()
    }


    fun requestChatlist() {
        var sendData: TogetherData = TogetherData()
        sendData.TogetherData("19", getLoginSeq(), "10", "0001", 0, "", "0701")
        sendMessage(sendData.toString())
    }
    fun requestChatjoin(idx: Int) {
        roomName = roomlist[idx]
        Log.e("!!!", "[requestChatjoin] roomName :: $roomName")
        val sendData = TogetherData()
        sendData.TogetherData("19", getLoginSeq(), "10", "0003", roomName.length, roomName, "0701")
        sendData.dataEncode()
        sendMessage(sendData.toString())
    }
    fun requestChatjoin(room_Name: String) {
        CloseKeyboard()
        roomCreateFragmentFlag = true

        roomName = room_Name
        val sendData = TogetherData()
        sendData.TogetherData("19", getLoginSeq(), "10", "0003", roomName.length, roomName, "0701")
        sendData.dataEncode()
        sendMessage(sendData.toString())
    }
    fun requestChatMemberList() {
        val sendData: TogetherData = TogetherData()
        sendData.TogetherData("19", getLoginSeq(), "02", "0001", getRoomSeq().toString().length, "" + getRoomSeq().toString(), "0701")
        sendData.dataEncode()
        sendMessage(sendData.toString())
    }
    fun requestChatexit() {
        CloseKeyboard()
        val sendData: TogetherData = TogetherData()
        sendData.TogetherData("19", getLoginSeq(), "10", "0005", getRoomName().length, getRoomName(), "0701")
        sendData.dataEncode()
        sendMessage(sendData.toString())

    }

    private fun responseMemberList(data: String) {
        runOnUiThread {
            chatFragment.responseMemberList(data)
        }
    }
    private fun recvmessage(recvMessage: TogetherData) {
        runOnUiThread() {
            chatFragment.recvmessage(recvMessage)
        }
    }

    private fun viewLoginFragment() {
        runOnUiThread {
            tv_title.setText("Login")
            replaceContentFragment(LoginFragment(this))
        }
    }
    private fun viewMainFragment() {
        mainFragment = MainFragment(this)
        runOnUiThread {
            tv_title.setText("Together - "+loginId)
            replaceContentFragment(mainFragment)
        }
    }
    fun viewChatFragment() {
        Log.e("!!!", "viewChatFragment")
        chatFragment = ChatFragment(this)
        runOnUiThread {
            tv_title.setText(roomName)
            addContentFragment(chatFragment)
        }
    }
    private fun chatFragmentExit() {
        Log.e("!!!", "chatFragmentExit")
        runOnUiThread {
            tv_title.text = "Together - ${getLoginId()}"
            removeFragment(chatFragment)
        }
    }

    fun CloseKeyboard() {
        var view = this.currentFocus
        if(view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    fun sendMessage(str: String) {
        Log.e("sendMessage", "str = $str")
        webSocket.send(str)
    }

    fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.show_from_right, R.animator.hide_to_rigth)
            .remove(fragment).commit()

        if(fragment is RoomCreateFragment || fragment is ChatFragment) {
            requestChatlist()
        }
    }

    fun addRoomCreateFagment(fragment: RoomCreateFragment) {
        roomCreateFragment = fragment
        addContentFragment(fragment)
    }

    fun requestChatMessageSend(sendmsg: String) {
        val sendMessage: String = "${getLoginId()}>$sendmsg"


        val sendData: TogetherData = TogetherData()
        sendData.TogetherData("19", getLoginSeq(), "01", String.format("%04d", getRoomSeq()), sendMessage.length, sendMessage, "0701")
        sendData.dataEncode()
        sendMessage(sendData.toString())
    }


    private class EchoWebSocketListener(val main:MainActivity) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            output("onOpen ")
            var id = main.getLoginId();
            var sendData: TogetherData = TogetherData()
            sendData.TogetherData("19", 0, "00", "0001", id.length, id, "0701")
            sendData.dataEncode()
            Log.e("!!!", "userIdLogin id :: $sendData")
            webSocket.send(sendData.toString())
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            output("Receiving : " + text!!)
            var recvMessage: TogetherData = TogetherData()
            recvMessage.TogetherData(text)
            recvMessage.dataDecode()

            output("onMessage recvMessage :: " + recvMessage)
            when(recvMessage.getCmd()) {
                "00" -> {   // login and logout
                    when(recvMessage.getSubcmd()) {
                        "0002" -> {
                            when(recvMessage.getData()) {
                                "ID Duplicate" -> {
                                    main.loginId = ""
                                    main.userIdLoginFail(main.CLOSURE_STATUS_LOGIN_FAIL_ID_DUPLICATE, "ID_DUPLICATE")
                                }
                                "DB ERROR" -> {
                                    main.loginId = ""
                                    main.userIdLoginFail(main.CLOSURE_STATUS_LOGIN_FAIL_DB_ERROR, "DB_ERROR")
                                }
                                else -> {
                                    output("Login complete")
                                    main.loginSeq = recvMessage.getData().toLong()
                                    main.LoginComplete();
                                }
                            }
                        }
                    }
                }
                "01" -> {
                    main.recvmessage(recvMessage)
                }
                "02" -> {
                    when(recvMessage.getSubcmd()) {
                        "0002" -> {
                            main.responseMemberList(recvMessage.getData())
                        }
                    }
                }
                "10" -> {
                    when(recvMessage.getSubcmd()) {
                        "0002" -> {
                            Log.e("onMessage", "[chat_list] return")

                            main.runOnUiThread {
                                if(recvMessage.getData().equals("")) {
                                    main.mainFragment.lv_roomlist.visibility = View.GONE
                                } else {
                                    main.mainFragment.lv_roomlist.visibility = View.VISIBLE
                                    main.roomlist = recvMessage.getData().split("\\|".toRegex()).toTypedArray()
                                    var prodAdapter = ArrayAdapter<String>(main.context, android.R.layout.simple_list_item_1, main.roomlist);
                                    main.mainFragment.lv_roomlist.adapter = prodAdapter
                                }
                            }
                        }
                        "0004" -> {
                            main.roomSeq = recvMessage.getData().toLong()
                            main.viewChatFragment()
                        }
                        "0006" -> {
                            main.chatFragmentExit()
                        }
                    }
                }
            }

        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            output("Receiving bytes : " + bytes!!.hex())
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            webSocket!!.close(main.CLOSURE_STATUS_NORMAL, null)
            output("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            output("Error : " + t.message)
        }

        private fun output(txt: String) {
            Log.e("WSS", txt)
        }
    }


}





