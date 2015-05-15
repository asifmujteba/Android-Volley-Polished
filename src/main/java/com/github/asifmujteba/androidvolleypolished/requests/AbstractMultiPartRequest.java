package com.github.asifmujteba.androidvolleypolished.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import okio.Buffer;

/**
 * Created by asifmujteba on 24/04/15.
 */
public class AbstractMultiPartRequest<T> extends Request<T> {

    private final Map<String, String> headers;
    private Map<String,File> fileUploads;
    private Map<String,String> stringUploads;
    private final Response.Listener<T> listener;

    private RequestBody requestBody = null;

    public AbstractMultiPartRequest(String url,
                                    Response.Listener<T> listener,
                                    Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.headers = new HashMap();
        this.fileUploads = new HashMap();
        this.stringUploads = new HashMap();
        this.listener = listener;
    }

    public AbstractMultiPartRequest(String url,
                                    Map<String, String> headers,
                                    Map<String, File> fileUploads,
                                    Map<String, String> stringUploads,
                                    Response.Listener<T> listener,
                                    Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.headers = headers;
        this.fileUploads = fileUploads;
        this.stringUploads = stringUploads;
        this.listener = listener;
    }

    public AbstractMultiPartRequest addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public AbstractMultiPartRequest addStringPart(String key, String value) {
        stringUploads.put(key, value);
        return this;
    }

    public AbstractMultiPartRequest addFilePart(String key, File file) {
        fileUploads.put(key, file);
        return this;
    }

    private RequestBody buildMultipartEntity() {
        if (requestBody == null) {
            MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

            for (String key : stringUploads.keySet()) {
                String value = stringUploads.get(key);
                multipartBuilder.addFormDataPart(key, value);
            }

            for (String key : fileUploads.keySet()) {
                File value = fileUploads.get(key);
                String name = value.getName();
                String contentType = URLConnection.guessContentTypeFromName(name);
                multipartBuilder.addFormDataPart(key, name,
                        RequestBody.create(MediaType.parse(contentType), value));
            }

            requestBody = multipartBuilder.build();
        }

        return requestBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return buildMultipartEntity().contentType().toString();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        Buffer buffer = new Buffer();
        try {
            buildMultipartEntity().writeTo(buffer);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return buffer.readByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
