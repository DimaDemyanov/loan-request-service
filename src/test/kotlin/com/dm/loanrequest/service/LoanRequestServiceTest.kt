package com.dm.loanrequest.service

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.repository.LoanRequestRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.math.BigDecimal

class LoanRequestServiceTest {

    private val loanRequestRepository: LoanRequestRepository = mock()
    private val redisTemplate: RedisTemplate<String, BigDecimal> = mock()
    private val valueOperations: ValueOperations<String, BigDecimal> = mock()
    private val loanRequestService = LoanRequestService(loanRequestRepository, redisTemplate)

    @Test
    fun `should retrieve total loan amount from Redis when present`() {
        val customerId = 1L
        val totalLoanAmount = BigDecimal("3000.0")
        val totalLoanAmountKey = "totalLoanAmount:$customerId"

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.get(totalLoanAmountKey)).thenReturn(totalLoanAmount)

        val totalAmount = loanRequestService.getTotalLoanAmountByCustomerId(customerId)

        assertEquals(totalLoanAmount, totalAmount)
        verify(valueOperations).get(totalLoanAmountKey)
        verifyNoInteractions(loanRequestRepository)
    }

    @Test
    fun `should calculate and update total loan amount in Redis when not present`() {
        val customerId = 2L
        val totalLoanAmountKey = "totalLoanAmount:$customerId"
        val loanRequests = listOf(
            LoanRequest(id = 1, amount = 1000.0, customerFullName = "Jane Doe", customerId = customerId),
            LoanRequest(id = 2, amount = 2000.0, customerFullName = "Jane Doe", customerId = customerId)
        )

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.get(totalLoanAmountKey)).thenReturn(null)
        whenever(loanRequestRepository.findByCustomerId(customerId)).thenReturn(loanRequests)
        doNothing().`when`(valueOperations).set(any(), any())

        val totalAmount = loanRequestService.getTotalLoanAmountByCustomerId(customerId)

        assertEquals(BigDecimal("3000.0"), totalAmount)
        verify(valueOperations).set(eq(totalLoanAmountKey), any())
        verify(loanRequestRepository).findByCustomerId(customerId)
    }

    @Test
    fun `should update total loan amount in Redis when new loan request is created`() {
        val customerId = 3L
        val loanRequest = LoanRequest(id = null, amount = 1500.0, customerFullName = "Alice", customerId = customerId)
        val savedLoanRequest = LoanRequest(id = 3, amount = 1500.0, customerFullName = "Alice", customerId = customerId)
        val totalLoanAmountKey = "totalLoanAmount:$customerId"
        val initialTotal = BigDecimal("1000.0") // Assume an initial amount for demonstration

        whenever(loanRequestRepository.save(any())).thenReturn(savedLoanRequest)
        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)
        whenever(valueOperations.get(totalLoanAmountKey)).thenReturn(initialTotal)
        doNothing().`when`(valueOperations).set(eq(totalLoanAmountKey), any())

        loanRequestService.createLoanRequest(loanRequest)

        verify(loanRequestRepository).save(loanRequest)
        verify(valueOperations).set(eq(totalLoanAmountKey), eq(initialTotal.add(BigDecimal(1500.0))))
    }
}
