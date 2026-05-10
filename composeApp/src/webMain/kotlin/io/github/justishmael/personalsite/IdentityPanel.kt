package io.github.justishmael.personalsite

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape

@Composable
internal fun IdentityGlassPanel(
    modifier: Modifier = Modifier,
    compact: Boolean,
) {
    Surface(
        modifier = modifier,
        color = Color(0xB3152C22),
        shape = PanelShape,
        border = BorderStroke(1.dp, Color(0xFF335343)),
        shadowElevation = 14.dp,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xB81C362A), Color(0x9A13241C)),
                        ),
                    ),
        ) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0x143EE48F), Color.Transparent),
                                center = Offset(120f, 40f),
                                radius = 420f,
                            ),
                        ),
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = if (compact) 20.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (compact) 16.dp else 10.dp),
            ) {
                SpacerHeader()
                HeroBlock(compact)
                SkillChips()
                CredentialBadges()
                IntroCopy(compact)
                ContactLinks()
            }
        }
    }
}

@Composable
private fun SpacerHeader() {
    Box(modifier = Modifier.heightIn(min = 12.dp))
}

@Composable
private fun HeroBlock(compact: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Hayden Johnson",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = if (compact) 40.sp else 44.sp,
            lineHeight = if (compact) 44.sp else 48.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-1.3).sp,
        )
        Surface(
            color = Color(0xB71A382B),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color(0xFF3A624E)),
        ) {
            Text(
                text = "Autistic Developer / Data Analyst / Systems Builder",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            text = "Building calm, useful systems for people, data, and play.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.94f),
            fontSize = if (compact) 23.sp else 22.sp,
            lineHeight = if (compact) 29.sp else 27.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun IntroCopy(compact: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("About")
        IntroParagraph(
            "I'm Hayden, an autistic developer and data analyst with a B.S. in Computer Science from WGU. I build software that makes complicated things easier to understand, use, and maintain.",
            compact,
        )
        IntroParagraph(
            "My work sits at the intersection of Kotlin development, data analysis, AI-assisted tooling, accessible design, and systems thinking. I'm especially drawn to tools that reduce cognitive load, improve workflows, and help people move through complex processes with more clarity.",
            compact,
        )
        IntroParagraph(
            "I'm also interested in indie game design, voxel systems, and sandbox spaces like Minecraft: playful technical environments where people can create, collaborate, and build joy.",
            compact,
        )
    }
}

@Composable
private fun IntroParagraph(
    text: String,
    compact: Boolean,
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
        fontSize = if (compact) 16.sp else 14.sp,
        lineHeight = if (compact) 26.sp else 22.sp,
    )
}

@Composable
private fun SkillChips() {
    val skills =
        listOf(
            "Kotlin",
            "Python",
            "SQL",
            "Java",
            "Compose",
            "Data Analysis",
            "AI Tooling",
            "QA Thinking",
            "SOPs",
            "Accessibility",
            "Voxel Systems",
            "Minecraft Servers",
        )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("Skills")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            skills.forEach { skill ->
                Surface(
                    color = Color(0xC113251D),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFF355443)),
                ) {
                    Text(
                        text = skill,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun CredentialBadges() {
    val badges =
        listOf(
            "B.S. Computer Science, WGU, Dec 2023",
            "CompTIA Project+",
            "Data Analysis + QA",
            "Kotlin / Python / SQL",
        )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("Degrees + Focus")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            badges.forEach { badge ->
                Surface(
                    color = Color(0xA514281E),
                    shape = SoftShape,
                    border = BorderStroke(1.dp, Color(0xFF355443)),
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactLinks() {
    val uriHandler = LocalUriHandler.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("Contact")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LinkButton("GitHub", "Open Hayden's GitHub profile", true) { uriHandler.openUri(GitHubUrl) }
            LinkButton("LinkedIn", "Open Hayden's LinkedIn profile", false) { uriHandler.openUri(LinkedInUrl) }
            LinkButton("Email", "Email Hayden", false) { uriHandler.openUri(EmailUrl) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.1.sp,
    )
}

@Composable
private fun LinkButton(
    label: String,
    description: String,
    emphasized: Boolean,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val container = if (emphasized) MaterialTheme.colorScheme.primary else Color(0xC4182F25)
    val content = if (emphasized) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val outline =
        when {
            focused -> MaterialTheme.colorScheme.secondary
            emphasized -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            else -> Color(0xFF385846)
        }

    Button(
        onClick = onClick,
        modifier =
            Modifier
                .widthIn(min = 92.dp)
                .heightIn(min = 48.dp)
                .onFocusChanged { focused = it.isFocused }
                .semantics {
                    contentDescription = description
                    role = Role.Button
                }
                .border(BorderStroke(2.dp, outline), SoftShape),
        shape = SoftShape,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp,
            maxLines = 1,
        )
    }
}
