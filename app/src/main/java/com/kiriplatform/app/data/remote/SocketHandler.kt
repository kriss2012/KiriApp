package com.kiriplatform.app.data.remote

import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketHandler {
    private var mSocket: Socket? = null

    @Synchronized
    fun setSocket(baseUrl: String) {
        if (mSocket == null) {
            try {
                val opts = IO.Options()
                opts.forceNew = true
                opts.reconnection = true
                mSocket = IO.socket(baseUrl, opts)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    fun getSocket(): Socket? {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket?.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket?.disconnect()
    }

    fun joinRoom(roomId: String) {
        mSocket?.emit("join_room", roomId)
    }

    /**
     * PERMANENT FIX: Consolidated global listeners to prevent overwrites.
     */
    fun setupGlobalListeners(
        onNotification: (JSONObject) -> Unit,
        onMessage: (JSONObject) -> Unit,
        onConnectionAccepted: (JSONObject) -> Unit,
        onMatchSuggested: (JSONObject) -> Unit
    ) {
        mSocket?.let { socket ->
            // Clear existing to avoid leaks/duplicates
            socket.off("new_notification")
            socket.off("receive_message")
            socket.off("connection_accepted")
            socket.off("match_suggested")

            socket.on("new_notification") { args ->
                val data = args.getOrNull(0) as? JSONObject
                data?.let { onNotification(it) }
            }

            socket.on("receive_message") { args ->
                val data = args.getOrNull(0) as? JSONObject
                data?.let { onMessage(it) }
            }

            socket.on("connection_accepted") { args ->
                val data = args.getOrNull(0) as? JSONObject
                data?.let { onConnectionAccepted(it) }
            }

            socket.on("match_suggested") { args ->
                val data = args.getOrNull(0) as? JSONObject
                data?.let { onMatchSuggested(it) }
            }
        }
    }

    fun listenForMessages(onMessage: (JSONObject) -> Unit) {
        mSocket?.on("receive_message") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as? JSONObject
                data?.let { onMessage(it) }
            }
        }
    }

    fun sendMessage(roomId: String, senderId: String, receiverId: String, content: String) {
        val data = JSONObject().apply {
            put("roomId", roomId)
            put("senderId", senderId)
            put("receiverId", receiverId)
            put("content", content)
        }
        mSocket?.emit("send_message", data)
    }
}
