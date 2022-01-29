package me.sadensmol.finmid.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import me.sadensmol.finmid.domain.Account
import me.sadensmol.finmid.domain.NotEnoughAmountException
import me.sadensmol.finmid.domain.NotFoundException
import me.sadensmol.finmid.domain.Transaction
import me.sadensmol.finmid.service.TransactionService
import me.sadensmol.finmid.utils.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(TransactionController::class)
class TransactionControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val transactionService: TransactionService,
) {

    @TestConfiguration
    class TestConfig {
        @Bean
        fun transactionService() = mockk<TransactionService>()
    }

    @BeforeEach
    fun setUpEach() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDownEach() {
        confirmVerified(transactionService)
    }

    @Test
    fun `Given transaction request when receive it then create transaction`() {
        coEvery { transactionService.transfer(any(), any(), any()) } returns TEST_TRANSACTION

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post(TransactionController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_CREATE_TRANSACTION_REQUEST))
        )
            .andExpect(status().isOk)
            .andReturn().response.getContentAsString()

        assertEquals(TEST_TRANSACTION, objectMapper.readValue(response, Transaction::class.java))
        coVerify(exactly = 1) {
            transactionService.transfer(
                from = TEST_ACCOUNT_ID, to = TEST_ACCOUNT_ID2, amount = TEST_AMOUNT
            )
        }
    }

    @Test
    fun `Given transaction request with zero amount when receive it then return bad request`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(TransactionController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_CREATE_TRANSACTION_REQUEST.copy(amount= BigDecimal.ZERO)))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `Given transaction request when receive it and any error occurred then return server error`() {
        coEvery { transactionService.transfer(any(), any(), any()) } throws Exception()

        mockMvc.perform(
            MockMvcRequestBuilders.post(TransactionController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_CREATE_TRANSACTION_REQUEST))
        )
            .andExpect(status().is5xxServerError)

        coVerify(exactly = 1) {
            transactionService.transfer(
                from = TEST_ACCOUNT_ID, to = TEST_ACCOUNT_ID2, amount = TEST_AMOUNT
            )
        }
    }

    @Test
    fun `Given transaction request with non existent account when receive it then return not found`() {
        coEvery { transactionService.transfer(any(), any(), any()) } throws NotFoundException()

        mockMvc.perform(
            MockMvcRequestBuilders.post(TransactionController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_CREATE_TRANSACTION_REQUEST))
        )
            .andExpect(status().isNotFound)

        coVerify(exactly = 1) {
            transactionService.transfer(
                from = TEST_ACCOUNT_ID, to = TEST_ACCOUNT_ID2, amount = TEST_AMOUNT
            )
        }
    }

    @Test
    fun `Given transaction request with amount higher that avail funds when receive it then return bad request`() {
        coEvery { transactionService.transfer(any(), any(), any()) } throws NotEnoughAmountException()

        mockMvc.perform(
            MockMvcRequestBuilders.post(TransactionController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_CREATE_TRANSACTION_REQUEST))
        )
            .andExpect(status().isBadRequest)

        coVerify(exactly = 1) {
            transactionService.transfer(
                from = TEST_ACCOUNT_ID, to = TEST_ACCOUNT_ID2, amount = TEST_AMOUNT
            )
        }
    }



}