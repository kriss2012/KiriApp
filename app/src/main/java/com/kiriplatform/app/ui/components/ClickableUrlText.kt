package com.kiriplatform.app.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.kiriplatform.app.ui.theme.OrangePrimary
import java.util.regex.Pattern

@Composable
fun ClickableUrlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    linkColor: Color = OrangePrimary
) {
    val uriHandler = LocalUriHandler.current
    val layoutResult = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<androidx.compose.ui.text.TextLayoutResult?>(null) }

    val annotatedString = buildAnnotatedString {
        append(text)
        val urlPattern = Pattern.compile(
            "(?:^|[\\W])((?:https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|])",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = urlPattern.matcher(text)
        while (matcher.find()) {
            val start = matcher.start(1)
            val end = matcher.end(1)
            val url = matcher.group(1)
            addStyle(
                style = SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                ),
                start = start,
                end = end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = url,
                start = start,
                end = end
            )
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    try {
                        uriHandler.openUri(annotation.item)
                    } catch (e: Exception) {
                        // Fallback or ignore
                    }
                }
        },
        onTextLayout = { layoutResult.value = it }
    )
}
