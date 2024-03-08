package com.dm.loanrequest.integrationtest

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.data.redis.core.StringRedisTemplate
import org.testcontainers.containers.wait.strategy.Wait

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    companion object {
        @Container
        val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:latest")

        @Container
        val redisContainer = GenericContainer<Nothing>("redis:latest").apply {
            withExposedPorts(6379)
            waitingFor(Wait.forListeningPort()) // Wait until Redis is ready
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }
    }


    @AfterEach
    fun tearDown() {
        redisTemplate.connectionFactory.connection.flushDb()
    }

    @Test
    fun `should reject invalid loan request`() {
        val invalidLoanRequestJson = """{"amount": 400.0, "customerFullName": "Jane Doe", "customerId": 2}"""

        mockMvc.perform(post("/loan-requests")
            .contentType("application/json")
            .content(invalidLoanRequestJson))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should create a loan request and retrieve the correct total loan amount`() {
        val customerId = System.nanoTime() // Using nanoTime for unique customerId
        val loanRequestJson = """{"amount": 1000.0, "customerFullName": "John Doe", "customerId": $customerId}"""

        mockMvc.perform(post("/loan-requests")
            .contentType("application/json")
            .content(loanRequestJson))
            .andExpect(status().isOk)

        mockMvc.perform(get("/loan-requests/total-amount/$customerId"))
            .andExpect(status().isOk)
            .andExpect(content().string("1000.0"))
    }

    @Test
    fun `should update total loan amount correctly when new loan is added`() {
        val customerId = System.nanoTime() // Using nanoTime for unique customerId
        val firstLoanRequestJson = """{"amount": 1000.0, "customerFullName": "John Doe", "customerId": $customerId}"""
        val secondLoanRequestJson = """{"amount": 2000.0, "customerFullName": "John Doe", "customerId": $customerId}"""

        mockMvc.perform(post("/loan-requests")
            .contentType("application/json")
            .content(firstLoanRequestJson))
            .andExpect(status().isOk)

        mockMvc.perform(post("/loan-requests")
            .contentType("application/json")
            .content(secondLoanRequestJson))
            .andExpect(status().isOk)

        mockMvc.perform(get("/loan-requests/total-amount/$customerId"))
            .andExpect(status().isOk)
            .andExpect(content().string("3000.0"))
    }
}

