package com.dm.loanrequest.model

import jakarta.persistence.*

@Entity
@Table(name = "LoanRequest", indexes = [Index(name = "idx_customer_id", columnList = "customerId")])
data class LoanRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val amount: Double = 0.0,
    val customerFullName: String = "",
    val customerId: Long = 0L
)
