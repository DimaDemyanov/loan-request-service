package com.dm.loanrequest.integrationtest

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
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
        val redisContainer: GenericContainer<*> = GenericContainer("redis:latest").withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
            registry.add("spring.redis.host") { redisContainer.host }
            registry.add("spring.redis.port") { redisContainer.firstMappedPort.toString() }
        }
    }

    @BeforeEach
    fun setUp() {
        // Optional: If there's specific setup needed before each test
    }

    @AfterEach
    fun tearDown() {
        // Clear Redis data after each test to ensure test isolation
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

