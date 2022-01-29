package me.sadensmol.finmid.repository

import me.sadensmol.finmid.domain.Account
import me.sadensmol.finmid.domain.Transaction
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ITransactionRepository : CrudRepository<Transaction, String> {
}

