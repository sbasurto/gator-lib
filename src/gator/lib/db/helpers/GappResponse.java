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

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappResponse {
        /**
         * Number of responses 0 is success.
         */
         private String resNum = "-1";
         
         /**
          * Description of error.
          */
         private String resDesc = "";
         
         /**
         * Function for this response.
         */
         private String resFn = "";
         /**
          * Response content.
          */
         private final HashMap<String, Object> response = new HashMap<>();
         
         private final ArrayList<String> arrayOfValues = new ArrayList<>();
         
         private final ArrayList<HashMap<String,String>> arrayOfMap= new ArrayList<>();
         
         /**
          * Allow to set the number responded.
          * @param number number for response.          
          */
         public void setResNum(String number) {
                resNum = number;
         }
         /**
          * Allow to retrieve the number responded.
          * @return Response number 0 is success, error otherwise.
          */
         public String getResNum() {
                return resNum;
         }
         
         /**
          * Allow to set the response description.
          * @param description Description of the response.
          */
         public void setResDesc(String description) {
                resDesc = description;
         }
         /**
          * Allow to retrieve the error description.
          * @return Response number 0 is success, error otherwise.
          */
         public String getResDesc() {
                return resDesc;
         }
         
         /**
          * Allow to retrieve the actual response content.
          * 
          * @param key Add the key to store response.
          * @param _response The object to be stored.
          */
         public void addResponse(String key, Object _response) {
                response.put(key, _response);
         }
         /**
          * Allow to retrieve the actual response content.
          * 
          * @return The actual response content.
          */
         public HashMap<String, Object> getResponse() {
                return response;
         }
         /**
          * Allow to set the number responded.
          * @param fn function creating response.          
          */
         public void setResFn(String fn) {
                resFn = fn;
         }
         /**
          * Allow to retrieve the number responded.
          * @return Response number 0 is success, error otherwise.
          */
         public String getResFn() {
                return resFn;
         }
         /**
          * Get the responses as JSON object.
          * @return String representing the responses.          
          */
         public String getResponseAsJson() {
                Gson gson = new Gson();
                return gson.toJson(response);
         }
         /**
          * Get the responses as JSON object.
          * @param key
          * @return String representing the responses.          
          */
         public String getSpecificAsJson(String key) {
                Gson gson = new Gson();
                return gson.toJson(response.get(key));
         }
}
