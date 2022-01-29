package me.sadensmol.finmid.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import me.sadensmol.finmid.domain.Account
import me.sadensmol.finmid.repository.IAccountRepository
import me.sadensmol.finmid.utils.TEST_ACCOUNT
import me.sadensmol.finmid.utils.TEST_ACCOUNT_ID
import me.sadensmol.finmid.utils.TEST_CREATE_ACCOUNT_REQUEST
import me.sadensmol.finmid.utils.TEST_WRONG_ACCOUNT_ID
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(AccountController::class)
class AccountControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val accountRepository: IAccountRepository,
) {

    @TestConfiguration
    class TestConfig {
        @Bean
        fun accountRepository() = mockk<IAccountRepository>()
    }

    @BeforeEach
    fun setUpEach() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDownEach() {
        confirmVerified(accountRepository)
    }

    @Test
    fun `Given create account request when receive it then create account`() {
        coEvery { accountRepository.save(any()) } returns TEST_ACCOUNT

        val response = mockMvc.perform(
            post(AccountController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_CREATE_ACCOUNT_REQUEST))
        )
            .andExpect(
                status().isOk
            )
            .andReturn().response.getContentAsString()

        assertEquals(TEST_ACCOUNT, objectMapper.readValue(response, Account::class.java))
        coVerify(exactly = 1) { accountRepository.save(TEST_ACCOUNT.copy(accountId = null)) }
    }

    @Test
    fun `Given create account request when receive it and any error occurred then return server error`() {
        coEvery { accountRepository.save(any()) } throws Exception()

        mockMvc.perform(
            post(AccountController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_CREATE_ACCOUNT_REQUEST))
        )
            .andExpect(status().is5xxServerError)
        coVerify(exactly = 1) { accountRepository.save(TEST_ACCOUNT.copy(accountId = null)) }
    }

    @Test
    fun `Given bad create account request when receive it then return bad request`() {
        mockMvc.perform(
            post(AccountController.URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andExpect(status().isBadRequest)
    }


    @Test
    fun `Given get account request when receive it then return account`() {
        coEvery { accountRepository.findById(any()) } returns Optional.of(TEST_ACCOUNT)

        val response = mockMvc.perform(get("${AccountController.URL}/$TEST_ACCOUNT_ID"))
            .andExpect(status().isOk)
            .andReturn().response.getContentAsString()

        assertEquals(TEST_ACCOUNT, objectMapper.readValue(response, Account::class.java))

        coVerify(exactly = 1) { accountRepository.findById(TEST_ACCOUNT_ID) }
    }


    @Test
    fun `Given get account request with wrong account id when receive it then return not found`() {
        coEvery { accountRepository.findById(any()) } returns Optional.empty()

        mockMvc.perform(get("${AccountController.URL}/$TEST_WRONG_ACCOUNT_ID"))
            .andExpect(status().isNotFound)

        coVerify(exactly = 1) { accountRepository.findById(TEST_WRONG_ACCOUNT_ID) }
    }

    @Test
    fun `Given get account request when receive it and error occurred then return server error`() {
        coEvery { accountRepository.findById(any()) } throws Exception()

        mockMvc.perform(get("${AccountController.URL}/$TEST_WRONG_ACCOUNT_ID"))
            .andExpect(status().is5xxServerError)
        coVerify(exactly = 1) { accountRepository.findById(TEST_WRONG_ACCOUNT_ID) }
    }
}