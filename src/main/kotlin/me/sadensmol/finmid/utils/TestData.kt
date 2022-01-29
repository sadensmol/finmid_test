package me.sadensmol.finmid.utils

import me.sadensmol.finmid.controller.AccountController
import me.sadensmol.finmid.controller.TransactionController
import me.sadensmol.finmid.domain.Account
import me.sadensmol.finmid.domain.Transaction
import java.math.BigDecimal

val TEST_BALANCE = BigDecimal.TEN
val TEST_BALANCE_2 = BigDecimal.ONE
val TEST_AMOUNT = BigDecimal.ONE
val TEST_ACCOUNT_ID = "test_account_id"
val TEST_ACCOUNT_ID_2 = "test_account_id_2"
val TEST_WRONG_ACCOUNT_ID = "test_wrong_account_id"

val TEST_ACCOUNT = Account(accountId = TEST_ACCOUNT_ID, balance = TEST_BALANCE)
val TEST_ACCOUNT_2 = Account(accountId = TEST_ACCOUNT_ID_2, balance = TEST_BALANCE_2)
val TEST_CREATE_ACCOUNT_REQUEST = AccountController.CreateAccountRequest(balance = TEST_BALANCE)

val TEST_TRANSACTION_ID = "test_transaction_id"

val TEST_TRANSACTION = Transaction(
    ixId = TEST_TRANSACTION_ID,
    from = TEST_ACCOUNT_ID, to = TEST_ACCOUNT_ID_2, amount = TEST_BALANCE)
val TEST_CREATE_TRANSACTION_REQUEST = TransactionController.CreateTransactionRequest(
    from = TEST_ACCOUNT_ID,
    to = TEST_ACCOUNT_ID_2,
    amount = TEST_AMOUNT
)
