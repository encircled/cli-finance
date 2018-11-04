package cz.encircled.cli.finance.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * @author encircled on 04-Nov-18.
 */
data class TransactionEntry(val id: String, val amount: BigDecimal, val sourceAccount: String, val transactionDate: LocalDate,
                            val transactionNote : String = "",
                            val customNote : String = "",
                            var category: String? = null)
