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
        
	
	/**
	 * Constructor
	 * @param language  The language that will be used to translate.
	 */
	public GappTranslator(String language){
                String normalized = language == null ? "" : language.trim().toLowerCase(Locale.ROOT).replace('_', '-');
                String bundle = normalized.equals("en") || normalized.startsWith("en-") ? "en" : "es";
                i18n = I18nFactory.getI18n(GappTranslator.class, "gator.lib.i18.Messages_" + bundle);
	}
        /**
         * Constructor to use when the i18n object is already defined.
         */
	public GappTranslator() {}
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
	 * @param jsonstr  The JSON string that will be used to create the object, in order to translate correctly the 
	 * 		   object must contain a object called translation that will contain an array of objects to be
	 * 		   translated with an index starting at 0 called translate0, translate1, ..., translateN.
         *                  {"phrases":
         *                      [
         *                          {"phrase":"Execution successful","translation":"Ejecución exitosa","pluarTranslation":"Ejecución exitosa"}
         *                      ]
         *                  }
	 * @return The JSON string with the translated strings.
	 */
	public String getTranslated(String jsonstr) {
                if (jsonstr == null || jsonstr.isBlank()) throw new IllegalArgumentException("Missing translation request");
                Gson gson = new GsonBuilder().setStrictness(Strictness.STRICT).create();
                JsonElement document;
                try {
                        document = gson.fromJson(jsonstr, JsonElement.class);
                } catch (com.google.gson.JsonParseException ex) {
                        throw new IllegalArgumentException("Invalid translation request");
                }
                if (document == null || !document.isJsonObject()) throw new IllegalArgumentException("Invalid translation request");
                JsonElement entries = document.getAsJsonObject().get("phrases");
                if (entries == null || !entries.isJsonArray() || entries.getAsJsonArray().isEmpty()) {
                        throw new IllegalArgumentException("Missing phrases");
                }
                for (JsonElement entry : entries.getAsJsonArray()) {
                        JsonElement phrase = entry.isJsonObject() ? entry.getAsJsonObject().get("phrase") : null;
                        if (phrase == null || !phrase.isJsonPrimitive() || !phrase.getAsJsonPrimitive().isString()
                                || phrase.getAsString().isBlank()) throw new IllegalArgumentException("Invalid phrase");
                }
                GappPhrases phrases;
                try {
                        phrases = gson.fromJson(document, GappPhrases.class);
                } catch (com.google.gson.JsonParseException ex) {
                        throw new IllegalArgumentException("Invalid translation request");
                }
                for (GappPhrase phrase : phrases.getPhrases()) phrase.setTrasnlation(i18n);
                return gson.toJson(phrases);
	}
        
        /**
         * Translate a singular string.
         * 
         * @param toTranslate   A string to be translated.
         * 
         * @return The translation or the original string.
         */
        public String translate(String toTranslate) {
                return i18n.tr(toTranslate) == null?toTranslate:i18n.tr(toTranslate);
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
