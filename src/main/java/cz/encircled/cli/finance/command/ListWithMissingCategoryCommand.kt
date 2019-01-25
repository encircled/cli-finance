package cz.encircled.cli.finance.command

import cz.encircled.cli.finance.FinanceContext
import cz.encircled.cli.finance.InputCommand
import cz.encircled.cli.finance.model.TransactionEntry

class ListWithMissingCategoryCommand : FinanceCommand {

    override fun requiredArgs(): List<String> = listOf()

    override fun doExec(context: FinanceContext, inputCommand: InputCommand): List<TransactionEntry> {
        return context.dataSet
                .filter { it.category.isNullOrBlank() }
    }

    override fun help(): String =
            "List transactions with missing category"
}