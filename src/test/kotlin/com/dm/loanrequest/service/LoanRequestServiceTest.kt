package com.dm.loanrequest.service

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.repository.LoanRequestRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LoanRequestServiceTest {

    private val loanRequestRepository: LoanRequestRepository = mock()
    private val loanRequestService = LoanRequestService(loanRequestRepository)

    @Test
    fun `should calculate total loan amount for a customer`() {
        val customerId = 1L
        val loanRequests = listOf(
            LoanRequest(id = 1, amount = 1000.0, customerFullName = "John Doe", customerId = customerId),
            LoanRequest(id = 2, amount = 2000.0, customerFullName = "John Doe", customerId = customerId)
        )

        whenever(loanRequestRepository.findByCustomerId(customerId)).thenReturn(loanRequests)

        val totalAmount = loanRequestService.getTotalLoanAmountByCustomerId(customerId)

        assertEquals(3000.0, totalAmount)
    }
}