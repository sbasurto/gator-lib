/*
 * Copyright (C) 2023 Sergio Basurto Juárez
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gator.lib.db.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Represent a database response for Soft Gator applications.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappDBResponse {
    /**
     * An array of JSON responses.
     */
    JsonArray responses = new JsonArray();
    /**
     * The current index of the array always starts at 0.
     */
    int index = 0;
    
    /**
     * A Gson object to code or decode JSON strings and objects.
     */
    Gson gson = new Gson();
    
    /**
     * Allow to add a response to this object.
     * @param response The response JSON as string to be added.
     */
    public void addResponse(String response) {
        JsonElement elJson = JsonParser.parseString(response);
        JsonObject result = elJson.getAsJsonObject();
        JsonArray resps = result.getAsJsonArray("responses");
        for(int i = 0; i < resps.size(); i++) {
            System.out.println(resps.get(i));
            responses.add(resps.get(i));
        }
    }
    /**
     * Get the responses as JSON object.
     * @return The JSON object representing the responses.
     */
    public String getResponseJson() {
          return  gson.toJson(responses);
    }
    /**
     * Get the responses as JSON string.
     * @return The JSON string representing the responses.
     */
    public String getResponsesAsString() {
        return responses.toString();
    }
}
