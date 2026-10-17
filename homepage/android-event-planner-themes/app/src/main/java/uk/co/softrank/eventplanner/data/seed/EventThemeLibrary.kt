package uk.co.softrank.eventplanner.data.seed

data class EventTheme(
    val id: String,
    val name: String,
    val description: String,
    val decorIdeas: List<String>,
    val colorStyle: String,
    val invitationIdeas: List<String>,
    val signageIdeas: List<String>,
    val activityIdeas: List<String>,
    val merchandiseIdeas: List<String>
)

object EventThemeLibrary {
    val themes: List<EventTheme> = listOf(
        EventTheme(
            id = "modern_minimal",
            name = "Modern & Minimal",
            description = "Clean lines, open space, and restrained detail for a calm professional atmosphere.",
            decorIdeas = listOf(
                "Use a limited furniture palette with simple silhouettes",
                "Keep floral arrangements sparse and architectural",
                "Leave clear circulation paths between stations"
            ),
            colorStyle = "Neutral base (white, soft grey, charcoal) with one accent colour used sparingly.",
            invitationIdeas = listOf(
                "Sans-serif typography with generous whitespace",
                "Single accent colour for headings or RSVP buttons",
                "Short agenda preview instead of dense copy"
            ),
            signageIdeas = listOf(
                "Floor stickers with directional arrows",
                "Slim freestanding boards with large readable type",
                "Consistent icon set for rooms and amenities"
            ),
            activityIdeas = listOf(
                "Quiet networking corners with timed conversation prompts",
                "Short demo stations with clear start times",
                "Photo area with a single strong backdrop"
            ),
            merchandiseIdeas = listOf(
                "Matte notebooks",
                "Minimal tote bags",
                "Simple metal pens"
            )
        ),
        EventTheme(
            id = "corporate",
            name = "Corporate",
            description = "Polished and structured for conferences, board briefings, and client days.",
            decorIdeas = listOf(
                "Branded stage backdrop with tidy cable management",
                "Uniform table settings and name-card stands",
                "Reception desk with clear badge pickup flow"
            ),
            colorStyle = "Deep navy or forest green with white and metallic accents.",
            invitationIdeas = listOf(
                "Formal subject line and calendar attach",
                "Speaker line-up summary",
                "Dress code and arrival guidance"
            ),
            signageIdeas = listOf(
                "Registration lane markers",
                "Session room door plaques",
                "Agenda boards near coffee stations"
            ),
            activityIdeas = listOf(
                "Keynote plus breakout rooms",
                "Structured networking rounds",
                "Q&A with moderated questions"
            ),
            merchandiseIdeas = listOf(
                "Lanyards and badges",
                "Branded notebooks",
                "Travel mugs for all-day sessions"
            )
        ),
        EventTheme(
            id = "luxury",
            name = "Luxury",
            description = "Elevated finishes and careful pacing for premium launches and celebrations.",
            decorIdeas = listOf(
                "Layered lighting with warm uplights",
                "Linen, glassware, and low centrepieces",
                "Entrance moment with a defined welcome path"
            ),
            colorStyle = "Champagne, deep black, ivory, and soft gold highlights.",
            invitationIdeas = listOf(
                "Letterpress-style digital card",
                "RSVP deadline and guest plus-one rules",
                "Venue map with arrival window"
            ),
            signageIdeas = listOf(
                "Elegant seating charts",
                "Subtle menu cards",
                "Host/hostess identification cards"
            ),
            activityIdeas = listOf(
                "Welcome drink reception",
                "Curated tasting or product reveal",
                "Live acoustic set during mingling"
            ),
            merchandiseIdeas = listOf(
                "Premium tote bags",
                "Insulated travel mugs",
                "Gift-ready notebooks"
            )
        ),
        EventTheme(
            id = "eco_friendly",
            name = "Eco-Friendly",
            description = "Low-waste choices and reusable materials that still feel intentional.",
            decorIdeas = listOf(
                "Potted plants instead of cut-flower waste where possible",
                "Reusable tableware stations",
                "Clear recycling and compost signage"
            ),
            colorStyle = "Leaf green, kraft brown, soft cream, and sky blue accents.",
            invitationIdeas = listOf(
                "Digital-only invites",
                "Transit and cycling directions",
                "Note about reusable cup or bottle encouragement"
            ),
            signageIdeas = listOf(
                "Waste-sorting guides",
                "Water refill station markers",
                "Plant-based menu cards"
            ),
            activityIdeas = listOf(
                "Sustainability tip exchange",
                "Upcycle craft table for kids or teams",
                "Walking tour of local green spaces"
            ),
            merchandiseIdeas = listOf(
                "Reusable water bottles",
                "Canvas tote bags",
                "Seed paper notebooks or recycled pens"
            )
        ),
        EventTheme(
            id = "retro",
            name = "Retro",
            description = "Nostalgic colour blocks and playful graphics without cluttering the space.",
            decorIdeas = listOf(
                "Bold geometric table runners",
                "Vinyl-style photo props",
                "Checkered or striped accent zones"
            ),
            colorStyle = "Mustard, teal, coral, and cream in balanced blocks.",
            invitationIdeas = listOf(
                "Poster-style invite artwork",
                "Playlist teaser link",
                "Costume or era optional note"
            ),
            signageIdeas = listOf(
                "Arcade-style directional signs",
                "Ticket-stub name badges",
                "Retro type on menu boards"
            ),
            activityIdeas = listOf(
                "Photo booth with throwback props",
                "Trivia rounds by decade",
                "Dancefloor playlist blocks"
            ),
            merchandiseIdeas = listOf(
                "Printed tote bags",
                "Enamel pins or badges",
                "Colourful notebooks"
            )
        ),
        EventTheme(
            id = "tropical",
            name = "Tropical",
            description = "Fresh outdoor energy with botanical accents and bright hospitality.",
            decorIdeas = listOf(
                "Palm leaves and citrus accents",
                "Light fabrics and open seating",
                "Fresh fruit water stations"
            ),
            colorStyle = "Turquoise, lime, coral, and sandy neutrals.",
            invitationIdeas = listOf(
                "Bright botanical header",
                "Outdoor weather contingency note",
                "Casual dress suggestion"
            ),
            signageIdeas = listOf(
                "Beach-hut style station labels",
                "Cocktail or mocktail boards",
                "Shade and seating maps"
            ),
            activityIdeas = listOf(
                "Lawn games",
                "Fresh juice tasting",
                "Sunset group photo"
            ),
            merchandiseIdeas = listOf(
                "Water bottles",
                "Lightweight tote bags",
                "Sun hats or fans (seasonal)"
            )
        ),
        EventTheme(
            id = "tech_futuristic",
            name = "Tech / Futuristic",
            description = "High-contrast lighting and crisp digital cues for product and innovation events.",
            decorIdeas = listOf(
                "LED edge lighting on stage edges",
                "Modular demo pods",
                "Dark backdrop with bright focal screens"
            ),
            colorStyle = "Charcoal, electric cyan, white, and muted violet accents used lightly.",
            invitationIdeas = listOf(
                "QR check-in instructions",
                "App or demo access details",
                "Hardware bring-along checklist"
            ),
            signageIdeas = listOf(
                "Digital agenda screens",
                "Demo queue numbering",
                "Charging station markers"
            ),
            activityIdeas = listOf(
                "Hands-on demos",
                "Lightning talks",
                "Hackathon or challenge booth"
            ),
            merchandiseIdeas = listOf(
                "Cable organisers",
                "Tech-friendly tote bags",
                "Branded notebooks for notes"
            )
        ),
        EventTheme(
            id = "seasonal",
            name = "Seasonal",
            description = "Tie décor and hospitality to the current season for warmth and relevance.",
            decorIdeas = listOf(
                "Seasonal foliage or produce centrepieces",
                "Temperature-aware seating (shade or heaters)",
                "Colour cues that match the season without clutter"
            ),
            colorStyle = "Spring pastels, summer brights, autumn rusts, or winter deep greens — pick one season.",
            invitationIdeas = listOf(
                "Seasonal illustration header",
                "Indoor/outdoor guidance",
                "Weather-aware arrival notes"
            ),
            signageIdeas = listOf(
                "Seasonal menu boards",
                "Coat check or shade tent signs",
                "Photo wall with seasonal frame"
            ),
            activityIdeas = listOf(
                "Seasonal tasting flight",
                "Outdoor stroll or indoor fireside chat",
                "Craft table using seasonal materials"
            ),
            merchandiseIdeas = listOf(
                "Seasonal tote bags",
                "Travel mugs for cooler months",
                "Water bottles for warmer months"
            )
        ),
        EventTheme(
            id = "sports",
            name = "Sports",
            description = "Energetic layout for tournaments, watch parties, and team celebrations.",
            decorIdeas = listOf(
                "Team colour banners in limited zones",
                "Scoreboard-style agenda display",
                "Comfortable viewing clusters"
            ),
            colorStyle = "Primary team colours with white and charcoal for readability.",
            invitationIdeas = listOf(
                "Match or start time in bold",
                "Kit or colour-wear suggestion",
                "Parking and entry gate details"
            ),
            signageIdeas = listOf(
                "Registration as 'check-in lane'",
                "Court/pitch zone maps",
                "Hydration station markers"
            ),
            activityIdeas = listOf(
                "Warm-up games",
                "Skill challenge booths",
                "Awards podium moment"
            ),
            merchandiseIdeas = listOf(
                "Water bottles",
                "Drawstring or tote bags",
                "Lanyards and participant badges"
            )
        ),
        EventTheme(
            id = "creative",
            name = "Creative",
            description = "Studio energy with maker stations and visual variety that still stays organised.",
            decorIdeas = listOf(
                "Pegboard or gallery wall for work-in-progress",
                "Colour-blocked seating islands",
                "Material sample tables"
            ),
            colorStyle = "Bold primary accents on a light neutral base; avoid rainbow overload.",
            invitationIdeas = listOf(
                "Portfolio-style invite layout",
                "Bring-your-sketchbook note",
                "Workshop capacity limits"
            ),
            signageIdeas = listOf(
                "Station numbers for workshops",
                "Supply pickup labels",
                "Gallery route arrows"
            ),
            activityIdeas = listOf(
                "Timed making challenges",
                "Critique circles",
                "Collaborative mural"
            ),
            merchandiseIdeas = listOf(
                "Sketch notebooks",
                "Tote bags for materials",
                "Pens and marker sets"
            )
        ),
        EventTheme(
            id = "networking",
            name = "Networking",
            description = "Conversation-first layout that helps people meet without awkward bottlenecks.",
            decorIdeas = listOf(
                "High-top tables in clusters of four to six",
                "Name-badge pickup near entrance",
                "Quiet side room for deeper chats"
            ),
            colorStyle = "Warm neutrals with a confident accent for badges and wayfinding.",
            invitationIdeas = listOf(
                "Attendee interest tags",
                "Icebreaker prompt of the evening",
                "Arrival window rather than rigid start only"
            ),
            signageIdeas = listOf(
                "Interest zone signs (Hiring, Founders, Design, etc.)",
                "Refreshment station labels",
                "Pronoun/name badge examples"
            ),
            activityIdeas = listOf(
                "Timed intro rounds",
                "Topic tables",
                "Optional lightning intros"
            ),
            merchandiseIdeas = listOf(
                "Badges and lanyards",
                "Compact notebooks",
                "Tote bags for handouts"
            )
        ),
        EventTheme(
            id = "custom",
            name = "Custom",
            description = "A flexible blank framework for building your own look around a single strong idea.",
            decorIdeas = listOf(
                "Choose one hero visual and repeat it consistently",
                "Limit décor props to three types maximum",
                "Prioritise lighting and clear walkways first"
            ),
            colorStyle = "Pick two neutrals and one accent. Document them before ordering materials.",
            invitationIdeas = listOf(
                "State the theme idea in one sentence",
                "Share a mood reference image set with your team",
                "Keep RSVP fields short"
            ),
            signageIdeas = listOf(
                "Match typeface across all printed pieces",
                "Use icons only if they improve wayfinding",
                "Proof text at actual print size"
            ),
            activityIdeas = listOf(
                "One signature activity tied to the theme",
                "One optional side activity",
                "A closing moment guests will remember"
            ),
            merchandiseIdeas = listOf(
                "One practical giveaway guests will reuse",
                "One lightweight paper item (agenda or map)",
                "Optional premium item for speakers or sponsors"
            )
        )
    )

    fun byId(id: String): EventTheme? = themes.find { it.id == id }
}
