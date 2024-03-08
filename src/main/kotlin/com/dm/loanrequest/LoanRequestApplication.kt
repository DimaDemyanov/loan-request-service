package com.dm.loanrequest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.dm.loanrequest"])
open class LoanRequestApplication

fun main(args: Array<String>) {
    runApplication<LoanRequestApplication>(*args)
}
