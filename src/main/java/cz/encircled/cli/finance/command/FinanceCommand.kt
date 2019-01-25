package cz.encircled.cli.finance.command

import cz.encircled.cli.finance.FinanceContext
import cz.encircled.cli.finance.InputCommand

interface FinanceCommand {

    fun requiredArgs(): List<String>

    fun exec(context: FinanceContext, inputCommand: InputCommand): Any {
        if (requiredArgs().any { !inputCommand.namedArgs.containsKey(it) }) {
            return "Expected [${requiredArgs()}] args, see help:\n${help()}"
        }

        return doExec(context, inputCommand)
    }

    fun doExec(context: FinanceContext, inputCommand: InputCommand): Any

    fun help(): String

}