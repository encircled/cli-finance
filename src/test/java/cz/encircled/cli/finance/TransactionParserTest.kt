package cz.encircled.cli.finance

import cz.encircled.cli.finance.model.TransactionEntry
import cz.encircled.cli.finance.parser.DefaultCategoryMatcher
import cz.encircled.cli.finance.parser.FioCsvParser
import cz.encircled.cli.finance.parser.TransactionsDataParser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

/**
 * @author encircled on 04-Nov-18.
 */
class TransactionParserTest {

    private val parser: TransactionsDataParser = FioCsvParser(DefaultCategoryMatcher())

    @Test
    fun testParseFile() {
        val path = "${System.getProperty("user.dir")}\\src\\test\\resources\\test.data.csv"
//        val path = "C:/Users/encir/Downloads/Pohyby_na_uctu-2400232467_20160901-20181104.csv"

        val start = System.nanoTime()
        val parsed = parser.parseFile(path)

        assertEquals(3, parsed.size)
        assertEquals(TransactionEntry("1", BigDecimal(-6000), "101010177", LocalDate.of(2018, 9, 3)), parsed[0])

        println("Elapsed: " + ((System.nanoTime() - start) / 1_000_000) + " ms")
    }

    @Test
    fun testParseSingleTransaction() {
        runBlocking {
            val entry = parser.parseTransactionEntry("\"123\";\"04.07.2018\";\"-6200\";\"CZK\";\"101010188\";\"\";\"0300\";\"ČSOB, a.s.\";\"0558\";\"777010\";\"\";\"customPoplatky\";\"poplatky\";\"Bezhotovostní platba\";\"\";\"\";\"\";\"\";\"1234567\"")
            assertEquals("123", entry.id)
            assertEquals("101010188", entry.sourceAccount)
            assertEquals("poplatky", entry.transactionNote)
            assertEquals("customPoplatky", entry.customNote)
            assertEquals(BigDecimal(-6200), entry.amount)
            assertEquals(LocalDate.of(2018, 7, 4), entry.transactionDate)
        }
    }

}