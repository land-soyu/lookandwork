package com.soyu.together_k.data

import android.util.Base64
import java.io.UnsupportedEncodingException

class TogetherData {
    private var start: String = ""
    private var user_seq: String = ""
    private var cmd: String = ""
    private var subcmd: String = ""
    private var data_length: Int = 0
    private var data: String = ""
    private var end: String = ""


    fun TogetherData(s: String) {
        var str = s
        start = str.substring(0, 2)
        str = str.substring(2)
        user_seq = str.substring(0, 4)
        str = str.substring(4)
        cmd = str.substring(0, 2)
        str = str.substring(2)
        subcmd = str.substring(0, 4)
        str = str.substring(4)
        data_length = str.substring(0, 4).toInt(16)
        str = str.substring(4)
        data = str.substring(0, data_length)
        str = str.substring(data_length)
        end = str.substring(0, 4)
    }

    fun TogetherData(start: String?, user_seq: Long, cmd: String?, subcmd: String?, data_length: Int, data: String?, end: String?) {
        this.start = start!!
        this.user_seq = String.format("%04d", user_seq)
        this.cmd = cmd!!
        this.subcmd = subcmd!!
        this.data_length = data_length
        this.data = data!!
        this.end = end!!
    }


    public fun getCmd(): String { return cmd }
    public fun getSubcmd(): String { return subcmd }
    public fun getData(): String { return data }

    override fun toString(): String {
        return start + user_seq + cmd + subcmd + String.format(
            "%04X",
            data_length
        ) + data + end
    }

    fun dataDecode() {
        try {
            data = decode(data)!!
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    fun dataEncode() {
        try {
            data = encode(data)!!
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        data_length = data.length
    }

    @Throws(UnsupportedEncodingException::class)
    fun encode(text: String): String? {
        var text = text
        text = text.trim { it <= ' ' }
        val data = text.toByteArray(Charsets.UTF_8)
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    @Throws(UnsupportedEncodingException::class)
    fun decode(text: String): String? {
        var text = text
        text = text.trim { it <= ' ' }
        return String(Base64.decode(text, Base64.NO_WRAP), Charsets.UTF_8)
    }

}