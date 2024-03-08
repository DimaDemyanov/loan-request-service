package com.dm.loanrequest.service

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.repository.LoanRequestRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal


@Service
open class LoanRequestService(
     open val loanRequestRepository: LoanRequestRepository,
     open val redisTemplate: RedisTemplate<String, BigDecimal>
) {
    companion object {
        private val totalLoanAmountKey = "totalLoanAmount"
        private val logger = LoggerFactory.getLogger(LoanRequestService::class.java)
    }

    @Transactional
    open fun createLoanRequest(loanRequest: LoanRequest): LoanRequest {
        try {
            val savedRequest = loanRequestRepository.save(loanRequest)
            logger.info("Saved loan request: {}", savedRequest)
            updateTotalInRedis(savedRequest.customerId, BigDecimal.valueOf(savedRequest.amount))

            return savedRequest
        } catch (e: Exception) {
            logger.error("Error creating loan request: ${e.message}")
            throw e
        }
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
