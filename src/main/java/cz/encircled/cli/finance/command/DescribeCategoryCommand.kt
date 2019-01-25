package cz.encircled.cli.finance.command

import cz.encircled.cli.finance.FinanceContext
import cz.encircled.cli.finance.InputCommand
import cz.encircled.cli.finance.model.TransactionEntry

class DescribeCategoryCommand : FinanceCommand {

    override fun requiredArgs(): List<String> = listOf("c")

    override fun doExec(context: FinanceContext, inputCommand: InputCommand): Any {
        val category = inputCommand.namedArgs["c"]!!

        return if (inputCommand.namedArgs.containsKey("g")) {
            val min = inputCommand.namedArgs["g"]!!.toInt()
            context.dataSet.filter { it.category == category }
                    .groupBy({ getSubCat(it, min) }, { it.amount })
                    .map { Pair(it.key, it.value.sumByDouble { amount -> amount.toDouble() }) }
                    .sortedBy { it.second }
        } else {
            context.dataSet.filter { it.category == category }
        }
    }

    private fun getSubCat(it: TransactionEntry, min: Int): String {
        return if (it.transactionNote.isEmpty()) {
            it.customNote.substring(0, Math.min(it.customNote.length, min))
        } else it.transactionNote.substring(0, Math.min(it.transactionNote.length, min))

    }

    override fun help(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}