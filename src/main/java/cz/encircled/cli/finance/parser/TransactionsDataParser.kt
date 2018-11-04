package cz.encircled.cli.finance.parser

import cz.encircled.cli.finance.model.TransactionEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate

/**
 * @author encircled on 04-Nov-18.
 */
interface TransactionsDataParser {

    fun parseFile(location: String): List<TransactionEntry>

    suspend fun parseTransactionEntry(source: String): TransactionEntry

}

class FioCsvParser : TransactionsDataParser {

    private val dataBegin = 13

    private val chunkSize = 50

    override fun parseFile(location: String): List<TransactionEntry> {
        val lines = File(location).readLines()

        return runBlocking {
            lines.subList(dataBegin, lines.size)
                    .chunked(chunkSize)
                    .map {
                        async {
                            it.map { entry -> parseTransactionEntry(entry) }
                        }
                    }
                    .map { it.await() }
                    .flatten()
        }

    }

    // TODO check order using CSV header
    override suspend fun parseTransactionEntry(source: String): TransactionEntry {
        val split = source.replace("\"", "").split(";")

        return TransactionEntry(split[0], getNumber(split[2]), split[4], getDate(split[1]))
    }

    private fun getNumber(source: String): BigDecimal {
        return BigDecimal(source.replace(",", "."))
    }

    private fun getDate(date: String): LocalDate {
        val split = date.split(".")
        return LocalDate.of(Integer.parseInt(split[2]), Integer.parseInt(split[1]), Integer.parseInt(split[0]))
    }
}