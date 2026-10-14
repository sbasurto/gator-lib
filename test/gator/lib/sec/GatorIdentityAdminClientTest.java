package gator.lib.sec;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GatorIdentityAdminClientTest {
    private static JsonObject request(String action) {
        JsonObject request = new JsonObject(); request.addProperty("action", action); return request;
    }
    private static URI issuer(HttpServer server) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/realms/test");
    }
    private static HttpServer server() throws IOException {
        return HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    }

    @Test void sendsOnlyToTrustedIssuerWithBearerAndJson() throws Exception {
        HttpServer server = server();
        server.createContext("/realms/test/gator-identity-admin", exchange -> {
            assertEquals("POST", exchange.getRequestMethod());
            assertEquals("Bearer fixture-token", exchange.getRequestHeaders().getFirst("Authorization"));
            assertEquals("application/json", exchange.getRequestHeaders().getFirst("Content-Type"));
            assertEquals("{\"action\":\"list\"}", new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] body = "{\"users\":[],\"status\":\"complete\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length); exchange.getResponseBody().write(body); exchange.close();
        });
        server.start();
        try { assertTrue(new GatorIdentityAdminClient(issuer(server)).send("fixture-token", request("list")).getAsJsonArray("users").isEmpty()); }
        finally { server.stop(0); }
    }

    @Test void sendsGrantOnlyAsAnExplicitSubjectAction() throws Exception {
        HttpServer server = server();
        server.createContext("/realms/test/gator-identity-admin", exchange -> {
            assertEquals("{\"action\":\"grant\",\"targetSubject\":\"exact-subject\"}",
                    new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] response = "{\"status\":\"complete\",\"granted\":true}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length); exchange.getResponseBody().write(response); exchange.close();
        });
        server.start();
        try {
            JsonObject body = request("grant"); body.addProperty("targetSubject", "exact-subject");
            assertTrue(new GatorIdentityAdminClient(issuer(server)).send("fixture-token", body).get("granted").getAsBoolean());
        } finally { server.stop(0); }
    }

    @Test void preservesExplicitCreateRetryId() throws Exception {
        HttpServer server=server();
        String requestId="11111111-2222-3333-4444-555555555555";
        server.createContext("/realms/test/gator-identity-admin",exchange->{
            var body=com.google.gson.JsonParser.parseString(new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8)).getAsJsonObject();
            assertEquals(requestId,body.get("requestId").getAsString());
            byte[] response="{\"status\":\"pending\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200,response.length);exchange.getResponseBody().write(response);exchange.close();
        });
        server.start();
        try {
            JsonObject body=request("create");body.addProperty("requestId",requestId);body.addProperty("username","fixture");
            body.addProperty("localUsuario","fixture");body.addProperty("name","Fixture");body.addProperty("email","fixture@example.invalid");
            assertEquals("pending",new GatorIdentityAdminClient(issuer(server)).send("fixture-token",body).get("status").getAsString());
        } finally {server.stop(0);}
    }

    @Test void rejectsUntrustedUrlAndRequestFieldsBeforeSending() {
        for (String value : new String[] {"http://identity.example/realms/test", "file:///tmp/issuer", "https://user@identity.example/realms/test",
                "https://identity.example/realms/test?host=evil", "https://identity.example/realms/test#fragment", "https://identity.example/realms/../test"})
            assertThrows(IllegalArgumentException.class, () -> new GatorIdentityAdminClient(URI.create(value)));
        GatorIdentityAdminClient client = new GatorIdentityAdminClient(URI.create("https://identity.example/realms/test"));
        for (String field : new String[] {"actor", "db", "issuer", "host", "password", "roles"}) {
            JsonObject body = request("update"); body.addProperty("targetSubject", "exact-subject"); body.addProperty(field, "Do not echo fixture secret");
            Exception error = assertThrows(IllegalArgumentException.class, () -> client.send("fixture-token", body));
            assertFalse(error.toString().contains("fixture secret"));
        }
        assertThrows(IllegalArgumentException.class, () -> client.send("bad\r\ntoken", request("list")));
    }

    @Test void doesNotExposeForbiddenBodyOrFollowRedirects() throws Exception {
        AtomicInteger redirects = new AtomicInteger();
        for (int status : new int[] {403, 302}) {
            HttpServer server = server();
            server.createContext("/realms/test/gator-identity-admin", exchange -> {
                exchange.getResponseHeaders().set("Location", "/stolen");
                byte[] body = "Password=fixture-secret-token".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(status, body.length); exchange.getResponseBody().write(body); exchange.close();
            });
            server.createContext("/stolen", exchange -> { redirects.incrementAndGet(); exchange.sendResponseHeaders(200, -1); exchange.close(); });
            server.start();
            try {
                GatorIdentityAdminClient.Failure error = assertThrows(GatorIdentityAdminClient.Failure.class,
                        () -> new GatorIdentityAdminClient(issuer(server)).send("fixture-token", request("list")));
                assertEquals(status == 403 ? 403 : 502, error.statusCode());
                assertFalse(error.toString().contains("fixture-secret")); assertNull(error.getCause()); assertEquals(0, redirects.get());
            } finally { server.stop(0); }
        }
    }

    @Test void rejectsMalformedAndOversizedResponsesWithoutLeakingContents() throws Exception {
        for (byte[] response : new byte[][] {"{\"secret\":fixture-secret}".getBytes(StandardCharsets.UTF_8), new byte[1_048_577]}) {
            HttpServer server = server();
            server.createContext("/realms/test/gator-identity-admin", exchange -> {
                exchange.sendResponseHeaders(200, 0);
                try { exchange.getResponseBody().write(response); } catch (IOException ignored) { }
                exchange.close();
            });
            server.start();
            try {
                GatorIdentityAdminClient.Failure error = assertThrows(GatorIdentityAdminClient.Failure.class,
                        () -> new GatorIdentityAdminClient(issuer(server)).send("fixture-token", request("list")));
                assertEquals(502, error.statusCode()); assertFalse(error.toString().contains("fixture-secret")); assertNull(error.getCause());
            } finally { server.stop(0); }
        }
    }

    @Test void boundsTheWholeResponseEvenAfterHeadersHaveArrived() throws Exception {
        HttpServer server = server(); var executor = Executors.newCachedThreadPool(); server.setExecutor(executor);
        server.createContext("/realms/test/gator-identity-admin", exchange -> {
            exchange.sendResponseHeaders(200, 0); exchange.getResponseBody().write('{'); exchange.getResponseBody().flush();
            try { Thread.sleep(7_000); } catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); }
            exchange.close();
        });
        server.start(); long started = System.nanoTime();
        try {
            GatorIdentityAdminClient.Failure error = assertThrows(GatorIdentityAdminClient.Failure.class,
                    () -> new GatorIdentityAdminClient(issuer(server)).send("fixture-token", request("list")));
            assertEquals(504, error.statusCode()); assertTrue((System.nanoTime() - started) / 1_000_000 < 6_500);
        } finally { server.stop(0); executor.shutdownNow(); }
    }
}
