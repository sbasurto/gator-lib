package gator.lib.sec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Authenticated server-to-server identity administration; issuer comes from trusted configuration. */
public final class GatorIdentityAdminClient {
    private static final int MAX_BYTES = 1_048_576;
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private static final Map<String, Set<String>> FIELDS = Map.of(
            "list", Set.of("action", "first", "max"),
            "get", Set.of("action", "targetSubject"),
            "create", Set.of("action", "username", "localUsuario", "accountId", "bodegaId", "name", "email", "requestId"),
            "update", Set.of("action", "targetSubject", "name", "email", "enabled"),
            "disable", Set.of("action", "targetSubject"),
            "enable", Set.of("action", "targetSubject"),
            "reset", Set.of("action", "targetSubject", "password", "temporary"),
            "status", Set.of("action", "targetSubject", "operationId"),
            "grant", Set.of("action", "targetSubject"));
    private final URI endpoint;

    public GatorIdentityAdminClient(URI trustedIssuer) {
        if (trustedIssuer == null || trustedIssuer.getHost() == null || trustedIssuer.getUserInfo() != null
                || trustedIssuer.getQuery() != null || trustedIssuer.getFragment() != null
                || !trustedIssuer.equals(trustedIssuer.normalize()) || trustedIssuer.getRawPath().contains("%")
                || !("https".equalsIgnoreCase(trustedIssuer.getScheme())
                    || "http".equalsIgnoreCase(trustedIssuer.getScheme())
                        && Set.of("127.0.0.1", "[::1]", "localhost").contains(trustedIssuer.getHost())))
            throw new IllegalArgumentException("Invalid trusted identity issuer");
        endpoint = URI.create(trustedIssuer.toString().replaceAll("/+$", "") + "/gator-identity-admin");
    }

    public JsonObject send(String accessToken, JsonObject request) throws IOException, InterruptedException {
        if (accessToken == null || !accessToken.matches("[A-Za-z0-9._~+/=-]+"))
            throw new IllegalArgumentException("Invalid identity access token");
        validate(request);
        byte[] body = request.toString().getBytes(StandardCharsets.UTF_8);
        if (body.length > MAX_BYTES) throw new IllegalArgumentException("Identity request too large");
        HttpRequest httpRequest = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(5))
                .header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
                .header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
        ByteArrayOutputStream received = new ByteArrayOutputStream();
        var future = HTTP.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArrayConsumer(chunk -> chunk.ifPresent(bytes -> {
            if (bytes.length > MAX_BYTES - received.size()) throw new IllegalStateException("Identity response too large");
            received.writeBytes(bytes);
        })));
        try {
            HttpResponse<Void> response = future.get(5, TimeUnit.SECONDS);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                int status = response.statusCode();
                throw new Failure(Set.of(400, 401, 403, 404, 409, 429).contains(status) ? status : 502,
                        "Identity administration request failed");
            }
            try (JsonReader reader = new JsonReader(new StringReader(received.toString(StandardCharsets.UTF_8)))) {
                reader.setStrictness(Strictness.STRICT);
                JsonElement parsed = JsonParser.parseReader(reader);
                if (!parsed.isJsonObject() || reader.peek() != JsonToken.END_DOCUMENT) throw new IllegalArgumentException();
                JsonObject result = parsed.getAsJsonObject();
                if (result.has("status") && (!result.get("status").isJsonPrimitive()
                        || !Set.of("pending", "complete", "failed").contains(result.get("status").getAsString())))
                    throw new IllegalArgumentException();
                return result;
            }
        } catch (TimeoutException timeout) {
            future.cancel(true);
            throw new Failure(504, "Identity administration timed out");
        } catch (InterruptedException interrupted) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new InterruptedException("Identity administration interrupted");
        } catch (ExecutionException failed) {
            future.cancel(true);
            throw new Failure(failed.getCause() instanceof java.net.http.HttpTimeoutException ? 504 : 502,
                    "Identity administration unavailable");
        } catch (Failure safe) {
            throw safe;
        } catch (IOException | RuntimeException malformed) {
            future.cancel(true);
            throw new Failure(502, "Invalid identity administration response");
        }
    }

    private static void validate(JsonObject request) {
        try {
            if (request == null || !string(request.get("action"))) throw new IllegalArgumentException();
            String action = request.get("action").getAsString();
            Set<String> fields = FIELDS.get(action);
            if (fields == null || !fields.containsAll(request.keySet())) throw new IllegalArgumentException();
            if (!Set.of("list", "create").contains(action) && !string(request.get("targetSubject")))
                throw new IllegalArgumentException();
            if (action.equals("create") && (!string(request.get("username")) || !string(request.get("localUsuario"))))
                throw new IllegalArgumentException();
            if (action.equals("reset") && !string(request.get("password"))) throw new IllegalArgumentException();
            if (action.equals("status") && !string(request.get("operationId"))) throw new IllegalArgumentException();
            for (var field : request.entrySet()) {
                String key = field.getKey(); JsonElement value = field.getValue();
                if (!value.isJsonPrimitive()) throw new IllegalArgumentException();
                if (Set.of("enabled", "temporary").contains(key)) {
                    if (!value.getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException();
                } else if (Set.of("accountId", "bodegaId", "first", "max").contains(key)) {
                    if (!value.getAsString().matches("[0-9]{1,9}")) throw new IllegalArgumentException();
                } else if (!value.getAsJsonPrimitive().isString()) throw new IllegalArgumentException();
            }
        } catch (RuntimeException invalid) {
            throw new IllegalArgumentException("Invalid identity administration request");
        }
    }

    private static boolean string(JsonElement value) {
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()
                && !value.getAsString().isBlank();
    }

    /** Sanitized transport failure: never retains a response body, token or exception cause. */
    public static final class Failure extends IOException {
        private final int statusCode;
        private Failure(int statusCode, String message) { super(message); this.statusCode = statusCode; }
        public int statusCode() { return statusCode; }
    }
}
