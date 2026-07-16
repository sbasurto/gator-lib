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

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class GappEndpointRequest {
    private String data = "";
    private String apiKey = "";
    private String iv = "";
    public void setData(String _data) {
        data = _data;
    }
    public void setApiKey(String _apiKey) {
        apiKey = _apiKey;
    }
    public void setIV(String _iv) {
        iv = _iv;
    }
    public String getData() {
        return data.replaceAll(" ","+");
    }
    public String getApiKey() {
        return apiKey;
    }
    public String getIV() {
        return iv.replaceAll(" ","+");
    }
    public void processOldFormat(String info) {
            String tmp[] = info.split("::@@::");
            if(tmp.length >= 3) {
                    setApiKey(tmp[0]);
                    setData(tmp[1]);
                    setIV(tmp[2]);
            }
    }
    public boolean isComplete() {
        return !getData().equals("") && !getApiKey().equals("") && !getIV().equals("");
    }
    public String missingParam() {
            if(getData().equals("")) {
                    return "1";
            } else if(getApiKey().equals("")) {
                    return "0";
            } else if(getIV().equals("")) {
                    return "2";
            } else {
                    return "all";
            }
    }
}
