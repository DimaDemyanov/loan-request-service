package com.dm.loanrequest.service

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.repository.LoanRequestRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class LoanRequestService(
    private val loanRequestRepository: LoanRequestRepository,
    private val redisTemplate: RedisTemplate<String, BigDecimal>
) {
    private val totalLoanAmountKey = "totalLoanAmount"

    fun createLoanRequest(loanRequest: LoanRequest): LoanRequest {
        val savedRequest = loanRequestRepository.save(loanRequest)
        updateTotalInRedis(savedRequest.customerId, BigDecimal.valueOf(savedRequest.amount))
        return savedRequest
    }

    private fun updateTotalInRedis(customerId: Long, amount: BigDecimal) {
        val currentTotal = getTotalLoanAmountByCustomerId(customerId)
        val newTotal = currentTotal.add(amount)
        redisTemplate.opsForValue().set("$totalLoanAmountKey:$customerId", newTotal)
    }

    fun getTotalLoanAmountByCustomerId(customerId: Long): BigDecimal {
        val cacheTotal = redisTemplate.opsForValue().get("$totalLoanAmountKey:$customerId")
        return cacheTotal ?: recalculateTotalLoanAmount(customerId)
    }

    private fun recalculateTotalLoanAmount(customerId: Long): BigDecimal {
        val totalAmount = loanRequestRepository.findByCustomerId(customerId)
            .sumOf { BigDecimal.valueOf(it.amount) }
        redisTemplate.opsForValue().set("$totalLoanAmountKey:$customerId", totalAmount)
        return totalAmount
    }
}
