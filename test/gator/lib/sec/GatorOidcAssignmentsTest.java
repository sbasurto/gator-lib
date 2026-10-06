package gator.lib.sec;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GatorOidcAssignmentsTest {
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
