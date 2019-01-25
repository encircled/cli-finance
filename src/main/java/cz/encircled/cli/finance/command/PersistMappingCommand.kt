package cz.encircled.cli.finance.command

import cz.encircled.cli.finance.FinanceContext
import cz.encircled.cli.finance.InputCommand
import cz.encircled.cli.finance.parser.FileBackingCategoryMatcher

class PersistMappingCommand : FinanceCommand {

    override fun requiredArgs(): List<String> = listOf()

    override fun doExec(context: FinanceContext, inputCommand: InputCommand): Any {
        val fileMatcher = context.categoryMatcher.matchers.firstOrNull { it is FileBackingCategoryMatcher } as FileBackingCategoryMatcher?
        return if (fileMatcher == null) "Fetched file mapping is required"
        else {
            fileMatcher.persistToFile(inputCommand.namedArgs)
            context.reloadCategories()
            return "Saved"
        }
    }

    override fun help() = "Arg -category=pattern"

}