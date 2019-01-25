package cz.encircled.cli.finance.command

import cz.encircled.cli.finance.FinanceContext
import cz.encircled.cli.finance.InputCommand

class ListCategoriesCommand : FinanceCommand {

    override fun requiredArgs(): List<String> = listOf()

    override fun doExec(context: FinanceContext, inputCommand: InputCommand): List<Pair<String, Double>> {
        return context.dataSet
                .groupBy({ it.category ?: "OTHER" }, { it.amount })
                .map { Pair(it.key, it.value.sumByDouble { amount -> amount.toDouble() }) }
                .sortedBy { it.second }
    }

    override fun help(): String =
            "List aggregated sum per category"


}