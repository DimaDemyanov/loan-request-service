package com.dm.loanrequest.service

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.repository.LoanRequestRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal

import org.slf4j.LoggerFactory

@Service
class LoanRequestService(
    private val loanRequestRepository: LoanRequestRepository,
    private val redisTemplate: RedisTemplate<String, BigDecimal>
) {
    private val totalLoanAmountKey = "totalLoanAmount"

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createLoanRequest(loanRequest: LoanRequest): LoanRequest {
        val savedRequest = loanRequestRepository.save(loanRequest)
        logger.info("Saved loan request: {}", savedRequest)
        updateTotalInRedis(savedRequest.customerId, BigDecimal.valueOf(savedRequest.amount))
        return savedRequest
    }

    private fun updateTotalInRedis(customerId: Long, amount: BigDecimal) {
        val currentTotal = redisTemplate.opsForValue().get("$totalLoanAmountKey:$customerId")
        val newTotal = currentTotal?.add(amount) ?: recalculateTotalLoanAmount(customerId)
        logger.debug("Updating total loan amount for customer {} to {}", customerId, newTotal)
        redisTemplate.opsForValue().set("$totalLoanAmountKey:$customerId", newTotal)
    }

    fun getTotalLoanAmountByCustomerId(customerId: Long): BigDecimal {
        val cacheTotal = redisTemplate.opsForValue().get("$totalLoanAmountKey:$customerId")
        logger.debug("Retrieved total loan amount for customer {}: {}", customerId, cacheTotal)
        return cacheTotal ?: recalculateTotalLoanAmount(customerId)
    }

    private fun recalculateTotalLoanAmount(customerId: Long): BigDecimal {
        val totalAmount = loanRequestRepository.findByCustomerId(customerId)
            .sumOf { BigDecimal.valueOf(it.amount) }
        logger.info("Recalculated total loan amount for customer {}: {}", customerId, totalAmount)
        redisTemplate.opsForValue().set("$totalLoanAmountKey:$customerId", totalAmount)
        return totalAmount
    }
}
