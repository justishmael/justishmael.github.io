package io.github.justishmael.personalsite

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalUriHandler
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private val BotanicalColors =
    darkColorScheme(
        background = Color(0xFF07110C),
        surface = Color(0xFF112019),
        primary = Color(0xFF8DDEAA),
        secondary = Color(0xFFE6D8B3),
        tertiary = Color(0xFFB4BC76),
        onBackground = Color(0xFFF4F0E6),
        onSurface = Color(0xFFF4F0E6),
        onPrimary = Color(0xFF0B2116),
        onSecondary = Color(0xFF2E2514),
    )

private val PanelShape = RoundedCornerShape(30.dp)
private val SoftShape = RoundedCornerShape(20.dp)

private val GitHubUrl = "https://github.com/justishmael"
private val LinkedInUrl = "https://www.linkedin.com/in/hayden-johnson-674114177/"
private val EmailUrl = "mailto:hayden.h.w.johnson@gmail.com"

@Immutable
data class GrowthState(
    val light: Int = 3,
    val water: Int = 3,
    val structure: Int = 3,
    val seed: Int = 1,
    val streak: Int = 0,
    val actionCount: Int = 0,
    val lastAction: GrowthAction = GrowthAction.Analyze,
)

@Immutable
data class PlantSegment(
    val start: Offset,
    val end: Offset,
    val thickness: Float,
    val kind: SegmentKind,
    val level: Int,
)

@Immutable
private data class PlantViewport(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
)

enum class SegmentKind {
    Stem,
    Leaf,
    Bud,
    Root,
}

enum class GrowthAction {
    AddLight,
    AddWater,
    PruneStructure,
    Analyze,
    ResetGarden,
}

@Composable
fun App() {
    MaterialTheme(colorScheme = BotanicalColors) {
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

@Composable
private fun IdentityGlassPanel(
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

@Composable
private fun GrowthLabStage(
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
    maxWidth: androidx.compose.ui.unit.Dp,
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
private fun GrowthCanvas(
    state: GrowthState,
    idlePhase: Float,
    drift: Float,
    actionPulse: Float,
    mobileLayout: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier =
            modifier.semantics {
                contentDescription = "Decorative garden visualization showing plant growth based on light, water, and structure."
            },
    ) {
        drawGrowthCanvas(
            state = state,
            idlePhase = idlePhase,
            drift = drift,
            actionPulse = actionPulse,
            mobileLayout = mobileLayout,
        )
    }
}

private fun DrawScope.drawGrowthCanvas(
    state: GrowthState,
    idlePhase: Float,
    drift: Float,
    actionPulse: Float,
    mobileLayout: Boolean,
) {
    val viewport = plantViewport(size.width, size.height, mobileLayout)
    val plant = generatePlant(state, viewport)
    val harmony = harmonyScore(state)
    val healthyTint = blend(Color(0xFF6E946C), Color(0xFF92E3A8), harmony / 100f)
    val glowCenter = Offset(size.width * (0.56f + drift * 0.04f), size.height * (0.38f + drift * 0.015f))
    val idleWave = sin(idlePhase * PI * 2).toFloat()

    drawRoundRect(
        brush =
            Brush.verticalGradient(
                colors = listOf(Color(0x141A3126), Color.Transparent),
            ),
        cornerRadius = CornerRadius(32f, 32f),
        size = size,
    )

    drawCircle(
        brush =
            Brush.radialGradient(
                colors =
                    listOf(
                        Color(0x1847E890).copy(alpha = 0.6f + actionPulse * 0.25f),
                        Color.Transparent,
                    ),
                center = glowCenter,
                radius = size.minDimension * (0.3f + actionPulse * 0.05f),
            ),
        radius = size.minDimension * (0.26f + actionPulse * 0.05f),
        center = glowCenter,
    )

    drawGrid(viewport, idleWave)

    drawLine(
        color = Color(0xFF486757),
        start = Offset(viewport.left, viewport.bottom),
        end = Offset(viewport.right, viewport.bottom),
        strokeWidth = 3.2f,
        cap = StrokeCap.Round,
    )

    plant.filter { it.kind == SegmentKind.Root }.forEach { segment ->
        drawLine(
            color = Color(0xFF62715C).copy(alpha = 0.36f),
            start = segment.start,
            end = segment.end,
            strokeWidth = segment.thickness,
            cap = StrokeCap.Round,
        )
    }

    plant.filter { it.kind == SegmentKind.Stem }.forEach { segment ->
        val offset = segment.level * 0.8f * idleWave
        drawLine(
            color = blend(Color(0xFF50755A), healthyTint, 0.4f + segment.level * 0.05f),
            start = segment.start.copy(x = segment.start.x + offset * 0.2f),
            end = segment.end.copy(x = segment.end.x + offset),
            strokeWidth = segment.thickness + actionPulse * 1.4f,
            cap = StrokeCap.Round,
        )
    }

    plant.filter { it.kind == SegmentKind.Leaf }.forEach { segment ->
        drawLeaf(
            center = segment.end.copy(x = segment.end.x + idleWave * (1.5f + segment.level)),
            state = state,
            idleWave = idleWave,
            actionPulse = actionPulse,
        )
    }

    plant.filter { it.kind == SegmentKind.Bud }.forEach { segment ->
        val budRadius = 3.6f + harmony * 0.015f + actionPulse * 2.2f
        drawCircle(
            color = Color(0xFFE6D8B3).copy(alpha = 0.9f),
            radius = budRadius,
            center = segment.end.copy(y = segment.end.y + drift * 2f),
        )
        drawCircle(
            color = Color(0x28FFE8A8).copy(alpha = 0.45f + actionPulse * 0.2f),
            radius = budRadius * 2.4f,
            center = segment.end.copy(y = segment.end.y + drift * 2f),
        )
    }
}

private fun DrawScope.drawGrid(
    viewport: PlantViewport,
    idleWave: Float,
) {
    val lineColor = Color(0xFF243A2E)
    val safeWidth = viewport.right - viewport.left
    val safeHeight = viewport.bottom - viewport.top
    val columnWidth = safeWidth / 6f
    var currentX = viewport.left + columnWidth
    repeat(5) {
        drawLine(
            color = lineColor.copy(alpha = 0.22f),
            start = Offset(currentX, viewport.top),
            end = Offset(currentX, viewport.bottom - 10f + idleWave * 1.2f),
            strokeWidth = 1f,
        )
        currentX += columnWidth
    }

    val rowHeight = safeHeight / 4f
    var currentY = viewport.top + rowHeight * 0.6f
    repeat(4) {
        drawLine(
            color = lineColor.copy(alpha = 0.18f),
            start = Offset(viewport.left, currentY + idleWave * 1.5f),
            end = Offset(viewport.right, currentY + idleWave * 1.5f),
            strokeWidth = 1f,
        )
        currentY += rowHeight
    }
}

private fun DrawScope.drawLeaf(
    center: Offset,
    state: GrowthState,
    idleWave: Float,
    actionPulse: Float,
) {
    val harmony = harmonyScore(state)
    val leafColor = blend(Color(0xFF5F8B69), Color(0xFF95E1A9), harmony / 100f)
    val width = 10f + state.water * 2.3f + actionPulse * 1.4f
    val height = 6f + state.light * 1.5f
    val tilt = idleWave * 2.8f

    val leftLeaf =
        Path().apply {
            moveTo(center.x, center.y)
            quadraticTo(center.x - width, center.y - height + tilt, center.x - width * 0.46f, center.y - height * 1.8f)
            quadraticTo(center.x - width * 0.12f, center.y - height, center.x, center.y)
            close()
        }
    val rightLeaf =
        Path().apply {
            moveTo(center.x, center.y)
            quadraticTo(center.x + width, center.y - height - tilt, center.x + width * 0.46f, center.y - height * 1.8f)
            quadraticTo(center.x + width * 0.12f, center.y - height, center.x, center.y)
            close()
        }

    drawPath(leftLeaf, leafColor.copy(alpha = 0.84f))
    drawPath(rightLeaf, leafColor)
}

private fun generatePlant(
    state: GrowthState,
    viewport: PlantViewport,
): List<PlantSegment> {
    val segments = mutableListOf<PlantSegment>()
    val safeWidth = viewport.right - viewport.left
    val safeHeight = viewport.bottom - viewport.top
    val compactCanvas = safeHeight < 360f || safeWidth < 420f
    val soilY = viewport.bottom
    val base = Offset((viewport.left + viewport.right) * 0.5f, soilY)
    val harmony = harmonyScore(state) / 100f
    val imbalanceX = ((state.light - state.water) * 0.045f).coerceIn(-0.16f, 0.16f)
    val trunkBase = if (compactCanvas) 0.30f else 0.36f
    val trunkMax = if (compactCanvas) 0.54f else 0.62f
    val trunkLength = safeHeight * (trunkBase + state.light * 0.04f + harmony * 0.1f).coerceIn(0.30f, trunkMax)
    val rootLength = safeWidth * (0.11f + state.structure * 0.02f)
    val branchDepth = 2 + ((state.totalResources() + state.structure + state.streak) / 5)
    val branchCount = 2 + (state.water / 2) + if (harmony > 0.72f) 1 else 0

    repeat(4) { index ->
        val direction = if (index % 2 == 0) -1f else 1f
        val spread = rootLength * (0.48f + index * 0.15f)
        val end =
            Offset(
                x = base.x + spread * direction,
                y = soilY + 16f + index * 11f + ((state.seed + index * 9) % 7),
            )
        segments += PlantSegment(base, end, 3.3f - index * 0.35f, SegmentKind.Root, 0)
    }

    val trunkEnd = Offset(base.x + safeWidth * imbalanceX, soilY - trunkLength)
    segments += PlantSegment(base, trunkEnd, 10f + state.structure * 0.75f, SegmentKind.Stem, 0)

    val sway = ((state.seed % 11) - 5) * 0.012f
    repeat(branchCount) { index ->
        val t = (index + 1f) / (branchCount + 1f)
        val start = Offset(lerp(base.x, trunkEnd.x, t), lerp(base.y, trunkEnd.y, t))
        val direction = if (index % 2 == 0) -1f else 1f
        val angle = -PI / 2 + direction * (0.46 + state.structure * 0.032 + index * 0.08) + sway
        val length = safeHeight * (0.11f + state.water * 0.014f + harmony * 0.06f) * (1f - t * 0.26f)
        growBranch(
            segments = segments,
            start = start,
            angle = angle,
            length = length,
            depth = branchDepth,
            direction = direction,
            state = state,
            level = 1,
        )
    }

    if (state.totalResources() <= 6) {
        segments += PlantSegment(trunkEnd, trunkEnd.copy(y = trunkEnd.y - 8f), 3f, SegmentKind.Leaf, branchDepth)
    }

    return segments
}

private fun plantViewport(
    width: Float,
    height: Float,
    mobileLayout: Boolean = false,
): PlantViewport {
    if (mobileLayout) {
        val sidePadding = (width * 0.14f).coerceIn(28f, 64f)
        val topPadding = (height * 0.08f).coerceIn(22f, 46f)
        val bottomPadding = (height * 0.18f).coerceIn(82f, 120f)
        return PlantViewport(
            left = sidePadding,
            right = width - sidePadding,
            top = topPadding,
            bottom = height - bottomPadding,
        )
    }

    val narrow = width < 700f
    val left = width * if (narrow) 0.1f else 0.08f
    val right = width * if (narrow) 0.9f else 0.92f
    val top = height * if (narrow) 0.12f else 0.08f
    val bottom = height * if (narrow) 0.86f else 0.86f
    return PlantViewport(left = left, right = right, top = top, bottom = bottom)
}

private fun growBranch(
    segments: MutableList<PlantSegment>,
    start: Offset,
    angle: Double,
    length: Float,
    depth: Int,
    direction: Float,
    state: GrowthState,
    level: Int,
) {
    if (depth <= 0 || length < 11f) return

    val end =
        Offset(
            x = start.x + (cos(angle) * length).toFloat(),
            y = start.y + (sin(angle) * length).toFloat(),
        )

    segments += PlantSegment(start, end, max(2.4f, 7.4f - level * 0.72f), SegmentKind.Stem, level)

    val shouldBloom = harmonyScore(state) >= 74 && state.streak >= 1 && depth <= 2
    val leafPairs = 1 + (state.water / 3) + if (depth == 1) 1 else 0
    repeat(leafPairs) { pair ->
        val ratio = (pair + 1f) / (leafPairs + 1f)
        val leafCenter = Offset(lerp(start.x, end.x, ratio), lerp(start.y, end.y, ratio))
        segments += PlantSegment(leafCenter, leafCenter, 1f, SegmentKind.Leaf, level)
    }

    if (shouldBloom) {
        segments += PlantSegment(end, end, 1f, SegmentKind.Bud, level)
    }

    val branchOffset = 0.17 + (state.light * 0.015) - (state.water * 0.005)
    val nextLength = length * (0.68f + state.structure * 0.02f).coerceIn(0.62f, 0.83f)

    growBranch(
        segments = segments,
        start = end,
        angle = angle - branchOffset * direction,
        length = nextLength,
        depth = depth - 1,
        direction = direction,
        state = state,
        level = level + 1,
    )

    if (depth > 1) {
        growBranch(
            segments = segments,
            start = end,
            angle = angle + (0.4 + state.structure * 0.024) * direction,
            length = nextLength * (0.76f + state.water * 0.012f).coerceIn(0.7f, 0.9f),
            depth = depth - 2,
            direction = -direction,
            state = state,
            level = level + 1,
        )
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

private fun harmonyScore(state: GrowthState): Int {
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

private fun GrowthState.totalResources(): Int = light + water + structure

private fun resourceSpread(state: GrowthState): Int =
    max(state.light, max(state.water, state.structure)) - min(state.light, min(state.water, state.structure))

private fun blend(
    start: Color,
    end: Color,
    fraction: Float,
): Color {
    val t = fraction.coerceIn(0f, 1f)
    return Color(
        red = lerp(start.red, end.red, t),
        green = lerp(start.green, end.green, t),
        blue = lerp(start.blue, end.blue, t),
        alpha = lerp(start.alpha, end.alpha, t),
    )
}

private fun lerp(
    start: Float,
    end: Float,
    fraction: Float,
): Float = start + (end - start) * fraction
