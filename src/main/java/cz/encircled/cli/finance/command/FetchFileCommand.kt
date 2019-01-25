package cz.encircled.cli.finance.command

import cz.encircled.cli.finance.FinanceContext
import cz.encircled.cli.finance.InputCommand
import java.math.BigDecimal

class FetchFileCommand : FinanceCommand {

    override fun requiredArgs(): List<String> = listOf("f")

    override fun doExec(context: FinanceContext, inputCommand: InputCommand): String {
        val parsed = context.transactionsDataParser.parseFile(inputCommand.namedArgs["f"]!!)
        context.dataSet = parsed.filter { it.amount < BigDecimal.ZERO }

        return "Fetched data from ${inputCommand.namedArgs["f"]}"
    }

    override fun help(): String =
            "Fetch data file. First argument is file location"
}