package gator.lib.sec;

import com.google.gson.JsonParser;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GatorOidcIdentityTest {
    @Test void bindsExactIssuerAndSubjectWithoutUsernameOrEmail() {
        var request = JsonParser.parseString(GatorOidcIdentity.request(
                "https://identity.example/realms/gator", "Subject-A")).getAsJsonObject();
        assertEquals("Subject-A", request.get("subject").getAsString());
        assertEquals("https://identity.example/realms/gator", request.get("issuer").getAsString());
        assertFalse(request.has("email"));
        assertThrows(IllegalArgumentException.class, () -> GatorOidcIdentity.request("http://identity.example", "x"));
        assertThrows(IllegalArgumentException.class, () -> GatorOidcIdentity.request("https://identity.example", " "));
    }
    @Test void preservesExactLocalIdentifierAndRejectsDenialWithoutFallback() {
        assertEquals("User-A", GatorOidcIdentity.localUser("""
                {"responses":[{"resNum":"0","response":{"usuario_id":"User-A"}}]}
                """));
        assertThrows(SecurityException.class, () -> GatorOidcIdentity.localUser("""
                {"responses":[{"resNum":"42501","response":{"usuario_id":"admin"}}]}
                """));
        for (String bad : List.of("false", "{}", "{\"responses\":[]}",
                "{\"responses\":[{\"resNum\":\"0\",\"response\":{}}]}",
                "{\"responses\":[{\"resNum\":\"08006\"}]}")) {
            assertThrows(IllegalStateException.class, () -> GatorOidcIdentity.localUser(bad));
        }
    }
    @Test void onlyAcceptsAccessRoleForTheRequestedInstallation() {
        Map<String, Object> claims = Map.of("resource_access", Map.of(
                "gator-olr-hera-erm", Map.of("roles", List.of("access")),
                "gator-olr-apolo-erm", Map.of("roles", List.of("read"))));
        assertDoesNotThrow(() -> GatorOidcIdentity.requireAccess(claims, "gator-olr-hera-erm"));
        assertThrows(SecurityException.class, () -> GatorOidcIdentity.requireAccess(claims, "gator-olr-apolo-erm"));
        assertThrows(SecurityException.class, () -> GatorOidcIdentity.requireAccess(Map.of(), "gator-olr-hera-erm"));
        assertThrows(SecurityException.class, () -> GatorOidcIdentity.requireAccess(
                Map.of("resource_access", Map.of("gator-olr-hera-erm", Map.of("roles", "access"))), "gator-olr-hera-erm"));
    }
}
