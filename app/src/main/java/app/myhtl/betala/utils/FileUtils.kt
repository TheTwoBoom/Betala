package app.myhtl.betala.utils

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Throws(IOException::class)
fun readTextFromUri(context: Context, uri: Uri): String {
    val stringBuilder = StringBuilder()
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
        }
    }
    return stringBuilder.toString()
}

