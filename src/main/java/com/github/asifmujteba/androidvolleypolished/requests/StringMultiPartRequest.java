package com.github.asifmujteba.androidvolleypolished.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by asifmujteba on 16/05/15.
 */
public class StringMultiPartRequest extends AbstractMultiPartRequest<String> {

    public StringMultiPartRequest(String url,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public StringMultiPartRequest(String url,
                                  Map<String, String> headers,
                                  Map<String, File> fileUploads,
                                  Map<String, String> stringUploads,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener) {
        super(url, headers, fileUploads, stringUploads, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
