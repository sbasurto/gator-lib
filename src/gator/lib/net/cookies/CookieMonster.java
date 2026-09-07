/*
 * Copyright (C) 2022 Sergio Basurto Juárez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gator.lib.net.cookies;

import com.google.gson.Gson;
import gator.lib.logs.GappLogging;

/**
 * CookieMonster class manage all the actions that can be done in cookies. 
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class CookieMonster {
        /**
 	 * Log instance to do the log.
	 */
	private final GappLogging logs = new GappLogging();
        /**
         * Returns the value for the cookie specified with name.
         * 
         * @param rawCookiesJar String with header Cookie.
         * @param nameToSearch The name of the cookie we want to get the value.
         *
         * @return GappCookie object with cookie's name and value.
         */
        public GappCookie eatCookie(String rawCookiesJar, String nameToSearch) {
                GappCookie gappCookie = new GappCookie();
                logs.logIt("CookieMonster", "Digesting cookies jar: (" + rawCookiesJar + ")", "gapps", "gappcookies", 0);
                if (rawCookiesJar == null || nameToSearch == null) return gappCookie;
                String []cookiesJar = rawCookiesJar.split(";");
                for (String cookie : cookiesJar) {
                        int separator = cookie.indexOf('=');
                        if(separator > 0 && cookie.substring(0, separator).trim().equals(nameToSearch)) {
                                String value = cookie.substring(separator + 1);
                                logs.logIt("CookieMonster", "Digested cookie: (" + value + ")", "gapps", "gappcookies", 0);
                                if(value.startsWith("{")) {
                                        try {
                                                return new Gson().fromJson(value, GappCookie.class);
                                        } catch (RuntimeException ignored) {
                                                // A malformed client cookie is still a plain cookie value.
                                        }
                                }
                                return new GappCookie(nameToSearch, value);
                        }
                }
                return gappCookie;
        }        
}
