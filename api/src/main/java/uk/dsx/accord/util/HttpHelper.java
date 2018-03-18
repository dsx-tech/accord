package uk.dsx.accord.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class HttpHelper {
    //TODO more reuse
    public static <T> T post(String url, String method) throws UnirestException {
        JSONObject body = new JSONObject();
        body.append("method", method);
        HttpResponse<JsonNode> resp = Unirest.post(url)
                .body(body)
                .asJson();
        JsonNode node = resp.getBody();
        String a = node.getObject().get("result").toString();
        return new Gson().fromJson(a, new TypeToken<T>() {}.getType());
    }

    public static <T, TParam> T post(String url, String method, TParam bodyObj) throws UnirestException {
        JSONObject body = new JSONObject();
        body.append("method", method);
        JSONArray params = new JSONArray();
        params.put(bodyObj);
        body.append("params", params);
        HttpResponse<JsonNode> resp = Unirest.post(url)
                .body(body)
                .asJson();
        JsonNode node = resp.getBody();
        String a = node.getObject().get("result").toString();
        return new Gson().fromJson(a, new TypeToken<T>() {}.getType());
    }
}
