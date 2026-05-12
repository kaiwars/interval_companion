package com.example.intervalcompanion.ui.settings.rounds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intervalcompanion.data.model.Round
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundsScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: RoundsViewModel = viewModel()
) {
    val rounds by viewModel.rounds.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }
    var newlyAddedId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rounds") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = isEditMode,
                            onClick = { isEditMode = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            label = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit mode",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                        SegmentedButton(
                            selected = !isEditMode,
                            onClick = { isEditMode = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            label = {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "Regular mode",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                    IconButton(onClick = onNavigateToHelp) {
                        Icon(
                            Icons.Default.Help,
                            contentDescription = "Help",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
        ) {
            Text(
                text = "Each round consists of one to three intervals with a duration in seconds. " +
                        "An empty field indicates this interval is omitted. " +
                        "The fourth field indicates how many times this round should be repeated " +
                        "before proceeding to the next round. " +
                        "Checked rounds are executed in order, repeatedly. " +
                        "Unchecked rounds are omitted.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(rounds, key = { _, r -> r.id }) { index, round ->
                    if (isEditMode) {
                        val focusFirst = round.id == newlyAddedId
                        EditRoundRow(
                            round = round,
                            onUpdate = viewModel::updateRound,
                            requestFocus = focusFirst,
                            onFocusConsumed = { newlyAddedId = null }
                        )
                    } else {
                        RegularRoundRow(
                            round = round,
                            isFirst = index == 0,
                            isLast = index == rounds.size - 1,
                            onUpdate = viewModel::updateRound,
                            onDelete = { viewModel.deleteRound(round.id) },
                            onMoveUp = { viewModel.moveRoundUp(round.id) },
                            onMoveDown = { viewModel.moveRoundDown(round.id) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.padding(16.dp)) {
                FloatingActionButton(
                    onClick = {
                        viewModel.addRound { newId ->
                            isEditMode = true
                            newlyAddedId = newId
                            coroutineScope.launch {
                                listState.animateScrollToItem(rounds.size)
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add round")
                }
            }
        }
    }
}

@Composable
private fun EditRoundRow(
    round: Round,
    onUpdate: (Round) -> Unit,
    requestFocus: Boolean,
    onFocusConsumed: () -> Unit
) {
    var t1 by remember(round.id) { mutableStateOf(round.interval1?.toString() ?: "") }
    var t2 by remember(round.id) { mutableStateOf(round.interval2?.toString() ?: "") }
    var t3 by remember(round.id) { mutableStateOf(round.interval3?.toString() ?: "") }
    var t4 by remember(round.id) { mutableStateOf(if (round.repeat > 1) round.repeat.toString() else "") }

    val focusRequester = remember { FocusRequester() }

    fun clamp(text: String): Int? = text.toIntOrNull()?.coerceIn(0, 999)

    fun commit() {
        onUpdate(
            round.copy(
                interval1 = clamp(t1),
                interval2 = clamp(t2),
                interval3 = clamp(t3),
                repeat = clamp(t4) ?: 1
            )
        )
    }

    fun onFieldChange(new: String): String {
        if (new.isEmpty()) return new
        val n = new.toIntOrNull() ?: return new.dropLast(1)
        return n.coerceIn(0, 999).toString()
    }

    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
            onFocusConsumed()
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val fieldMod0 = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
            val fieldMod = Modifier.weight(1f)

            listOf(
                Triple(t1, fieldMod0) { v: String -> t1 = onFieldChange(v); commit() },
                Triple(t2, fieldMod) { v: String -> t2 = onFieldChange(v); commit() },
                Triple(t3, fieldMod) { v: String -> t3 = onFieldChange(v); commit() },
                Triple(t4, fieldMod) { v: String -> t4 = onFieldChange(v); commit() }
            ).forEach { (value, mod, onChange) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    modifier = mod,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }
        }
    }
}

@Composable
private fun RegularRoundRow(
    round: Round,
    isFirst: Boolean,
    isLast: Boolean,
    onUpdate: (Round) -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val dimmed = !round.checked
    val labelStyle: TextStyle = if (dimmed) {
        LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontStyle = FontStyle.Italic
        )
    } else {
        LocalTextStyle.current.copy(color = Color.Unspecified)
    }

    val label = buildString {
        append(round.interval1?.toString() ?: "")
        append("/")
        append(round.interval2?.toString() ?: "")
        append("/")
        append(round.interval3?.toString() ?: "")
        append("-")
        append(round.repeat)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                IconButton(
                    onClick = onMoveUp,
                    enabled = !isFirst,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move up",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = !isLast,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move down",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Checkbox(
                checked = round.checked,
                onCheckedChange = { onUpdate(round.copy(checked = it)) }
            )

            Text(
                text = label,
                style = labelStyle,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
