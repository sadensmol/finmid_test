package me.sadensmol.finmid.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import kotlinx.coroutines.sync.Mutex
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "Account")
data class Account(
    @Id
    val accountId: String? = null,

    val balance: BigDecimal,


    ) {

    /**
     * Mutex to perform all operations on the account
     * In distributed system should be changed to distributed lock
     */
    @Transient
    @JsonIgnore
    val mutex: Mutex = Mutex()
}

