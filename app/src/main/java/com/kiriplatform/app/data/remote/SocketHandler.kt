package com.kiriplatform.app.data.remote

import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketHandler {
    private var mSocket: Socket? = null
    private var currentToken: String? = null
    private var lastBaseUrl: String? = null

    @Synchronized
    fun setSocket(baseUrl: String, token: String? = null) {
        lastBaseUrl = baseUrl
        currentToken = token
        
        if (mSocket == null) {
            try {
                val opts = IO.Options()
                opts.forceNew = true
                opts.reconnection = true
                
                // Add authentication token if available
                token?.let {
                    val auth = JSONObject()
                    auth.put("token", it)
                    opts.auth = mapOf("token" to it) // For newer Socket.io
                    // Some servers expect it in query or extraHeaders
                    opts.query = "token=$it"
                }

                mSocket = IO.socket(baseUrl, opts)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    fun updateToken(newToken: String) {
        if (currentToken == newToken) return
        
        currentToken = newToken
        // If socket exists, we might need to reconnect with new credentials
        mSocket?.let { socket ->
            val wasConnected = socket.connected()
            closeConnection()
            mSocket = null
            lastBaseUrl?.let { setSocket(it, newToken) }
            if (wasConnected) establishConnection()
        }
    }

    @Synchronized
    fun getSocket(): Socket? {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        if (mSocket?.connected() == false) {
            mSocket?.connect()
        }
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
