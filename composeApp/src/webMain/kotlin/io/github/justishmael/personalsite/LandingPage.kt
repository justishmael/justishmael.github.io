package io.github.justishmael.personalsite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun PersonalLandingPage() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0B1711), Color(0xFF07100B)),
                    ),
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x183EE38D), Color.Transparent),
                            center = Offset(180f, 40f),
                            radius = 1400f,
                        ),
                    ),
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isStacked = maxWidth < 1040.dp
            val isMobile = maxWidth < 760.dp
            if (isStacked) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    IdentityGlassPanel(
                        modifier = Modifier.fillMaxWidth(),
                        compact = isMobile,
                    )
                    GrowthLabStage(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = if (isMobile) 860.dp else 780.dp),
                        compact = true,
                    )
                }
            } else {
                Row(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    IdentityGlassPanel(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .widthIn(min = 360.dp, max = 520.dp),
                        compact = false,
                    )
                    GrowthLabStage(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        compact = false,
                    )
                }
            }
        }
    }
}
