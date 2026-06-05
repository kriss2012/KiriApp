package com.kiriplatform.app.utils

object AppConfig {
    /**
     * The primary backend server URL.
     * Ensure this ends with a trailing slash for Retrofit.
     */
    const val BASE_URL = "https://kiriapp.onrender.com/api/"

    /**
     * The unified Socket.io server URL.
     */
    const val SOCKET_URL = "https://kiriapp.onrender.com"

    /**
     * The official community website.
     */
    const val WEBSITE_URL = "https://kiriorg.netlify.app/"

    /**
     * Timeout configuration for network requests (in seconds).
     */
    const val NETWORK_TIMEOUT = 60L
}
