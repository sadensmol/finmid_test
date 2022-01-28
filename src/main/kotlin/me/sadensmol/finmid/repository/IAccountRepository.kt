package me.sadensmol.finmid.repository

import me.sadensmol.finmid.domain.Account
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IAccountRepository : CrudRepository<Account, String> {
}

