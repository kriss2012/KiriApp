package com.kiriplatform.app

import com.kiriplatform.app.data.remote.ApiClient
import org.junit.Test
import org.junit.Assert.assertNotNull

class ApiTest {
    @Test
    fun testRetrofitInitialization() {
        val service = ApiClient.service
        assertNotNull(service)
        println("Retrofit initialized successfully.")
    }
}
