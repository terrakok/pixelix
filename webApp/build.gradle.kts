import java.security.MessageDigest

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":sharedUI"))
        }
    }
}

tasks.register<ServiceWorkerDistributionTask>("wasmJsBrowserDistributionWithServiceWorker") {
    group = "distribution"
    description = "Builds the production web distribution and adds a precaching Service Worker"

    dependsOn("wasmJsBrowserDistribution")
    sourceDirectory.set(layout.buildDirectory.dir("dist/wasmJs/productionExecutable"))
    outputDirectory.set(layout.buildDirectory.dir("dist/wasmJs/productionExecutableWithServiceWorker"))
}

@DisableCachingByDefault(because = "The task manages its own content-based Service Worker cache version")
abstract class ServiceWorkerDistributionTask : DefaultTask() {
    @get:InputDirectory
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun buildDistribution() {
        val source = sourceDirectory.get().asFile
        val output = outputDirectory.get().asFile

        output.deleteRecursively()
        check(source.copyRecursively(output)) {
            "Could not copy the web distribution to ${output.absolutePath}"
        }

        val distributionFiles = output
            .walkTopDown()
            .filter { it.isFile }
            .sortedBy { it.relativeTo(output).invariantSeparatorsPath }
            .toList()

        check(distributionFiles.isNotEmpty()) {
            "Web distribution is empty: ${output.absolutePath}"
        }

        val relativePaths = distributionFiles.map {
            it.relativeTo(output).invariantSeparatorsPath
        }
        val digest = MessageDigest.getInstance("SHA-256")
        distributionFiles.forEachIndexed { index, file ->
            digest.update(relativePaths[index].toByteArray())
            digest.update(0.toByte())
            digest.update(file.readBytes())
        }
        val cacheVersion = digest.digest()
            .joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
            .take(16)

        val precachePaths = buildList {
            if ("index.html" in relativePaths) add("./")
            addAll(relativePaths.map { "./$it" })
        }
        val precacheEntries = precachePaths.joinToString(",\n") { "    ${it.toJsString()}" }

        output.resolve("service-worker.js").writeText(
            """
            const CACHE_PREFIX = `cmp-app-precache:${'$'}{self.registration.scope}:`;
            const CACHE_NAME = CACHE_PREFIX + ${cacheVersion.toJsString()};
            const PRECACHE_PATHS = [
            $precacheEntries
            ];
            const PRECACHE_URLS = new Set(
                PRECACHE_PATHS.map(path => new URL(path, self.location.href).href)
            );

            function validateResponse(url, response) {
                if (!response || !response.ok) {
                    throw new Error(`Failed to load ${'$'}{url}: HTTP ${'$'}{response?.status ?? "missing response"}`);
                }
                return response;
            }

            async function precache() {
                const cache = await caches.open(CACHE_NAME);
                await Promise.all(Array.from(PRECACHE_URLS, async url => {
                    try {
                        const response = validateResponse(url,
                            await fetch(url, { cache: "reload" }));
                        await cache.put(url, response);
                    } catch {
                        // Leave failed resources uncached. The fetch handler will
                        // retry and cache them on the next successful request.
                    }
                }));
                await self.skipWaiting();
            }

            self.addEventListener("install", event => {
                event.waitUntil(precache());
            });

            self.addEventListener("activate", event => {
                event.waitUntil(
                    caches.keys()
                        .then(names => Promise.all(
                            names
                                .filter(name => name.startsWith(CACHE_PREFIX) && name !== CACHE_NAME)
                                .map(name => caches.delete(name))
                        ))
                        .then(() => self.clients.claim())
                );
            });

            self.addEventListener("fetch", event => {
                if (event.request.method !== "GET" || !PRECACHE_URLS.has(event.request.url)) return;
                event.respondWith((async () => {
                    const cache = await caches.open(CACHE_NAME);
                    const cached = await cache.match(event.request);
                    if (cached) {
                        try {
                            return validateResponse(event.request.url, cached);
                        } catch {
                            await cache.delete(event.request);
                        }
                    }
                    const response = validateResponse(event.request.url,
                        await fetch(event.request, { cache: "reload" }));
                    await cache.put(event.request, response.clone());
                    return response;
                })());
            });
            """.trimIndent() + "\n"
        )

        val indexFile = output.resolve("index.html")
        check(indexFile.isFile) { "index.html was not found in the web distribution" }

        val registration = """
            <script>
                if ("serviceWorker" in navigator) {
                    window.addEventListener("load", () => {
                        navigator.serviceWorker.register("./service-worker.js", { scope: "./" });
                    });
                }
            </script>
        """.trimIndent()
        val indexHtml = indexFile.readText()
        check("</html>" in indexHtml) { "Closing </html> tag was not found in index.html" }
        indexFile.writeText(indexHtml.replace("</html>", "$registration\n</html>"))
    }

    private fun String.toJsString(): String = buildString {
        append('"')
        this@toJsString.forEach { character ->
            when (character) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(character)
            }
        }
        append('"')
    }
}

