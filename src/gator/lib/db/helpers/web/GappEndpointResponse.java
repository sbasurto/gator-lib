/*
 * Copyright (C) 2024 Sergio Basurto Juárez
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
package gator.lib.db.helpers.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gator.lib.db.helpers.GappResponse;
import gator.lib.db.helpers.GappResponses;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappEndpointResponse {
    private final GappResponses responses = new GappResponses();    
    public void addResponse(GappResponse response) {
        responses.addResponse(response);
    }
    public GappResponses getResponses() {
        return responses;
    }
    public void addResponseParams(String resNum, String message, String dataSize) {
        GappResponse response = new GappResponse();
        response.setResNum(resNum);
        response.addResponse("message", message);
        response.addResponse("dataSize", dataSize);
        responses.addResponse(response);
    }
    public String getResponseJson() {
        Gson gson = new Gson();
        return gson.toJson(responses);
    }
    public void setResponseDesc(String description) {
        responses.getResponses().get(responses.getResponses().size() - 1).setResDesc(description);
    }
    public void addObjectToResponse(String key, JsonObject obj) {
        responses.getResponses().get(responses.getResponses().size() - 1).addResponse(key, obj);
    }
    public void addStringToResponse(String key, String str) {
        responses.getResponses().get(responses.getResponses().size() - 1).addResponse(key, str);
    }
}
