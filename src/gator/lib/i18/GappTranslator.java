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
package gator.lib.i18;

// msgfmt --java2 -d src/ -r gator.lib.i18.Messages -l es po/es.po

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import java.util.Locale;
import org.xnap.commons.i18n.*;

/**
 * Translator is the class that helps to translate any string on Soft Gator system
 *
 * @author      <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 14 Apr 2013
 *
 */
public class GappTranslator {
	private I18n i18n;    
	
	/**
	 * Constant for Spanish
	 */
	public static String SPANISH = "es";
	
	/**
	 * Constant for english
	 */
	public static String ENGLISH = "en";
	
	/**
	 * Constant for french
	 */
	public static String FRENCH = "fr";
        
        private static final Gson GSON = new GsonBuilder().setStrictness(Strictness.STRICT).create();
	
	/**
	 * Constructor
	 * @param language  The language that will be used to translate.
	 */
	public GappTranslator(String language){
                String tag = language == null ? "" : language.strip().replace('_', '-');
                String normalized = Locale.forLanguageTag(tag).getLanguage();
                String bundle = normalized.equals("en") ? "en" : "es";
                i18n = I18nFactory.getI18n(GappTranslator.class, "gator.lib.i18.Messages_" + bundle);
	}
        /**
         * Constructor to use when the i18n object is already defined.
         */
	public GappTranslator() { this("es"); }
	/**
	 * This function allow to get the instance of factory
	 * @return The instanced factory.
	 */
	public I18n getI18n(){
		return this.i18n;
	}

	/**
	 * This function translate any string contained on JSON object
	 *
	 * @param jsonstr JSON containing a nonempty phrases array of objects with nonblank string phrases.
         *                  {"phrases":
         *                      [
         *                          {"phrase":"Execution successful"}
         *                      ]
         *                  }
	 * @return The JSON string with the translated strings.
         * @throws IllegalArgumentException if the input does not satisfy the translation contract.
	 */
        public String getTranslated(String jsonstr) {
                JsonElement root;
                try {
                        root = GSON.fromJson(jsonstr, JsonElement.class);
                } catch (JsonParseException e) {
                        throw new IllegalArgumentException("Invalid translation request");
                }
                if (root == null || !root.isJsonObject()) {
                        throw new IllegalArgumentException("Invalid translation request");
                }
                JsonElement items = root.getAsJsonObject().get("phrases");
                if (items == null || !items.isJsonArray() || items.getAsJsonArray().isEmpty()) {
                        throw new IllegalArgumentException("Invalid translation request");
                }
                for (JsonElement item : items.getAsJsonArray()) {
                        JsonElement phrase = item.isJsonObject() ? item.getAsJsonObject().get("phrase") : null;
                        if (phrase == null || !phrase.isJsonPrimitive() || !phrase.getAsJsonPrimitive().isString()
                                        || phrase.getAsString().isBlank()) {
                                throw new IllegalArgumentException("Invalid translation request");
                        }
                }
                GappPhrases phrases;
                try {
                        phrases = GSON.fromJson(root, GappPhrases.class);
                } catch (JsonParseException e) {
                        throw new IllegalArgumentException("Invalid translation request");
                }
                for (GappPhrase phrase : phrases.getPhrases()) phrase.setTrasnlation(i18n);
                return GSON.toJson(phrases);
        }
        
        /**
         * Translate a singular string.
         * 
         * @param toTranslate   A string to be translated.
         * 
         * @return The translation or the original string.
         */
        public String translate(String toTranslate) {
                if (toTranslate == null) return "";
                String translated = i18n.tr(toTranslate);
                return translated == null ? toTranslate : translated;
        }
        
        /**
         * This method allow to translate a generic String in the best way.
         * @param toTranslate The string to be translated.
         * @param plural The plural form or empty string if there is not.
         * @param context The context to make the translation.
         * 
         * @return The translation if there is one or the original string.
         * 
         */
        public String getGenericTranslate(String toTranslate, String plural, String context) {
            toTranslate = toTranslate == null?"":toTranslate;
            plural = plural == null?"":plural;
            context = context == null?"":context;
            String translated = toTranslate;            
            if(!plural.equals("")) {
                        if(!context.equals("")) {
                            translated = i18n.trnc(context, toTranslate, plural, 2);
                        } else {
                            translated = i18n.trn(toTranslate, plural, 2);
                        }                
            } else {                                                        
                if(!toTranslate.equals("")) {                                
                    if(!context.equals("")) {                                        
                        translated = i18n.trc(context, toTranslate);                                
                    } else {                                        
                        translated = i18n.tr(toTranslate);
                                
                    }
                        
                }
                
            }
            return translated;
        }
        /**
         * Allow to set the i18 object to do the translator.
         * @param _i18n The object to be settled.
         */
        public void setI18n(I18n _i18n){
		this.i18n = _i18n;
	}
}
