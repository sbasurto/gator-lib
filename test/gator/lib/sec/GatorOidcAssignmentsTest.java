package gator.lib.sec;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GatorOidcAssignmentsTest {
    @Test void acceptsExplicitBrowserDestinationsWithoutInventingOtherAssignments() {
        var destinations = GatorOidcAssignments.parse("""
                {"gator-mobile":{"gatorrhmaster":{"index":"indexRH","accessClientId":"rh-access"},
                  "gator-mail":{"index":"pg_mail","accessClientId":"mail-access"},
                  "cpsmexico":{"index":"indexCPS","accessClientId":"cps-access"}}}
                """).get("gator-mobile");
        assertEquals(3, destinations.size());
        assertEquals("mail-access", destinations.get("gator-mail").accessClientId());
        assertFalse(destinations.containsKey("gatorwmaster"));
        assertThrows(IllegalArgumentException.class, () -> GatorOidcAssignments.parse(
                "{\"gator-mobile\":{\"../gator-mail\":\"index\"}}"));
    }
    @Test void preservesLegacyAndOptInLinkedDestinationsInOneCatalog() {
        var targets = GatorOidcAssignments.parse("""
                {"gator-mobile":{"gatorwa":"indexA",
                  "gatorwb":{"index":"indexB","accessClientId":"gator-olr-hera-wms"}}}
                """).get("gator-mobile");
        assertEquals("indexA", targets.get("gatorwa").index());
        assertFalse(targets.get("gatorwa").linked());
        assertTrue(targets.get("gatorwb").linked());
        assertEquals("gator-olr-hera-wms", targets.get("gatorwb").accessClientId());
    }
    @Test void rejectsDuplicateMalformedAndPartialAssignments() {
        for (String value : new String[]{"{}", "[]", "null",
                "{\"mobile\":{\"gatorwa\":\"indexA\",\"gatorwa\":\"indexB\"}}",
                "{\"mobile\":{\"gatorwa\":{\"index\":\"a\"}}}",
                "{\"mobile\":{\"gatorwa\":{\"index\":\"../a\",\"accessClientId\":\"client\"}}}",
                "{\"mobile\":{\"gatorwa\":{\"index\":\"a\",\"accessClientId\":\"\"}}}",
                "{\"mobile\":{\"gatorwa\":12}}",
                "{\"mobile\":{\"gatorwa\":\"indexA\"}} trailing"}) {
            assertThrows(IllegalArgumentException.class, () -> GatorOidcAssignments.parse(value), value);
        }
    }
}
