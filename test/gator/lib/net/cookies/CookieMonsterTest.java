package gator.lib.net.cookies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CookieMonsterTest {
    private final CookieMonster monster = new CookieMonster();

    @Test
    void readsPlainAndJsonCookieValues() {
        assertEquals("es", monster.eatCookie("language=es", "language").getValue());
        assertEquals("en", monster.eatCookie("language={\"value\":\"en\"}", "language").getValue());
        assertEquals("", monster.eatCookie(null, "language").getValue());
    }
}
