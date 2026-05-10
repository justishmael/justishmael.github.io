package io.github.justishmael.personalsite

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Immutable
internal data class GrowthState(
    val light: Int = 3,
    val water: Int = 3,
    val structure: Int = 3,
    val seed: Int = 1,
    val streak: Int = 0,
    val actionCount: Int = 0,
    val lastAction: GrowthAction = GrowthAction.Analyze,
)

internal enum class GrowthAction {
    AddLight,
    AddWater,
    PruneStructure,
    Analyze,
    ResetGarden,
}

@Composable
internal fun GrowthLabStage(
    modifier: Modifier = Modifier,
    compact: Boolean,
) {
    var growthState by remember { mutableStateOf(GrowthState()) }
    val harmony = harmonyScore(growthState)
    val readout = statusReadout(growthState)
    val infiniteTransition = rememberInfiniteTransition(label = "growth-lab")
    val idlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(6200, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "idle-phase",
    )
    val drift by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(3600, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "drift",
    )
    val pulse = remember { Animatable(0f) }

    LaunchedEffect(growthState.actionCount) {
        pulse.snapTo(1f)
        pulse.animateTo(0f, animationSpec = tween(900))
    }

    BoxWithConstraints(
        modifier =
            modifier
                .clip(PanelShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x40172F24), Color.Transparent, Color(0x24121F18)),
                    ),
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x123CE68B), Color.Transparent),
                            center = Offset(780f, 160f),
                            radius = 980f,
                        ),
                    ),
        )

        val onAction: (GrowthAction) -> Unit = { action ->
            growthState =
                when (action) {
                    GrowthAction.AddLight -> growthState.adjust(light = 1, water = 0, structure = -1, action = action)
                    GrowthAction.AddWater -> growthState.adjust(light = -1, water = 1, structure = 0, action = action)
                    GrowthAction.PruneStructure -> growthState.adjust(light = 0, water = -1, structure = 1, action = action)
                    GrowthAction.Analyze -> growthState.copy(lastAction = action, actionCount = growthState.actionCount + 1)
                    GrowthAction.ResetGarden -> GrowthState(seed = growthState.seed + 17, lastAction = action)
                }
        }

        if (compact) {
            MobileGrowthLabStage(
                maxWidth = maxWidth,
                growthState = growthState,
                harmony = harmony,
                readout = readout,
                idlePhase = idlePhase,
                drift = drift,
                actionPulse = pulse.value,
                onAction = onAction,
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    GrowthLabHeader()
                    GrowthSummary(
                        state = growthState,
                        harmony = harmony,
                        readout = readout,
                        alignEnd = true,
                    )
                }

                GrowthCanvas(
                    state = growthState,
                    idlePhase = idlePhase,
                    drift = drift,
                    actionPulse = pulse.value,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 12.dp, bottom = 16.dp),
                )

                GrowthControls(
                    compact = compact,
                    state = growthState,
                    maxItemsInEachRow = 5,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun MobileGrowthLabStage(
    maxWidth: Dp,
    growthState: GrowthState,
    harmony: Int,
    readout: String,
    idlePhase: Float,
    drift: Float,
    actionPulse: Float,
    onAction: (GrowthAction) -> Unit,
) {
    val mobileCanvasHeight =
        when {
            maxWidth < 420.dp -> 440.dp
            maxWidth < 600.dp -> 500.dp
            else -> 540.dp
        }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GrowthLabHeader()
        GrowthSummary(
            state = growthState,
            harmony = harmony,
            readout = readout,
            alignEnd = false,
        )
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(mobileCanvasHeight),
            color = Color(0x6612251C),
            shape = SoftShape,
            border = BorderStroke(1.dp, Color(0xFF2E4B3A)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
            ) {
                GrowthCanvas(
                    state = growthState,
                    idlePhase = idlePhase,
                    drift = drift,
                    actionPulse = actionPulse,
                    mobileLayout = true,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        GrowthControls(
            compact = true,
            state = growthState,
            maxItemsInEachRow = 2,
            onAction = onAction,
        )
    }
}

@Composable
private fun GrowthLabHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Growth Lab",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        )
        Text(
            text = "Balance light, water, and structure.",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun GrowthSummary(
    state: GrowthState,
    harmony: Int,
    readout: String,
    alignEnd: Boolean,
) {
    val summary = "Harmony $harmony percent. Light ${state.light}, water ${state.water}, structure ${state.structure}. $readout"

    Surface(
        modifier =
            Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "Growth Lab system readout"
                stateDescription = summary
                testTag = "growthReadout"
            },
        color = Color(0x8C13241C),
        shape = SoftShape,
        border = BorderStroke(1.dp, Color(0xFF2E4C3B)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "$harmony%",
                color = if (harmony >= 72) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ResourcePill("Light", state.light, MaterialTheme.colorScheme.primary)
                ResourcePill("Water", state.water, Color(0xFFB0E1D0))
                ResourcePill("Structure", state.structure, MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
private fun ResourcePill(
    label: String,
    value: Int,
    color: Color,
) {
    Surface(
        color = Color(0x9012241C),
        shape = SoftShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.28f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = value.toString(),
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun GrowthControls(
    compact: Boolean,
    state: GrowthState,
    maxItemsInEachRow: Int,
    onAction: (GrowthAction) -> Unit,
) {
    Surface(
        color = Color(0x9012251C),
        shape = PanelShape,
        border = BorderStroke(1.dp, Color(0xFF2E4B3A)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tend the garden",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Last action: ${actionLabel(state.lastAction)}",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = maxItemsInEachRow,
            ) {
                GrowthActionButton("Add Light", "Increase light in the Growth Lab", false) { onAction(GrowthAction.AddLight) }
                GrowthActionButton("Add Water", "Increase water in the Growth Lab", false) { onAction(GrowthAction.AddWater) }
                GrowthActionButton("Prune / Structure", "Improve plant structure in the Growth Lab", false) { onAction(GrowthAction.PruneStructure) }
                GrowthActionButton("Analyze", "Analyze the current growth pattern", true) { onAction(GrowthAction.Analyze) }
                GrowthActionButton("Reset Garden", "Reset the Growth Lab garden", true) { onAction(GrowthAction.ResetGarden) }
            }
        }
    }
}

@Composable
private fun GrowthActionButton(
    label: String,
    description: String,
    subtle: Boolean,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val borderColor =
        when {
            focused -> MaterialTheme.colorScheme.secondary
            subtle -> Color(0xFF436552)
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        }
    val containerColor = if (subtle) Color(0xD013241C) else Color(0xD0183024)
    val textColor = if (subtle) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary

    Button(
        onClick = onClick,
        modifier =
            Modifier
                .heightIn(min = 46.dp)
                .onFocusChanged { focused = it.isFocused }
                .semantics {
                    contentDescription = description
                    role = Role.Button
                }
                .border(BorderStroke(2.dp, borderColor), SoftShape),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = textColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        shape = SoftShape,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

private fun GrowthState.adjust(
    light: Int,
    water: Int,
    structure: Int,
    action: GrowthAction,
): GrowthState {
    val next =
        copy(
            light = clampResource(this.light + light),
            water = clampResource(this.water + water),
            structure = clampResource(this.structure + structure),
            actionCount = actionCount + 1,
            lastAction = action,
        )
    val spread = resourceSpread(next)
    return next.copy(streak = if (spread <= 1) (streak + 1).coerceAtMost(5) else 0)
}

private fun clampResource(value: Int): Int = value.coerceIn(0, 6)

internal fun harmonyScore(state: GrowthState): Int {
    val average = (state.light + state.water + state.structure) / 3f
    val spread =
        (state.light - average).absoluteValue +
            (state.water - average).absoluteValue +
            (state.structure - average).absoluteValue
    val balancePenalty = (spread / 12f).coerceIn(0f, 1f)
    val growthBonus = (state.totalResources() / 18f) * 0.14f
    val streakBonus = state.streak * 0.03f
    val score = (0.88f - balancePenalty + growthBonus + streakBonus).coerceIn(0.1f, 0.99f)
    return (score * 100).toInt()
}

private fun statusReadout(state: GrowthState): String {
    val harmony = harmonyScore(state)

    return when {
        harmony >= 84 -> "Balanced branching. Structure is holding. Clear feedback supports bloom."
        state.structure <= 1 -> "Growth is loose. Add structure to stabilize the canopy."
        state.light <= 1 -> "The plant is leaning. More light would improve orientation."
        state.water <= 1 -> "The system looks dry. A little more water would help sustain growth."
        state.water > state.light + 1 -> "Soft expansion. The canopy is healthy, but it wants more light."
        state.light > state.water + 1 -> "Upward reach is strong. A little more water would calm the edges."
        else -> "Mixed signals. Small balanced adjustments will improve harmony."
    }
}

private fun actionLabel(action: GrowthAction): String =
    when (action) {
        GrowthAction.AddLight -> "Added light"
        GrowthAction.AddWater -> "Added water"
        GrowthAction.PruneStructure -> "Pruned for structure"
        GrowthAction.Analyze -> "Analyzed the garden"
        GrowthAction.ResetGarden -> "Reset the garden"
    }

internal fun GrowthState.totalResources(): Int = light + water + structure

private fun resourceSpread(state: GrowthState): Int =
    max(state.light, max(state.water, state.structure)) - min(state.light, min(state.water, state.structure))
