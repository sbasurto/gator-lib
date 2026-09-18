package gator.lib.session;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GappSessionDataTest {
    @Test void preservesExistingSessionJsonAndSelectionById() {
        String json = """
                {"cuentas":{"cuentas":{"row-key":{"id":"AC-1","nombre":"Cuenta",
                  "sistema":"erm","bodegas":{"warehouse-row":{"id":"WH-1","nombre":"Bodega"}}}}},
                 "servidores":[{"id":"server-1"}],"impresoras":[],"idioma":"es",
                 "usuarioEmail":"user@example.test","usuarioTelefono":"+5215555555555",
                 "afterChoose":"dummy_frame.jsp","afterPrinter":"dummy_frame.jsp",
                 "debugLevel":"0","empresaLogo":"logo.png","sessionTimeout":60000,
                 "modulos":[],"menus":[]}
                """;
        Gson gson = new Gson();
        GappSessionData session = gson.fromJson(json, GappSessionData.class);
        GappCuentas accounts = session.getAccounts();
        GappCuenta account = accounts.findById("AC-1");
        GappBodega warehouse = account.findWarehouseById("WH-1");
        assertSame(account, accounts.getCuenta(0));
        assertEquals("row-key", accounts.getKey(0));
        assertEquals("warehouse-row", account.getBodegaKey(0));
        assertEquals("Bodega", warehouse.getBodegaNombre());
        assertNull(accounts.findById("unassigned"));
        assertNull(account.findWarehouseById("unassigned"));
        assertEquals(JsonParser.parseString(json), JsonParser.parseString(gson.toJson(session)));
    }
}
