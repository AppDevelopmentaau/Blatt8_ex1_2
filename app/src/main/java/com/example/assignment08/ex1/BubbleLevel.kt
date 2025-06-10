package com.example.assignment08.ex1

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt


//runde Wasserwaage
@Composable
fun BubbleLevel(
    xTilt: Float,
    yTilt: Float,
    isLevel: Boolean,
    modifier: Modifier = Modifier
) {
    // Scale tilt values to control bubble sensitivity
    val scaleFactor = 10f
    
    // Clamp the values to avoid bubble going outside the container
    val scaledXTilt = (xTilt / scaleFactor).coerceIn(-1f, 1f)
    val scaledYTilt = (yTilt / scaleFactor).coerceIn(-1f, 1f)
    
    // Animate bubble position
    val animatedX by animateFloatAsState(
        targetValue = scaledXTilt,
        animationSpec = tween(200),
        label = "bubbleX"
    )
    
    val animatedY by animateFloatAsState(
        targetValue = scaledYTilt,
        animationSpec = tween(200),
        label = "bubbleY"
    )
    
    // Pulsing animation for the bubble when level
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLevel) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnimation"
    )
    
    // Subtle color transition when approaching level
    val tiltMagnitude = sqrt(xTilt * xTilt + yTilt * yTilt)
    val isNearlyLevel = tiltMagnitude < 3.0f // Slightly more relaxed than isLevel
    
    // Bubble level container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 2.dp,
                color = if (isLevel) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Draw the level markings
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2
            
            // Draw center circle
            drawCircle(
                color = if (isLevel) Color(0xFF4CAF50).copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.5f),
                center = center,
                radius = radius * 0.2f,
                style = Stroke(width = 2f)
            )
            
            // Draw middle circle
            drawCircle(
                color = if (isNearlyLevel) Color(0xFF8BC34A).copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.7f),
                center = center,
                radius = radius * 0.7f,
                style = Stroke(width = 3f)
            )
            
            // Draw X-axis line
            drawLine(
                color = Color.Gray.copy(alpha = 1f),
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
            
            // Draw Y-axis line
            drawLine(
                color = Color.Gray.copy(alpha = 1f),
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 3f,
                cap = StrokeCap.Round            
            )
            
            // Draw precision grid (small tick marks)
            for (i in -4..4 step 1) {
                // Horizontal ticks
                drawLine(
                    color = Color.Gray.copy(alpha = 0.6f),
                    start = Offset(center.x + (radius * 0.4f * i), center.y - radius * 0.1f),
                    end = Offset(center.x + (radius * 0.4f * i), center.y + radius * 0.1f),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
                
                // Vertical ticks
                drawLine(
                    color = Color.Gray.copy(alpha = 0.6f),
                    start = Offset(center.x - radius * 0.1f, center.y + (radius * 0.4f * i)),
                    end = Offset(center.x + radius * 0.1f, center.y + (radius * 0.4f * i)),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
        }
        
        // Bubble
        Box(
            modifier = Modifier
                .size(50.dp * (if (isLevel) pulseScale else 1f))
                .offset(
                    x = (animatedX * 100).dp,
                    y = (animatedY * 100).dp
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isLevel) {
                            listOf(
                                Color(0xFF4CAF50),  // Green center
                                Color(0xFF388E3C)   // Darker green edge
                            )
                        } else if (isNearlyLevel) {
                            listOf(
                                Color(0xFF8BC34A),  // Light green center
                                Color(0xFF689F38)   // Darker light green edge
                            )
                        } else {
                            listOf(
                                Color(0xFF64B5F6),  // Blue center
                                Color(0xFF1976D2)   // Darker blue edge
                            )
                        }
                    )
                )
        )
    }
}

//horizontale Wasserwaage
@Composable
fun HorizontalBubbleLevel(
    xTilt: Float,
    isLevel: Boolean,
    modifier: Modifier = Modifier
) {
    // Scale tilt values to control bubble sensitivity
    val scaleFactor = 10f
    val scaledXTilt = (xTilt / scaleFactor).coerceIn(-1f, 1f)
    
    // Animate bubble position for smooth transitions
    val animatedX by animateFloatAsState(
        targetValue = scaledXTilt,
        animationSpec = tween(200),
        label = "horizontalBubbleX"
    )
    
    // Pulsing animation for the bubble when level
    val infiniteTransition = rememberInfiniteTransition(label = "horizontalPulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLevel) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "horizontalPulseAnimation"
    )

    val isNearlyLevel = abs(xTilt) < 3.0f


    // Horizontal bubble level container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 2.dp,
                color = if (isLevel) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(50)
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {

          
        // Bubble
        Box(
            modifier = Modifier
                .size(40.dp * (if (isLevel) pulseScale else 1f), 30.dp * (if (isLevel) pulseScale else 1f))
                .offset(
                    x = (animatedX * (LocalDensity.current.density * 100)).dp,
                    y = 0.dp
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isLevel) {
                            listOf(
                                Color(0xFF4CAF50),  // Green center
                                Color(0xFF388E3C)   // Darker green edge
                            )
                        } else if (isNearlyLevel) {
                            listOf(
                                Color(0xFF8BC34A),  // Light green center
                                Color(0xFF689F38)   // Darker light green edge
                            )
                        } else {
                            listOf(
                                Color(0xFF64B5F6),  // Blue center
                                Color(0xFF1976D2)   // Darker blue edge
                            )
                        }
                    )
                )
        )
    }
}

//vertikale Wasserwaage
@Composable
fun VerticalBubbleLevel(
    yTilt: Float,
    isLevel: Boolean,
    modifier: Modifier = Modifier
) {
    // Scale tilt values to control bubble sensitivity
    val scaleFactor = 10f
    val scaledYTilt = (yTilt / scaleFactor).coerceIn(-1f, 1f)
    
    // Animate bubble position for smooth transitions
    val animatedY by animateFloatAsState(
        targetValue = scaledYTilt,
        animationSpec = tween(200),
        label = "verticalBubbleY"
    )
    
    // Pulsing animation for the bubble when level
    val infiniteTransition = rememberInfiniteTransition(label = "verticalPulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLevel) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "verticalPulseAnimation"
    )
    
    // Subtle glow animation for the tube when nearly level
    val isNearlyLevel = abs(yTilt) < 3.0f
    val glowOpacity by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = if (isNearlyLevel) 0.3f else 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "verticalTubeGlowAnimation"
    )
    
    // Vertical bubble level container
    Box(
        modifier = modifier
            .fillMaxSize(0.2f)
            .aspectRatio(0.2f)
            .padding(16.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 2.dp,
                color = if (isLevel) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(50)
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        
        // Bubble
        Box(
            modifier = Modifier
                .size(30.dp * (if (isLevel) pulseScale else 1f), 40.dp * (if (isLevel) pulseScale else 1f))
                .offset(
                    x = 0.dp,
                    y = (animatedY * (LocalDensity.current.density * 100)).dp
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isLevel) {
                            listOf(
                                Color(0xFF4CAF50),  // Green center
                                Color(0xFF388E3C)   // Darker green edge
                            )
                        } else if (isNearlyLevel) {
                            listOf(
                                Color(0xFF8BC34A),  // Light green center
                                Color(0xFF689F38)   // Darker light green edge
                            )
                        } else {
                            listOf(
                                Color(0xFF64B5F6),  // Blue center
                                Color(0xFF1976D2)   // Darker blue edge
                            )
                        }
                    )
                )
        )
    }
}
