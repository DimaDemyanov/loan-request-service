package com.dm.loanrequest.repository

import com.dm.loanrequest.model.LoanRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoanRequestRepository : JpaRepository<LoanRequest, Long> {
    fun findByCustomerId(customerId: Long): List<LoanRequest>
}
