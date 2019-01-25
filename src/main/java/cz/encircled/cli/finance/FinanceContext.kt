package cz.encircled.cli.finance

import cz.encircled.cli.finance.command.*
import cz.encircled.cli.finance.model.TransactionEntry
import cz.encircled.cli.finance.parser.DefaultCategoryMatcher
import cz.encircled.cli.finance.parser.DelegatingCategoryMatcher
import cz.encircled.cli.finance.parser.FioCsvParser
import cz.encircled.cli.finance.parser.TransactionsDataParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import kotlin.collections.ArrayList


class FinanceContext(private val consolePrinter: ConsolePrinter = ConsolePrinter()) {

    var categoryMatcher: DelegatingCategoryMatcher = DelegatingCategoryMatcher(ArrayList(listOf(DefaultCategoryMatcher())))

    var transactionsDataParser: TransactionsDataParser = FioCsvParser(categoryMatcher)

    var dataSet: List<TransactionEntry> = listOf()

    fun exec(input: String): String {
        val inputCommand = parseCommand(input)

        val result = when (inputCommand.commandName) {
            "f", "fetch" -> FetchFileCommand().exec(this, inputCommand)
            "m", "mapping" -> FetchMappingFileCommand().exec(this, inputCommand)
            "save" -> PersistMappingCommand().exec(this, inputCommand)
            "cat missing", "cat miss" -> ListWithMissingCategoryCommand().exec(this, inputCommand)
            "cat list", "list cat" -> ListCategoriesCommand().exec(this, inputCommand)
            "cat describe", "describe cat" -> DescribeCategoryCommand().exec(this, inputCommand)
            else -> "Unknown command '${inputCommand.commandName}'. Please, use one of 'f/fetch'"

        }

        return toPrettyFormat(inputCommand, result)
    }

    fun reloadCategories() {
        dataSet.chunked(50)
                .map { chunk ->
                    GlobalScope.async {
                        chunk.forEach {
                            it.category = categoryMatcher.getCategory(it)
                        }
                    }
                }
    }

    fun parseCommand(input: String): InputCommand {
        val tokens = input
                .split(" ")
                .filter { it.isNotEmpty() }

        val commandName = tokens.filter { !it.startsWith("-") }
                .joinToString(" ")

        val namedArgs = tokens
                .filter { it.startsWith("-") }
                .associateBy(
                        {
                            if (it.contains("="))
                                it.split("=")[0].substring(1)
                            else
                                it.substring(1)
                        },
                        {
                            if (it.contains("="))
                                it.split("=")[1]
                            else
                                ""
                        }
                )

        return InputCommand(commandName, namedArgs)
    }

    private fun toPrettyFormat(inputCommand: InputCommand, output: Any): String {
        val total = total(inputCommand, output)

        val result = sort(inputCommand,
                limit(inputCommand, output))

        return total + consolePrinter.pattern(inputCommand, result)
    }

    private fun sort(inputCommand: InputCommand, output: Any): Any {
        if (output is List<*> && output.isNotEmpty() && output[0] is TransactionEntry) {
            val entries = output as List<TransactionEntry>
            return when (inputCommand.namedArgs["s"]) {
                "transactionNote" -> entries.sortedByDescending { it.transactionNote }
                "customNote" -> entries.sortedBy { it.customNote }
                else -> entries.sortedBy { it.amount }
            }
        }

        return output
    }

    private fun limit(inputCommand: InputCommand, output: Any): Any {
        if (output is List<*>) {
            val max = inputCommand.namedArgs["max"]?.toInt() ?: output.size
            val min = inputCommand.namedArgs["min"]?.toInt() ?: 0

            return output.subList(min, max)
        }

        return output
    }


    private fun total(inputCommand: InputCommand, output: Any): String {
        return if (output is List<*> && inputCommand.namedArgs.containsKey("t")) {
            return "Total count: ${output.size}\n"
        } else ""
    }

}

data class InputCommand(val commandName: String, val namedArgs: Map<String, String>)

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val context = FinanceContext()

    if (args.isNotEmpty()) {
        args.joinToString(" ").split(";").forEach {
            println(context.exec(it))
        }
    }

    while (true) {
        val input = scanner.nextLine()
        if (input == "exit") {
            break
        }
        println(context.exec(input))
    }

}