package me.sadensmol.finmid.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "Transaction")
data class Transaction(
    @Id
    val ixId: String? = null,
    val amount: BigDecimal,
    @Indexed
    val from: String,
    @Indexed
    val to: String,
)

