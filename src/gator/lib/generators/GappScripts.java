/*
 * Copyright (C) 2023 Sergio Basurto Juárez
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
package gator.lib.generators;

import java.util.ArrayList;

/**
 * A list of scripts.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappScripts {
        /**
         * The specifics scripts for any element.
         */
        ArrayList<GappScript> scripts = new ArrayList<>();
        
        /**
         * Add scripts to the set.
         * @param script The script to be added.
         */
        public void addScript(GappScript script) {
                scripts.add(script);
        }
        
        /**
         * Get the complete list of scripts.
         * @return The list of scripts as array list object.
         */
        public ArrayList<GappScript> getScripts() {
                return scripts == null?new ArrayList<>():scripts;
        }
}
