package com.dm.loanrequest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.service.LoanRequestService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(LoanRequestController::class)
class LoanRequestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var loanRequestService: LoanRequestService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create loan request when valid data is provided`() {
        val loanRequest = LoanRequest(amount = 1000.0, customerFullName = "John Doe", customerId = 1L)
        val loanRequestJson = objectMapper.writeValueAsString(loanRequest)

        mockMvc.perform(post("/loan-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loanRequestJson))
            .andExpect(status().isOk)
    }

    @Test
    fun `should return bad request when amount is less than minimum`() {
        val loanRequest = LoanRequest(amount = 100.0, customerFullName = "John Doe", customerId = 1L) // Invalid amount
        val loanRequestJson = objectMapper.writeValueAsString(loanRequest)

        mockMvc.perform(post("/loan-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loanRequestJson))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return bad request when customer name is missing`() {
        val loanRequest = LoanRequest(amount = 1000.0, customerFullName = "", customerId = 1L) // Missing customer name
        val loanRequestJson = objectMapper.writeValueAsString(loanRequest)

        mockMvc.perform(post("/loan-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loanRequestJson))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return bad request when amount exceeds maximum`() {
        val loanRequest = LoanRequest(amount = 15000.0, customerFullName = "John Doe", customerId = 1L) // Exceeding amount
        val loanRequestJson = objectMapper.writeValueAsString(loanRequest)

        mockMvc.perform(post("/loan-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loanRequestJson))
            .andExpect(status().isBadRequest)
    }
}
