package com.dm.loanrequest.controller

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.service.LoanRequestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/loan-requests")
class LoanRequestController(private val loanRequestService: LoanRequestService) {

    @PostMapping
    fun createLoanRequest(@RequestBody loanRequest: LoanRequest): ResponseEntity<Any> {
        val validationErrors = validateLoanRequest(loanRequest)
        return if (validationErrors.isEmpty()) {
            ResponseEntity.ok(loanRequestService.createLoanRequest(loanRequest))
        } else {
            ResponseEntity.badRequest().body(validationErrors)
        }
    }

    fun getTotalLoanAmountByCustomerId(@PathVariable customerId: Long): ResponseEntity<Double> =
        ResponseEntity.ok(loanRequestService.getTotalLoanAmountByCustomerId(customerId))

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