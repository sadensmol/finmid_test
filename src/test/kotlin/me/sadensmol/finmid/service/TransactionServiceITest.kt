package me.sadensmol.finmid.service

import kotlinx.coroutines.*
import me.sadensmol.finmid.domain.Transaction
import me.sadensmol.finmid.repository.IAccountRepository
import me.sadensmol.finmid.utils.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.system.measureTimeMillis


@SpringBootTest
class TransactionServiceITest(
    @Autowired private val accountRepository: IAccountRepository,
    @Autowired private val transactionService: TransactionService,
) {
    companion object {
        const val NUMBER_REQUESTS = 1000
        const val NUMBER_PARALLEL_REQUESTS = 10

        @JvmStatic
        val redis: GenericContainer<*> = GenericContainer<Nothing>(DockerImageName.parse("docker.io/bitnami/redis:latest")).apply {
            withEnv("ALLOW_EMPTY_PASSWORD", "yes")
            withExposedPorts(6379)
            start()
        }

        @JvmStatic
        val mongo: GenericContainer<*> = GenericContainer<Nothing>(DockerImageName.parse("docker.io/bitnami/mongodb:4.0")).apply {
            withEnv("MONGODB_DATABASE","finmid")
            withExposedPorts(27017)
            start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun setUp(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") { "mongodb://${mongo.host}:${mongo.firstMappedPort}/finmid" }
            registry.add("spring.redis.host", redis::getHost)
            registry.add("spring.redis.port", redis::getFirstMappedPort)
        }
    }

    @AfterEach
    fun tearDownEach() {
        accountRepository.deleteAll()
    }


    @Test
    fun `Given two account when transfer money from one to another then they successfully transferred`() {
        accountRepository.save(TEST_ACCOUNT)
        accountRepository.save(TEST_ACCOUNT_2)

        val tx = transactionService.transfer(TEST_ACCOUNT_ID, TEST_ACCOUNT_ID_2, TEST_AMOUNT)
        assertTx(tx, TEST_ACCOUNT_ID, TEST_ACCOUNT_ID_2)

        val accountAfter1 = accountRepository.findByIdOrNull(TEST_ACCOUNT_ID)
        val accountAfter2 = accountRepository.findByIdOrNull(TEST_ACCOUNT_ID_2)

        Assertions.assertEquals(TEST_BALANCE - TEST_AMOUNT, accountAfter1?.balance)
        Assertions.assertEquals(TEST_BALANCE_2 + TEST_AMOUNT, accountAfter2?.balance)
    }

    private fun assertTx(tx: Transaction, from: String, to: String) {
        Assertions.assertNotNull(tx.ixId)
        Assertions.assertEquals(from, tx.from)
        Assertions.assertEquals(to, tx.to)
        Assertions.assertEquals(TEST_AMOUNT, tx.amount)

    }

    @Test
    fun `Given two account when transfer money sequentially from one to another then they successfully transferred`() {
        accountRepository.save(TEST_ACCOUNT)
        accountRepository.save(TEST_ACCOUNT_2)


        val time = measureTimeMillis {
            repeat(NUMBER_REQUESTS) {
                val tx = transactionService.transfer(TEST_ACCOUNT_ID, TEST_ACCOUNT_ID_2, TEST_AMOUNT)
                val tx2 = transactionService.transfer(TEST_ACCOUNT_ID_2, TEST_ACCOUNT_ID, TEST_AMOUNT)
                assertTx(tx, TEST_ACCOUNT_ID, TEST_ACCOUNT_ID_2)
                assertTx(tx2, TEST_ACCOUNT_ID_2, TEST_ACCOUNT_ID)
            }
        }

        println("sequential executed in $time millis")

        val accountAfter1 = accountRepository.findByIdOrNull(TEST_ACCOUNT_ID)
        val accountAfter2 = accountRepository.findByIdOrNull(TEST_ACCOUNT_ID_2)

        Assertions.assertEquals(TEST_BALANCE, accountAfter1?.balance)
        Assertions.assertEquals(TEST_BALANCE_2, accountAfter2?.balance)
    }


    @Test
    fun `Given two account when transfer money in parallel from one to another then they successfully transferred`() {
        val initialAmount = BigDecimal.valueOf(NUMBER_REQUESTS* NUMBER_PARALLEL_REQUESTS*2L);


        accountRepository.save(TEST_ACCOUNT.copy(balance = initialAmount))
        accountRepository.save(TEST_ACCOUNT_2.copy(balance = initialAmount))

        val time = measureTimeMillis {
            repeat(NUMBER_REQUESTS/NUMBER_PARALLEL_REQUESTS) {
            runBlocking(Dispatchers.IO) {
                repeat(NUMBER_PARALLEL_REQUESTS) {
                    launch {
                        val tx = transactionService.transfer(TEST_ACCOUNT_ID, TEST_ACCOUNT_ID_2, TEST_AMOUNT)
                        assertTx(tx, TEST_ACCOUNT_ID, TEST_ACCOUNT_ID_2)
                    }
                    launch {
                        val tx2 = transactionService.transfer(TEST_ACCOUNT_ID_2, TEST_ACCOUNT_ID, TEST_AMOUNT)
                        assertTx(tx2, TEST_ACCOUNT_ID_2, TEST_ACCOUNT_ID)
                    }
                }
            }
        }}

        println("parallel executed in $time millis")

        val accountAfter1 = accountRepository.findByIdOrNull(TEST_ACCOUNT_ID)
        val accountAfter2 = accountRepository.findByIdOrNull(TEST_ACCOUNT_ID_2)

        Assertions.assertEquals(initialAmount, accountAfter1?.balance)
        Assertions.assertEquals(initialAmount, accountAfter2?.balance)
    }

}
