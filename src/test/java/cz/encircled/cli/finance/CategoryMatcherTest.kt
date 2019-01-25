package cz.encircled.cli.finance

import cz.encircled.cli.finance.model.TransactionEntry
import cz.encircled.cli.finance.parser.CategoryMatcher
import cz.encircled.cli.finance.parser.DefaultCategoryMatcher
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

class CategoryMatcherTest {

    private val categoryMatcher: CategoryMatcher = DefaultCategoryMatcher()

    @Test
    fun testDefaultCategoryMatcher() {
        listOf(
                Pair("123 GAS station", "GAS"),
                Pair("123 station", null)
        ).forEach {
            assertEquals(it.second, categoryMatcher.getCategory(transactionEntry(it.first)))
        }
    }

    private fun transactionEntry(transactionNote: String = "", customNote: String = ""): TransactionEntry =
            TransactionEntry("", BigDecimal.ONE, "", LocalDate.now(), transactionNote, customNote)

}