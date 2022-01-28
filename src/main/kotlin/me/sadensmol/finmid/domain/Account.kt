package me.sadensmol.finmid.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "Account")
data class Account(
    @Id
    val accountId: String? = null,
    val balance: BigDecimal,
)

