package cz.encircled.cli.finance

import cz.encircled.cli.finance.model.TransactionEntry
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

class ConsolePrinterTest {

    private val consolePrinter: ConsolePrinter = ConsolePrinter()

    @Test
    fun testNamedAttributes() {
        val namedArgs = mapOf(Pair("p", "amount,id"))
        assertResult("| amount: 12 | id: 123 |", namedArgs)
    }

    @Test
    fun testEmptyAlias() {
        val namedArgs = mapOf(Pair("p", "amount_as_,id_as_"))
        assertResult("| 12 | 123 |", namedArgs)
    }

    @Test
    fun testAlias() {
        val namedArgs = mapOf(Pair("p", "amount_as_a,id_as_i"))
        assertResult("| a: 12 | i: 123 |", namedArgs)
    }

    private fun assertResult(result: String, namedArgs: Map<String, String>) {
        val entry = TransactionEntry("123", BigDecimal(12), "123", LocalDate.now())
        val pattern = consolePrinter.pattern(InputCommand("cat list", namedArgs), entry)
        assertEquals(result, pattern)
    }

}