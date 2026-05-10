package io.github.justishmael.personalsite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SiteColors =
    darkColorScheme(
        background = Color(0xFF101410),
        surface = Color(0xFF182018),
        primary = Color(0xFFA8D5BA),
        secondary = Color(0xFFD7C7A3),
        onBackground = Color(0xFFF3F0E8),
        onSurface = Color(0xFFF3F0E8),
        onPrimary = Color(0xFF102015),
        onSecondary = Color(0xFF2A2110),
    )

@Composable
fun App() {
    MaterialTheme(colorScheme = SiteColors) {
        PersonalLandingPage()
    }
}

@Composable
private fun PersonalLandingPage() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF101410),
                            Color(0xFF182018),
                            Color(0xFF101410),
                        ),
                    ),
                ).padding(horizontal = 28.dp, vertical = 36.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 760.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
            tonalElevation = 2.dp,
            shadowElevation = 8.dp,
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        PaddingValues(
                            horizontal = 32.dp,
                            vertical = 36.dp,
                        ),
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Hayden Johnson",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 46.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Developer • Data Analyst • Systems Thinker",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "I build calm, structured software for people, data, and systems. My work is guided by clarity, accessibility, and the belief that good tools should reduce friction rather than add more noise.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                )

                Spacer(modifier = Modifier.height(26.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SiteChip("Kotlin")
                    SiteChip("Python")
                    SiteChip("SQL")
                    SiteChip("Data Analysis")
                    SiteChip("Accessible Design")
                    SiteChip("Human-Centered Tools")
                }

                Spacer(modifier = Modifier.height(30.dp))

                ContactLinks()
            }
        }
    }
}

@Composable
private fun SiteChip(label: String) {
    AssistChip(
        onClick = {},
        label = { Text(label) },
        colors =
            AssistChipDefaults.assistChipColors(
                labelColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.52f),
            ),
        border =
            AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.42f),
            ),
    )
}

@Composable
private fun ContactLinks() {
    val uriHandler = LocalUriHandler.current

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ContactButton(
            label = "GitHub",
            onClick = { uriHandler.openUri("https://github.com/justishmael") },
        )

        ContactButton(
            label = "LinkedIn",
            onClick = { uriHandler.openUri("https://www.linkedin.com/in/hayden-johnson-674114177/") },
        )

        ContactButton(
            label = "Email Me",
            onClick = { uriHandler.openUri("mailto:hayden.h.w.johnson@gmail.com") },
        )
    }
}

@Composable
private fun ContactButton(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
