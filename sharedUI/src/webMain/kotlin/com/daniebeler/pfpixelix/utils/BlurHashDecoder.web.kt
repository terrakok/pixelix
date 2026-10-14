@file:OptIn(ExperimentalWasmJsInterop::class, ExperimentalSkikoApi::class, InternalSkikoApi::class)

package com.daniebeler.pfpixelix.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.await
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.webext.installPixelsFromArrayBuffer
import org.jetbrains.skiko.ExperimentalSkikoApi
import org.jetbrains.skiko.InternalSkikoApi
import org.jetbrains.skiko.wasm.awaitSkiko
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast

internal actual suspend fun decodeBlurHash(
    blurHash: String?,
    width: Int?,
    height: Int?,
    punch: Float
): ImageBitmap? {
    if (blurHash == null || width == null || height == null || blurHash.length < 6) return null
    require(width > 0) { "Width must be greater than zero" }
    require(height > 0) { "height must be greater than zero" }

    return try {
        val pixels = BlurHashWorker.decode(blurHash, width, height, punch) ?: return null
        awaitSkiko.await()
        val info = ImageInfo(
            ColorInfo(ColorType.RGBA_8888, ColorAlphaType.UNPREMUL, ColorSpace.sRGB),
            width,
            height
        )
        val bitmap = Bitmap()
        try {
            check(bitmap.installPixelsFromArrayBuffer(info, pixels, info.minRowBytes)) {
                "Failed to install BlurHash pixels"
            }
            bitmap.setImmutable()
            bitmap.asComposeImageBitmap()
        } catch (error: Throwable) {
            bitmap.close()
            throw error
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Logger.w(error) { "BlurHash worker failed; falling back to decoding on the main thread" }
        BlurHashDecoder.decode(blurHash, width, height, punch)
    }
}

private object BlurHashWorker {
    private var worker: Worker? = null
    private var unavailable = false
    private var nextId = 0L
    private val pending = mutableMapOf<String, CancellableContinuation<ArrayBuffer?>>()

    private fun getWorker(): Worker {
        check(!unavailable) { "BlurHash worker is unavailable" }
        worker?.let { return it }
        try {
            val url = URL.createObjectURL(workerBlob(BLUR_HASH_WORKER_JS))
            val created = try {
                Worker(url)
            } finally {
                URL.revokeObjectURL(url)
            }
            created.addEventListener("message", { event ->
                val message = (event as? MessageEvent)?.data?.unsafeCast<BlurHashWorkerMessage>()
                    ?: return@addEventListener
                val continuation = pending.remove(message.id)
                if (continuation?.isActive == true) {
                    when (message.kind) {
                        "result" -> continuation.resume(message.buffer)
                        "error" -> continuation.resumeWithException(
                            IllegalStateException("BlurHash worker: ${message.message}")
                        )
                        else -> continuation.resumeWithException(
                            IllegalStateException("Unexpected BlurHash worker response")
                        )
                    }
                }
            })
            created.addEventListener("error", { failWorker() })
            created.addEventListener("messageerror", { failWorker() })
            worker = created
            return created
        } catch (error: Exception) {
            unavailable = true
            throw error
        }
    }

    private fun failWorker() {
        unavailable = true
        worker?.terminate()
        worker = null
        val continuations = pending.values.toList()
        pending.clear()
        for (continuation in continuations) {
            if (continuation.isActive) {
                continuation.resumeWithException(IllegalStateException("BlurHash worker failed"))
            }
        }
    }

    suspend fun decode(hash: String, width: Int, height: Int, punch: Float): ArrayBuffer? {
        val currentWorker = getWorker()
        return suspendCancellableCoroutine { continuation ->
            val id = (nextId++).toString()
            pending[id] = continuation
            continuation.invokeOnCancellation {
                if (pending.remove(id) != null) {
                    // A failed/terminated worker must not turn cancellation into an exception.
                    runCatching { currentWorker.postMessage(cancelRequest(id)) }
                }
            }
            if (continuation.isActive) {
                try {
                    currentWorker.postMessage(decodeRequest(id, hash, width, height, punch))
                } catch (error: Exception) {
                    pending.remove(id)
                    continuation.resumeWithException(error)
                }
            }
        }
    }
}

private fun workerBlob(code: String): Blob = js("new Blob([code], { type: 'application/javascript' })")

private fun decodeRequest(id: String, hash: String, width: Int, height: Int, punch: Float): JsAny =
    js("({ kind: 'decode', id: id, hash: hash, width: width, height: height, punch: punch })")

private fun cancelRequest(id: String): JsAny = js("({ kind: 'cancel', id: id })")

private external interface BlurHashWorkerMessage : JsAny {
    val id: String
    val kind: String
    val buffer: ArrayBuffer?
    val message: String
}

// Match the existing Kotlin decoder's Float arithmetic and linear RGB output.
// Do not apply linear -> sRGB here: changing colour rendering is a separate concern.
private const val BLUR_HASH_WORKER_JS = """
const alphabet = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#${'$'}%*+,-.:;=?@[]^_{|}~';
const active = new Set();
const cancelled = new Set();
const f = Math.fround;

function decode83(hash, from, to) {
    let value = 0;
    for (let i = from; i < to; i++) {
        const index = alphabet.indexOf(hash[i]);
        if (index !== -1) value = value * 83 + index;
    }
    return value;
}

function srgbToLinear(channel) {
    const v = f(channel / 255);
    return v <= f(0.04045)
        ? f(v / f(12.92))
        : f(Math.pow(f(f(v + f(0.055)) / f(1.055)), f(2.4)));
}

function ac(channel, maximum) {
    const v = f((channel - 9) / 9);
    return f(f(v * v) * Math.sign(v) * maximum);
}

function byte(channel) {
    return Math.floor(f(f(Math.max(0, Math.min(1, channel)) * 255) + 0.5));
}

async function decode(hash, width, height, punch, id) {
    const size = decode83(hash, 0, 1);
    const nx = size % 9 + 1;
    const ny = Math.floor(size / 9) + 1;
    if (hash.length !== 4 + 2 * nx * ny) return null;
    const maximum = f(f((decode83(hash, 1, 2) + 1) / 166) * punch);
    const colors = new Float32Array(nx * ny * 3);
    const dc = decode83(hash, 2, 6);
    colors[0] = srgbToLinear(dc >> 16);
    colors[1] = srgbToLinear((dc >> 8) & 255);
    colors[2] = srgbToLinear(dc & 255);
    for (let k = 1; k < nx * ny; k++) {
        const value = decode83(hash, 4 + k * 2, 6 + k * 2);
        colors[k * 3] = ac(Math.floor(value / 361), maximum);
        colors[k * 3 + 1] = ac(Math.floor(value / 19) % 19, maximum);
        colors[k * 3 + 2] = ac(value % 19, maximum);
    }
    const pixels = new Uint8Array(width * height * 4);
    for (let y = 0; y < height; y++) {
        // Yield to the worker's event loop so cancellation messages can be processed.
        if (y % 8 === 0) await new Promise(resolve => setTimeout(resolve, 0));
        if (cancelled.has(id)) return null;
        for (let x = 0; x < width; x++) {
            let r = 0, g = 0, b = 0;
            for (let j = 0; j < ny; j++) {
                for (let i = 0; i < nx; i++) {
                    const basis = f(Math.cos(Math.PI * x * i / width) * Math.cos(Math.PI * y * j / height));
                    const k = (j * nx + i) * 3;
                    r = f(r + f(colors[k] * basis));
                    g = f(g + f(colors[k + 1] * basis));
                    b = f(b + f(colors[k + 2] * basis));
                }
            }
            const offset = (x + width * y) * 4;
            pixels[offset] = byte(r);
            pixels[offset + 1] = byte(g);
            pixels[offset + 2] = byte(b);
            pixels[offset + 3] = 255;
        }
    }
    return pixels.buffer;
}

self.onmessage = async event => {
    const { kind, id, hash, width, height, punch } = event.data;
    if (kind === 'cancel') {
        if (active.has(id)) cancelled.add(id);
        return;
    }
    if (kind !== 'decode') return;
    active.add(id);
    try {
        const buffer = await decode(hash, width, height, punch, id);
        if (!cancelled.has(id)) {
            self.postMessage({ kind: 'result', id, buffer }, buffer ? [buffer] : []);
        }
    } catch (error) {
        if (!cancelled.has(id)) {
            self.postMessage({ kind: 'error', id, message: error.message || String(error) });
        }
    } finally {
        active.delete(id);
        cancelled.delete(id);
    }
};
"""
