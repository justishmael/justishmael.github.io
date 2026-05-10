package io.github.justishmael.personalsite

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

internal enum class SegmentKind {
    Stem,
    Leaf,
    Bud,
    Root,
}

internal data class PlantSegment(
    val start: Offset,
    val end: Offset,
    val thickness: Float,
    val kind: SegmentKind,
    val level: Int,
)

private data class PlantViewport(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
)

@Composable
internal fun GrowthCanvas(
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
