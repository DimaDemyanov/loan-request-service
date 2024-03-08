package com.dm.loanrequest.service

import com.dm.loanrequest.model.LoanRequest
import com.dm.loanrequest.repository.LoanRequestRepository
import org.springframework.stereotype.Service

@Service
open class LoanRequestService(private val loanRequestRepository: LoanRequestRepository) {
    fun createLoanRequest(loanRequest: LoanRequest): LoanRequest = loanRequestRepository.save(loanRequest)

    fun getTotalLoanAmountByCustomerId(customerId: Long): Double =
        loanRequestRepository.findByCustomerId(customerId).sumOf { it.amount }
}
