package gator.lib.sec;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gator.lib.db.GappSQLStatement;
import gator.lib.db.helpers.GappDBHelper;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/** Resolves an already authenticated OIDC identity; never authenticates a token itself. */
public final class GatorOidcIdentity {
    private GatorOidcIdentity() { }

    public static String request(String issuer, String subject) {
        if (issuer == null || subject == null || subject.isBlank() || subject.length() > 1024
                || subject.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Invalid OIDC identity");
        URI uri = URI.create(issuer);
        if (!"https".equals(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null
                || uri.getQuery() != null || uri.getFragment() != null)
            throw new IllegalArgumentException("Invalid OIDC issuer");
        JsonObject input = new JsonObject();
        input.addProperty("idctrl", UUID.randomUUID().toString());
        input.addProperty("debugLevel", "0");
        input.addProperty("issuer", issuer);
        input.addProperty("subject", subject);
        return input.toString();
    }

    public static String resolve(String configuration, String issuer, String subject) {
        GappSQLStatement statement = new GappSQLStatement();
        statement.setStoreProcedure("app_fn_resolve_oidc_identity");
        statement.addParam(request(issuer, subject));
        return localUser(new GappDBHelper(configuration).executeStore(statement));
    }

    public static String localUser(String response) {
        try {
            var results = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("responses");
            if (results.size() != 1) throw new IllegalStateException();
            var result = results.get(0).getAsJsonObject();
            String code = result.get("resNum").getAsString();
            if ("42501".equals(code)) throw new SecurityException("Identity is not assigned or active");
            if (!"0".equals(code)) throw new IllegalStateException();
            var value = result.getAsJsonObject("response").get("usuario_id");
            if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()
                    || value.getAsString().isBlank()) throw new IllegalStateException();
            return value.getAsString();
        } catch (SecurityException denied) {
            throw denied;
        } catch (RuntimeException unavailable) {
            // Never include the database response in logs or exception text.
            throw new IllegalStateException("Identity resolution unavailable");
        }
    }

    public static void requireAccess(Map<String, ?> authenticatedClaims, String clientId) {
        if (clientId != null && !clientId.isBlank() && authenticatedClaims != null
                && authenticatedClaims.get("resource_access") instanceof Map<?, ?> clients
                && clients.get(clientId) instanceof Map<?, ?> client
                && client.get("roles") instanceof Collection<?> roles && roles.contains("access")) return;
        throw new SecurityException("Identity has no access to this installation");
    }
}
