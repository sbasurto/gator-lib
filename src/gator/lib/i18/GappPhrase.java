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

import org.xnap.commons.i18n.I18n;

/**
 * Representation of a phrase for translation.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappPhrase {
          
        /**
         * The actual original phrase.
         */
        private String phrase;
        
        /**
         * Translation of phrase, same phrase if there is not translation.
         */
        private String translation;
        
        /**
         * Translation to plural if there is one.
         */
        private String pluralTranslation;
        
        /**
         * Allow to set the string for this phrase.
         * @param _phrase String for this phrase as it is.
         */
        public void setPhrase(String _phrase) {
                this.phrase = _phrase;
        }
        /**
         * Allows to retrieve a phrase.
         * @return Empty string or defined phrase.
         */
        public String getPhrase() {
                return this.phrase == null?"":this.phrase;
        }
        /**
         * Allows to set the object to use for translations.
         * @param i18n An object to make translations.
         */
        public void setTrasnlation(I18n i18n) {
                this.translation = i18n.tr(phrase);
                this.pluralTranslation = i18n.trn(phrase, phrase, 2);
        }
        /**
         * Get the translation for this phrase.
         * @return String representing translation.
         */
        public String getTranslation() {
                return this.translation;
        }
        /**
         * Allow to set the plural form for this phrase.
         * @param pluralForm The phrase in plural form.
         */
        public void setPluralTrasnlation(String pluralForm) {
                this.pluralTranslation = pluralForm;
        }
        /**
         * Retrieves the plural form of this phrase.
         * @return The plural form for this phrase.
         */
        public String getPluralTranslation() {
                return this.pluralTranslation;
        }        
}
