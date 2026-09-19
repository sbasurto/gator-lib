package gator.lib.sec;

import java.util.Base64;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LegacyCryptoTest {
    @Test void matchesHashesRecordedFromShiro3() {
        assertEquals("491e4d73982f537e782fba613b38b28eb5e2adeaa56ba3749920c8a28393959b1ca8b9ea93a2982e95826d936bcdb4efd4b3951a86e81d8fe9e28f5900af42b7",
                GappAuth.sha512("GatorTest21", "test-salt", 3));
        assertEquals("cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
                GappAuth.sha512("", "", 1));
        assertEquals("4603e449ae77d3372d85bef032e9408befe34697fb90b67c0e0c51f68a9b38d94d723f7649db3e32b61ab69745393227897b9508c3d28dee86993948e7093b55",
                GappAuth.sha512("contraseña🔑", "sal-ñ", 3025));
        assertEquals("2908d2c28dfc047741fc590a026ffade237ab2ba7e1266f010fe49bde548b5987a534a86655a0d17f336588e540cd66f67234b152bbb645b4bb85758a1325d64",
                GappAuth.sha512("password", "salt", 0));
        assertEquals("51469b44660bed0545a311be281c9b2b3f7886e2c1769ae114f923ce0a739afaa8ebc58dae8eec894fe50415b0e9d6f84e2461be73d5c24f2e8ba5bf1120aad2",
                GappAuth.sha512("password", "salt", 2));
    }

    @Test void preservesLegacyAes128KeyFormat() {
        var maker = new GappKeyMaker();
        String first = maker.getNewKey();
        assertEquals(16, Base64.getDecoder().decode(first).length);
        assertNotEquals(first, maker.getNewKey());
    }
}
