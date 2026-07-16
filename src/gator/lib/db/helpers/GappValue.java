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
package gator.lib.db.helpers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author sbasurto
 */
public class GappValue {
        /**
         * Simple value.
         */
         private String value = "";
         
         /**
          * An array.
          */
         private ArrayList<String> values = new ArrayList<>();
         
         /**
          * An object.
          */
         private HashMap<String, Object> objeto = new HashMap<>();
         
         /**
          * An integer.
          */
         private Integer number = -1;
         
         /**
          * Get value.
          * 
          * @return Value as string, empty if there is no value.
          */
         public String getValue() {
                return value;
         }
         
         /**
          * Allow to retrieve the values.
          * 
          * @return An array list of values.
          */
         public ArrayList<String> getValues() {
                return values;
         }
         
         /**
          * Allow to retrieve the object of this value.
          * 
          * @return A map of string, object.
          */
         public HashMap<String, Object> getObject() {
                return objeto;
         }
         
         /**
          * Get integer.
          * 
          * @return Value as string, empty if there is no value.
          */
         public Integer getInteger() {
                return number;
         }
}
