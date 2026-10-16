package com.wheelhouse.app.ui.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Keyboard focus indicator for every `clickable` in the app, wrapping Material's ripple.
 *
 * The ripple's own focus state is a ~10% tint of the content colour — far below the 3:1
 * WCAG 1.4.11 needs against the card. This adds a two-tone ring on top: an Ink outer band
 * and a Paper inner band. One band always contrasts with its neighbour — Ink against the
 * light backgrounds (≥15:1 on Paper/Fill/Bg/VegaBg/WarnBg), Paper against Ink-filled
 * primary buttons (17.4:1) — so no single-colour ring disappears on a dark control.
 *
 * Drawn inset, so it survives the `clip` most controls apply before `clickable`; the outer
 * band's 7dp outer radius matches the option buttons' corner.
 */
internal data class FocusRingIndication(private val delegate: Indication) : Indication {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val delegateInstance = delegate.rememberUpdatedInstance(interactionSource)
        val focused = interactionSource.collectIsFocusedAsState()
        return remember(delegateInstance, focused) { FocusRingInstance(delegateInstance, focused) }
    }
}

private class FocusRingInstance(
    private val delegate: IndicationInstance,
    private val focused: State<Boolean>,
) : IndicationInstance {
    override fun ContentDrawScope.drawIndication() {
        with(delegate) { drawIndication() }
        if (!focused.value) return
        val band = 2.dp.toPx()
        drawBand(Ink, inset = band / 2, width = band, radius = 6.dp.toPx())
        drawBand(Paper, inset = band * 1.5f, width = band, radius = 4.dp.toPx())
    }

    private fun ContentDrawScope.drawBand(
        color: Color,
        inset: Float,
        width: Float,
        radius: Float,
    ) {
        if (size.width <= inset * 2 || size.height <= inset * 2) return
        drawRoundRect(
            color = color,
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2, size.height - inset * 2),
            cornerRadius = CornerRadius(radius),
            style = Stroke(width),
        )
    }
}
