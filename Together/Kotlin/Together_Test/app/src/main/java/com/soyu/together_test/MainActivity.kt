package com.soyu.together_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bt_login = findViewById(R.id.bt_login) as Button
        bt_login.setOnClickListener {
            Log.e("!!!", "bt_login setOnClickListener")

            val client = OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                //.sslSocketFactory() - ? нужно ли его указывать дополнительно
                .build()
            val request = Request.Builder()
                .url("ws://steeloz.ddns.net:4877") // 'wss' - для защищенного канала
                .build()
            val wsListener = EchoWebSocketListener ()
            val webSocket = client.newWebSocket(request, wsListener) // this provide to make 'Open ws connection'

        }

    }
}











private class EchoWebSocketListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        output("onOpen ")
        webSocket.send("Hello, it's SSaurel !")
        webSocket.send("What's up ?")
        webSocket.send(ByteString.decodeHex("deadbeef"))
        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        output("Receiving : " + text!!)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        output("Receiving bytes : " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
    }

    companion object {
        private val NORMAL_CLOSURE_STATUS = 1000
    }

    private fun output(txt: String) {
        Log.e("WSS", txt)
    }
}