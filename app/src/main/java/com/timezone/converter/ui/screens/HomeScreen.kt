package com.timezone.converter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timezone.converter.ui.components.TimeZoneCard
import com.timezone.converter.viewmodel.TimeZoneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TimeZoneViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToConverter: () -> Unit
) {
    // Use derived state to avoid unnecessary recomposition
    val selectedZones by remember { derivedStateOf { viewModel.selectedZones } }
    val currentTimes by remember { derivedStateOf { viewModel.currentTimes } }
    val currentDates by remember { derivedStateOf { viewModel.currentDates } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TimeZone Converter",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    FloatingActionButton(
                        onClick = onNavigateToConverter,
                        modifier = Modifier.padding(end = 8.dp),
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Convert time")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add time zone")
            }
        }
    ) { paddingValues ->
        if (selectedZones.isEmpty()) {
            Text(
                text = "Tap + to add time zones",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = selectedZones,
                    key = { it.id }
                ) { zone ->
                    // Use remember to avoid recomposition when time updates
                    val time = remember(currentTimes[zone.id]) { currentTimes[zone.id] ?: "--" }
                    val date = remember(currentDates[zone.id]) { currentDates[zone.id] ?: "--" }
                    
                    TimeZoneCard(
                        label = zone.label,
                        time = time,
                        date = date,
                        onRemove = { viewModel.removeTimeZone(zone.id) },
                        showRemove = true
                    )
                }
            }
        }
    }
}