package me.sadensmol.finmid.controller

import me.sadensmol.finmid.controller.TransactionController.Companion.URL
import me.sadensmol.finmid.domain.InvalidAmountValueException
import me.sadensmol.finmid.domain.Transaction
import me.sadensmol.finmid.service.TransactionService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping(URL)
class TransactionController(
private val transactionService: TransactionService
) {

    companion object {
        internal const val URL = "/transaction"
    }

    @PostMapping
    fun create(@Valid @RequestBody createTransactionRequest: CreateTransactionRequest): Transaction {
        if (createTransactionRequest.amount <= BigDecimal.ZERO) throw InvalidAmountValueException("Amount should be higher than 0")
        return transactionService.transfer(createTransactionRequest.from, createTransactionRequest.to, createTransactionRequest.amount)
    }

    @Validated
    data class CreateTransactionRequest(
        val amount: BigDecimal,
        @field:NotBlank
        val from: String,
        @field:NotBlank
        val to: String,
    )
}