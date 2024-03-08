package com.dm.loanrequest.controller

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.service.LoanRequestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/loan-requests")
class LoanRequestController(private val loanRequestService: LoanRequestService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun createLoanRequest(@RequestBody loanRequest: LoanRequest): ResponseEntity<Any> {
        logger.info("Received request to create loan request: {}", loanRequest)
        val validationErrors = validateLoanRequest(loanRequest)
        return if (validationErrors.isEmpty()) {
            val createdLoanRequest = loanRequestService.createLoanRequest(loanRequest)
            logger.info("Loan request created successfully: {}", createdLoanRequest)
            ResponseEntity.ok(createdLoanRequest)
        } else {
            logger.warn("Invalid loan request received: {}", validationErrors)
            ResponseEntity.badRequest().body(validationErrors)
        }
    }

    @GetMapping("/total-amount/{customerId}")
    fun getTotalLoanAmountByCustomerId(@PathVariable customerId: Long): ResponseEntity<BigDecimal> {
        logger.info("Received request to get total loan amount for customer {}", customerId)
        val totalLoanAmount = loanRequestService.getTotalLoanAmountByCustomerId(customerId)
        logger.info("Total loan amount for customer {}: {}", customerId, totalLoanAmount)
        return ResponseEntity.ok(totalLoanAmount)
    }

    private fun validateLoanRequest(loanRequest: LoanRequest): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (loanRequest.amount < 500.0 || loanRequest.amount > 12000.50) {
            errors["amount"] = "The amount must be between 500 and 12000.50"
        }

        if (loanRequest.customerFullName.isBlank()) {
            errors["customerFullName"] = "Customer full name must not be blank"
        }

        return errors
    }
}
