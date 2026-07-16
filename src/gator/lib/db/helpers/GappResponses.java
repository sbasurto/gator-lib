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

/**
 * Responses get from database calls.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappResponses {
        /**
         * Array of responses.
         */
         private final ArrayList<GappResponse> responses = new ArrayList<>();
         
         /**
          * Get the responses.
          * @return Array list of GappResponse with responses.
          * @see GappResponse
          */
         public ArrayList<GappResponse> getResponses() {
                return responses;
         }
         /**
          * Allow to add response to the array.
          * @param response
          */
         public void addResponse(GappResponse response) {
                responses.add(response);
         }
         /**
          * Get the responses as JSON object.
          * @return String representing the responses.          
          */
         public String getResponsesAsJson() {
                Gson gson = new Gson();
                return gson.toJson(responses);
         }
}
