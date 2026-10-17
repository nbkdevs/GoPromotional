package uk.co.softrank.eventplanner.util

object AppConstants {
    val EVENT_TYPES = listOf(
        "Conference",
        "Exhibition",
        "Trade Show",
        "Corporate Event",
        "Networking Event",
        "Product Launch",
        "Charity Event",
        "University Event",
        "Birthday",
        "Wedding",
        "Other"
    )

    val TASK_CATEGORIES = listOf(
        "Venue",
        "Catering",
        "Marketing",
        "Invitations",
        "Equipment",
        "Staffing",
        "Entertainment",
        "Decorations",
        "Merchandise",
        "Transport",
        "Other"
    )

    val TASK_PRIORITIES = listOf("High", "Medium", "Low")

    val EXPENSE_CATEGORIES = listOf(
        "Venue",
        "Catering",
        "Marketing",
        "Equipment",
        "Decorations",
        "Entertainment",
        "Staffing",
        "Merchandise",
        "Transport",
        "Other"
    )

    val MERCH_STATUSES = listOf("Idea", "Quoted", "Ordered", "Received", "Distributed")

    val MERCH_STARTERS = listOf(
        "Water bottles",
        "Tote bags",
        "Travel mugs",
        "Notebooks",
        "Pens",
        "Lanyards",
        "Badges",
        "Other"
    )

    data class DefaultTask(val title: String, val category: String, val priority: String)

    val DEFAULT_TASKS = listOf(
        DefaultTask("Confirm venue availability and capacity", "Venue", "High"),
        DefaultTask("Draft run-of-show agenda", "Other", "High"),
        DefaultTask("Set catering headcount and dietary options", "Catering", "High"),
        DefaultTask("Prepare guest invitation list", "Invitations", "Medium"),
        DefaultTask("Design and send invitations", "Invitations", "Medium"),
        DefaultTask("Book audiovisual and staging equipment", "Equipment", "Medium"),
        DefaultTask("Plan staffing roles and briefings", "Staffing", "Medium"),
        DefaultTask("Arrange decorations and wayfinding", "Decorations", "Low"),
        DefaultTask("Select event merchandise items", "Merchandise", "Medium"),
        DefaultTask("Confirm transport and parking details", "Transport", "Low"),
        DefaultTask("Publish marketing schedule", "Marketing", "Medium"),
        DefaultTask("Book entertainment or speakers", "Entertainment", "Medium")
    )
}
