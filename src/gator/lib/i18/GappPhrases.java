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
package gator.lib.i18;

import java.util.ArrayList;

/**
 * An set of phrases.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappPhrases {
        /**
         * A set of phrases.
         */
        private final ArrayList<GappPhrase> phrases;

        /**
         * Constructor.
         */
        public GappPhrases() {
                this.phrases = new ArrayList<>();
        }
        
        /**
         * Allows to add new phrases.
         * @param phrase To be added to set.
         */
        public void addPhrase(GappPhrase phrase) {
                phrases.add(phrase);
        }
        
        /**
         * Retrieve the set of phrases.
         * @return Set of phrases.
         */
        public ArrayList<GappPhrase> getPhrases() {
                return phrases;
        }
}
