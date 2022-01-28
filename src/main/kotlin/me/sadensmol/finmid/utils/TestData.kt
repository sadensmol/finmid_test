package me.sadensmol.finmid.utils

import me.sadensmol.finmid.controller.AccountController
import me.sadensmol.finmid.domain.Account
import java.math.BigDecimal

val TEST_BALANCE  = BigDecimal.TEN
val TEST_ACCOUNT_ID  = "test_account_id"
val TEST_WRONG_ACCOUNT_ID  = "test_wrong_account_id"

val TEST_ACCOUNT  = Account(accountId = TEST_ACCOUNT_ID, balance = TEST_BALANCE)
val TEST_CREATE_ACCOUNT_REQUEST  = AccountController.CreateAccountRequest(balance = TEST_BALANCE)