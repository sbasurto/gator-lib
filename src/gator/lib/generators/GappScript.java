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

/**
 * A representation of a script source.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappScript {
        /**
         * The name of the script, could be js, css or html.
         */
        String name;
        
        /**
         * The context of the script, could be head or inline.
         */
        String context;
        
        /**
         * Allows to set the name for the script.
         * @param _name The name for this script, could be any of js, css or html, case sensitive.
         */
        public void setName(String _name) {
                name = _name;
        }
        /**
         * Get the script name.
         * @return The name of the script.
         */
        public String getName() {
                return name == null?"":name;
        }
        /**
         * Allows to set the context.
         * @param _context The context where the script will be used, it could be head or inline, case sensitive.
         */
        public void setContext(String _context) {
                context = _context;
        }
        /**
         * Get the context of this script.
         * @return The context of this script.
         */
        public String getContext() {
                return context == null?"":context;
        }
}
