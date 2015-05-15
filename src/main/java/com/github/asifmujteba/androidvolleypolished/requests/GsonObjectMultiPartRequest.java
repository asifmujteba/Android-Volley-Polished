package com.github.asifmujteba.androidvolleypolished.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by asifmujteba on 24/04/15.
 */
public class GsonObjectMultiPartRequest extends AbstractMultiPartRequest<JsonObject> {

    private final Gson gson ;
    private final Class<JsonObject> clazz;

    public GsonObjectMultiPartRequest(String url,
                                      Response.Listener<JsonObject> listener,
                                      Response.ErrorListener errorListener) {
        super(url, listener, errorListener);

        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
        this.clazz = JsonObject.class;
    }

    public GsonObjectMultiPartRequest(String url,
                                      Map<String, String> headers,
                                      Map<String, File> fileUploads,
                                      Map<String, String> stringUploads,
                                      Response.Listener<JsonObject> listener,
                                      Response.ErrorListener errorListener) {
        super(url, headers, fileUploads, stringUploads, listener, errorListener);

        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
        this.clazz = JsonObject.class;
    }

    @Override
    protected Response<JsonObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
