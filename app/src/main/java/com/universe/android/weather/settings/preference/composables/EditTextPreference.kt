/**
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package com.universe.android.weather.settings.preference.composables

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.universe.android.weather.R
import com.universe.android.weather.common.ui.widgets.Material3CardListItem
import com.universe.android.weather.common.ui.widgets.defaultCardListItemElevation
import com.universe.android.weather.theme.compose.DayNightTheme
import com.universe.android.weather.theme.compose.rememberThemeRipple

@Composable
fun EditTextPreferenceView(
    @StringRes titleId: Int,
    summary: ((Context, String) -> String?)? = null,
    content: String,
    enabled: Boolean = true,
    regex: Regex? = null,
    regexError: String? = null,
    onValueChanged: (String) -> Unit,
) = EditTextPreferenceView(
    title = stringResource(titleId),
    summary = { context, value ->
        summary?.let { it(context, value) }
    },
    content = content,
    enabled = enabled,
    regex = regex,
    regexError = regexError,
    onValueChanged = onValueChanged,
)

@Composable
fun EditTextPreferenceView(
    title: String,
    summary: (Context, String) -> String?, // content -> summary.
    content: String,
    enabled: Boolean = true,
    regex: Regex? = null,
    regexError: String? = null,
    onValueChanged: (String) -> Unit,
) {
    val contentState = remember { mutableStateOf(content) }
    val dialogOpenState = remember { mutableStateOf(false) }

    Material3CardListItem(
        elevation = if (enabled) defaultCardListItemElevation else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (enabled) 1f else 0.5f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberThemeRipple(),
                    onClick = { dialogOpenState.value = true },
                    enabled = enabled,
                )
                .padding(dimensionResource(R.dimen.normal_margin)),
            verticalArrangement = Arrangement.Center,
        ) {
            Column {
                Text(
                    text = title,
                    color = DayNightTheme.colors.titleColor,
                    style = MaterialTheme.typography.titleMedium,
                )
                val currentSummary = summary(LocalContext.current, contentState.value)
                if (currentSummary?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.little_margin)))
                    Text(
                        text = currentSummary,
                        color = DayNightTheme.colors.bodyColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    if (dialogOpenState.value) {
        val inputState = remember { mutableStateOf(contentState.value) }
        AlertDialog(
            onDismissRequest = { dialogOpenState.value = false },
            title = {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            text = {
                OutlinedTextField(
                    value = inputState.value,
                    onValueChange = { inputState.value = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    readOnly = false,
                    enabled = true,
                    singleLine = true,
                    isError = regex != null && !inputState.value.matches(regex),
                    supportingText = {
                        if (regex != null && !inputState.value.matches(regex)) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if (!regexError.isNullOrEmpty()) {
                                    regexError
                                } else stringResource(R.string.settings_invalid_syntax),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        errorCursorColor = MaterialTheme.colorScheme.error,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.38f
                        ),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    )
                )
            },
            confirmButton = {
                TextButton(
                    enabled = regex == null || inputState.value.matches(regex),
                    onClick = {
                        contentState.value = inputState.value
                        dialogOpenState.value = false
                        onValueChanged(contentState.value)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_done),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { dialogOpenState.value = false }
                ) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        )
    }
}
