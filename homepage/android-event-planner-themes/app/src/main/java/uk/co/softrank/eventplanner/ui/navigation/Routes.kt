package uk.co.softrank.eventplanner.ui.navigation

object Routes {
    const val HOME = "home"
    const val EVENTS = "events"
    const val EVENT_DETAIL = "event_detail/{eventId}"
    const val EVENT_EDIT = "event_edit/{eventId}"
    const val CHECKLIST = "checklist"
    const val TASK_EDIT = "task_edit/{taskId}"
    const val THEMES = "themes"
    const val THEME_DETAIL = "theme_detail/{themeId}"
    const val BUDGET = "budget"
    const val EXPENSE_EDIT = "expense_edit/{expenseId}"
    const val MERCHANDISE = "merchandise"
    const val MERCH_EDIT = "merch_edit/{itemId}"

    fun eventDetail(id: Long) = "event_detail/$id"
    fun eventEdit(id: Long? = null) = "event_edit/${id ?: -1L}"
    fun taskEdit(id: Long? = null) = "task_edit/${id ?: -1L}"
    fun themeDetail(id: String) = "theme_detail/$id"
    fun expenseEdit(id: Long? = null) = "expense_edit/${id ?: -1L}"
    fun merchEdit(id: Long? = null) = "merch_edit/${id ?: -1L}"
}
