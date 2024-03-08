package com.dm.loanrequest.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class LoanRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val amount: Double = 0.0,
    val customerFullName: String = "",
    val customerId: Long = 0L
)
