const METHODS = "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS";

// Accept both /https://server/path?query and /<encodeURIComponent(complete URL)>.
export async function onRequest({ request, next, env }) {
    const proxyUrl = new URL(request.url);
    const rawTarget = proxyUrl.pathname.slice(1);
    if (!/^https?(?::|%3a)/i.test(rawTarget)) {
        return withHeaders(await next());
    }

    if (request.method === "OPTIONS") {
        return new Response(null, { status: 204, headers: corsHeaders(request) });
    }
    if (!METHODS.split(", ").includes(request.method)) {
        return errorResponse("Method not allowed", 405, request);
    }

    let target;
    try {
        const decoded = /^https?:\/\//i.test(rawTarget) ? rawTarget : decodeURIComponent(rawTarget);
        // Concatenation preserves duplicate query parameters and their original encoding.
        target = new URL(decoded + (proxyUrl.search ? (decoded.includes("?") ? "&" : "?") + proxyUrl.search.slice(1) : ""));
        if (!["http:", "https:"].includes(target.protocol) || target.username || target.password || target.origin === proxyUrl.origin) {
            throw new Error("Invalid target");
        }
    } catch {
        return errorResponse("Invalid target URL", 400, request);
    }

    // Optional Pages environment variable: comma-separated exact upstream hostnames.
    const allowedHosts = env?.ALLOWED_HOSTS?.split(",").map(host => host.trim().toLowerCase()).filter(Boolean);
    if (allowedHosts?.length && !allowedHosts.includes(target.hostname)) {
        return errorResponse("Target host is not allowed", 403, request);
    }

    const headers = new Headers(request.headers);
    for (const name of ["Host", "Origin", "Referer", "Cookie", "Connection", "Content-Length", "CF-Connecting-IP", "X-Forwarded-For", "X-Real-IP"]) {
        headers.delete(name);
    }

    try {
        const upstream = await fetch(target.href, {
            method: request.method,
            headers,
            body: ["GET", "HEAD"].includes(request.method) ? undefined : request.body,
            redirect: "manual",
        });
        const responseHeaders = new Headers(upstream.headers);
        responseHeaders.delete("Set-Cookie");
        const location = responseHeaders.get("Location");
        if (location) {
            const redirectTarget = new URL(location, target);
            if (["http:", "https:"].includes(redirectTarget.protocol)) {
                // To the client all proxied URLs are same-origin. Do not let it
                // accidentally forward a bearer token to another upstream origin.
                if (headers.has("Authorization") && redirectTarget.origin !== target.origin) {
                    return errorResponse("Cross-origin redirect with Authorization is not allowed", 502, request);
                }
                responseHeaders.set("Location", `${proxyUrl.origin}/${encodeURIComponent(redirectTarget.href)}`);
            }
        }
        return withHeaders(new Response(upstream.body, {
            status: upstream.status,
            statusText: upstream.statusText,
            headers: responseHeaders,
        }), request);
    } catch {
        return errorResponse("Upstream request failed", 502, request);
    }
}

function corsHeaders(request) {
    return new Headers({
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": METHODS,
        "Access-Control-Allow-Headers": request?.headers.get("Access-Control-Request-Headers") || "Authorization, Content-Type, Accept, Range",
        "Access-Control-Expose-Headers": "*",
        "Access-Control-Max-Age": "86400",
        "Vary": "Access-Control-Request-Headers",
    });
}

function withHeaders(response, request) {
    const headers = new Headers(response.headers);
    if (request) {
        headers.delete("Access-Control-Allow-Credentials");
        corsHeaders(request).forEach((value, name) => {
            if (name === "vary") headers.append(name, value);
            else headers.set(name, value);
        });
    }
    headers.set("Cross-Origin-Opener-Policy", "same-origin");
    headers.set("Cross-Origin-Embedder-Policy", "require-corp");
    headers.set("Cross-Origin-Resource-Policy", "cross-origin");
    return new Response(response.body, {
        status: response.status,
        statusText: response.statusText,
        headers,
    });
}

function errorResponse(error, status, request) {
    return withHeaders(Response.json({ error }, { status }), request);
}
