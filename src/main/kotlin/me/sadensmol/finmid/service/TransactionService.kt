package me.sadensmol.finmid.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import me.sadensmol.finmid.domain.Account
import me.sadensmol.finmid.domain.NotEnoughAmountException
import me.sadensmol.finmid.domain.NotFoundException
import me.sadensmol.finmid.domain.Transaction
import me.sadensmol.finmid.repository.IAccountRepository
import me.sadensmol.finmid.repository.ITransactionRepository
import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@Service
class TransactionService(
    private val redisLockRegistry: RedisLockRegistry,
    private val accountRepository: IAccountRepository,
    private val transactionRepository: ITransactionRepository,
) {

    companion object {
        val TIMEOUT_SEC = 5L
    }

    /**
     * Transfers money from [from] account to [to] account.
     * Returns [NotFoundException] if any account is not found
     * Returns [NotEnoughAmountException] if money amount exceeds the [from] account balance
     *
     * The simplest way here to lock 2 accounts directly in the database,
     * perform transaction and then unlock them
     * MongoDb does support transactions since 4.0 (but only in enterprise version),
     * and also it locks the whole collection.
     *
     * We are using Redis here to perform only required accounts locking,
     * and it also suitable for distributed systems
     */
    fun transfer(from: String, to: String, amount: BigDecimal): Transaction {

        val fromLock = redisLockRegistry.obtain(from)
        val toLock = redisLockRegistry.obtain(to)

        fromLock.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)
        val fromAccount = accountRepository.findById(from).orElseThrow { NotFoundException("$from account is not found") }
        if (fromAccount.balance < amount) throw NotEnoughAmountException("Not enough balance on account $from to perform transaction")

        toLock.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)
        val toAccount = accountRepository.findById(to).orElseThrow { NotFoundException("$to account is not found") }

        accountRepository.save(toAccount.copy(balance = toAccount.balance + amount))
        accountRepository.save(fromAccount.copy(balance = fromAccount.balance - amount))

        return transactionRepository.save(Transaction(amount = amount, from = from, to = to))


    }
}