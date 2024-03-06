package com.dm.loanrequest.controller

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.service.LoanRequestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/loan-requests")
class LoanRequestController(private val loanRequestService: LoanRequestService) {

    @PostMapping
    fun createLoanRequest(@RequestBody loanRequest: LoanRequest): ResponseEntity<LoanRequest> =
        ResponseEntity.ok(loanRequestService.createLoanRequest(loanRequest))

    @GetMapping("/total-amount/{customerId}")
    fun getTotalLoanAmountByCustomerId(@PathVariable customerId: Long): ResponseEntity<Double> =
        ResponseEntity.ok(loanRequestService.getTotalLoanAmountByCustomerId(customerId))
}
