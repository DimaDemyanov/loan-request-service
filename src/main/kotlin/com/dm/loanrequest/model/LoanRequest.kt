package com.dm.loanrequest.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.DecimalMax

@Entity
data class LoanRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull
    @DecimalMin("500.0")
    @DecimalMax("12000.50")
    val amount: Double,

    @NotNull
    val customerFullName: String,

    @NotNull
    val customerId: Long
)
