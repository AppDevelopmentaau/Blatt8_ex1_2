package com.example.assignment08.ex1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.assignment08.R
import kotlin.math.abs


@Composable
fun SpiritLevelScreen(
    uiState: SpiritLevelUiState,
    onModeToggle: () -> Unit,
    onSoundToggle: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!uiState.isAccelerometerAvailable) {
            // Show error message if accelerometer is not available
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Accelerometer not available on this device",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top bar with back button and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to main screen",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Text(
                        text = "Spirit Level",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 55.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Display the tilt values
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.x_axis, uiState.xTilt),
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 24.sp,
                            color = when {
                                abs(uiState.xTilt) < 1.0f -> Color(0xFF4CAF50) // Green if level
                                abs(uiState.xTilt) < 3.0f -> Color(0xFF8BC34A) // Light green if close
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(R.string.y_axis, uiState.yTilt),
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 24.sp,
                            color = when {
                                abs(uiState.yTilt) < 1.0f -> Color(0xFF4CAF50) // Green if level
                                abs(uiState.yTilt) < 3.0f -> Color(0xFF8BC34A) // Light green if close
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        
                        // Add a subtle description text
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = when {
                                uiState.isLevel -> "Perfect level!"
                                abs(uiState.xTilt) < 3.0f && abs(uiState.yTilt) < 3.0f -> "Almost level..."
                                else -> "Adjust device to level the surface"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.isLevel,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        // Pulsing animation for the level message
                        val infiniteTransition = rememberInfiniteTransition(label = "messagePulseTransition")
                        val scale = infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "messagePulseAnimation"
                        ).value
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scale),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50) // Green color
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.perfectly_level),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                  // Toggle between horizontal and vertical mode
                Button(
                    onClick = onModeToggle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (uiState.isHorizontalMode) 
                                stringResource(R.string.horizontal_mode)
                            else 
                                stringResource(R.string.vertical_mode)
                        )
                    }
                }

                // Toggle sound on/off
                Button(
                    onClick = onSoundToggle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (uiState.isSoundEnabled) 
                                stringResource(R.string.sound_on)
                            else 
                                stringResource(R.string.sound_off)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Show different bubble level UI based on the selected mode
                if (uiState.isHorizontalMode) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Circular bubble level in the top 60% of the space
                        Box(
                            modifier = Modifier
                                .weight(0.55f)
                                .fillMaxWidth(0.8f)
                        ) {
                            BubbleLevel(
                                xTilt = uiState.xTilt,
                                yTilt = uiState.yTilt,
                                isLevel = uiState.isLevel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Spacer that takes 10% of the space
                        Spacer(modifier = Modifier.weight(0.1f))

                        // Horizontal bubble level in the bottom 30% of the space
                        Box(
                            modifier = Modifier
                                .weight(0.25f)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            HorizontalBubbleLevel(
                                xTilt = uiState.xTilt,
                                isLevel = uiState.isLevel && abs(uiState.xTilt) < 1.0f,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    // In vertical mode, show vertical bubble level
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Vertical bubble level in the left 30% of the space
                        Box(
                            modifier = Modifier
                                .weight(0.18f)
                                .fillMaxHeight(0.7f)
                                .padding(vertical = 8.dp)
                        ) {
                            VerticalBubbleLevel(
                                yTilt = uiState.yTilt,
                                isLevel = uiState.isLevel && abs(uiState.yTilt) < 1.0f,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Spacer that takes 10% of the space
                        Spacer(modifier = Modifier.weight(0.05f))

                        // Circular bubble level in the right 60% of the space
                        Box(
                            modifier = Modifier
                                .weight(0.75f)
                                .fillMaxHeight(0.8f)
                        ) {
                            BubbleLevel(
                                xTilt = uiState.xTilt,
                                yTilt = uiState.yTilt,
                                isLevel = uiState.isLevel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
