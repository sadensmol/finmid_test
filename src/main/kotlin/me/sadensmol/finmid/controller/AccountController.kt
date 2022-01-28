package me.sadensmol.finmid.controller

import me.sadensmol.finmid.controller.AccountController.Companion.URL
import me.sadensmol.finmid.domain.Account
import me.sadensmol.finmid.domain.NotFoundException
import me.sadensmol.finmid.repository.IAccountRepository
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
@RequestMapping(URL)
class AccountController(
    private val accountRepository: IAccountRepository,
) {

    companion object {
        internal const val URL = "/account"
    }

    @PostMapping
    fun create(@Valid @RequestBody createAccountRequest: CreateAccountRequest): Account {
        return accountRepository.save(Account(balance = createAccountRequest.balance))
    }

    @GetMapping("/{accountId}")
    fun get(@PathVariable("accountId") accountId: String): Account {
        return accountRepository.findById(accountId).orElseThrow { NotFoundException() }
    }

    data class CreateAccountRequest(
        val balance: BigDecimal,
    )
}