package cz.encircled.cli.finance.command

import cz.encircled.cli.finance.FinanceContext
import cz.encircled.cli.finance.InputCommand
import cz.encircled.cli.finance.parser.FileBackingCategoryMatcher


class FetchMappingFileCommand : FinanceCommand {

    override fun requiredArgs(): List<String> = listOf("f")

    override fun doExec(context: FinanceContext, inputCommand: InputCommand): Any {
        val matcher = FileBackingCategoryMatcher(inputCommand.namedArgs["f"]!!)
        context.categoryMatcher.matchers.add(0, matcher)
        context.reloadCategories()
        return "Mapping added: ${matcher.getCategoryMappings()}"
    }

    override fun help(): String = "Fetch file with mapping"

}