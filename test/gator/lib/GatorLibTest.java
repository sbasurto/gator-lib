package gator.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gator.lib.db.GappSQLStatement;
import gator.lib.db.conf.GappDBConfFile;
import gator.lib.db.helpers.web.GappEndpointResponse;
import gator.lib.sec.GappBase64;
import gator.lib.sec.GappCrypt;
import gator.lib.sec.GappKeystore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GatorLibTest {
    @Test
    void encryptsAndDecryptsAes() {
        GappCrypt crypt = new GappCrypt("test");
        String key = crypt.getAESKey();
        String encrypted = crypt.crytpStringAES("mensaje");

        assertEquals("mensaje", crypt.decryptStringAES(encrypted, key, crypt.getIVStr()));
    }

    @Test
    void buildsPostgresStoredProcedureCall() {
        GappSQLStatement statement = new GappSQLStatement();
        statement.setStoreProcedure("app_fn_test");
        statement.addParam("one");
        statement.addParam("two");

        assertEquals("{ ?= call app_fn_test(?,?) }", statement.getStoreForExecution());
    }

    @Test
    void serializesEndpointResponse() {
        GappEndpointResponse response = new GappEndpointResponse();
        response.addResponseParams("0", "ok", "1");

        JsonObject json = JsonParser.parseString(response.getResponseJson()).getAsJsonObject();
        assertEquals("0", json.getAsJsonArray("responses").get(0).getAsJsonObject().get("resNum").getAsString());
    }

    @Test
    void encodesAndDecodesBase64() {
        assertEquals("Gator", GappBase64.decodeString(GappBase64.encodeString("Gator")));
    }

    @Test
    void containsNoDefaultCredentialsOrPasswordInLogs() {
        GappDBConfFile config = new GappDBConfFile();
        GappKeystore keystore = new GappKeystore();
        keystore.setStoreFile("keys.p12");
        keystore.setStorePassword("secret");
        keystore.setStoreKey("server");

        assertEquals("", config.getUser());
        assertEquals("", config.getSecret());
        assertFalse(keystore.toString().contains("secret"));
    }
}

