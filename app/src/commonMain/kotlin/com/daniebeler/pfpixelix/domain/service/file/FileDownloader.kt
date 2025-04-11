package com.daniebeler.pfpixelix.domain.service.file

import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.write
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readRawBytes
import io.ktor.utils.io.reader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class FileDownloader(httpClient: HttpClient) {
    private val client = httpClient.config { followRedirects = true }
    fun download(file: PlatformFile, url: String) {
        GlobalScope.launch(Dispatchers.IO) {
            Logger.d { "Downloading: $url -> $file" }
            val bytes = client.get(url).bodyAsBytes()
            file.write(bytes)
        }
    }
}