package com.github.asifmujteba.androidvolleypolished.requests;

import com.android.volley.Response;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by asifmujteba on 23/04/15.
 */
public class GsonObjectRequest extends GsonRequest<JsonObject> {

    public GsonObjectRequest(int method,
                             String url,
                             Response.Listener<JsonObject> listener,
                             Response.ErrorListener errorListener) {
        super(method, url, JsonObject.class, listener, errorListener);
    }

    public GsonObjectRequest(int method,
                             String url,
                             Map<String, String> headers,
                             Map<String, String> params,
                             Response.Listener<JsonObject> listener,
                             Response.ErrorListener errorListener) {
        super(method, url, JsonObject.class, headers, params, listener, errorListener);
    }
}
