package com.github.asifmujteba.androidvolleypolished;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.github.asifmujteba.androidvolleypolished.caches.LruBitmapCache;
import com.github.asifmujteba.androidvolleypolished.stacks.OkHttpStack;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by asifmujteba on 23/04/15.
 */
public abstract class AbstractVolleyManager {

    public static final String TAG = AbstractVolleyManager.class.getName().toString();

    public AbstractVolleyManager() {}

    /**
     * Constructor which uses custom image loader instead of default
     * @param imageLoader ImageLoader to use
     */
    public AbstractVolleyManager(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    /**
     * Get Request Queue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getContext(), getHttpStack());
        }

        return mRequestQueue;
    }

    /**
     * Get ImageLoader instance
     * In case, an image loader does not exists then create a new one
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, getLruBitmapCache());
        }
        return this.mImageLoader;
    }

    /**
     * Cancel all pending request in the entire queue
     */
    public void cancelAllPendingRequests() {
        if (mRequestQueue != null) {
            requestsInProgress.clear();
            mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });;
        }
    }

    /**
     * Cancel all pending requests associated with given tag
     * @param tag tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            removeFromInProgressWithTag(tag);
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * Cancel all pending requests created without passing a tag,
     * hence associated with Class's default tag
     */
    public void cancelPendingRequestsWithoutTag() {
        cancelPendingRequests(TAG);
    }

    /**
     * Add request to RequestQueue
     * @param req request to add
     * @param retryPolicy retryPolicy for the request
     * @param tag tag to associate request with
     * @param uniqueIdentifier A unique identifier which identifies this request,
     *                         this can be later used to query if request is in progress or not
     */
    public <T> void addToRequestQueue(Request<T> req, RetryPolicy retryPolicy, Object tag,
                                      String uniqueIdentifier) {
        if (tag == null || (tag instanceof String && ((String) tag).length() == 0)) {
            tag = TAG;
        }

        if (!TextUtils.isEmpty(uniqueIdentifier)) {
            setInProgress(tag, uniqueIdentifier, true);
        }

        req.setTag(tag);
        req.setRetryPolicy(retryPolicy);
        getRequestQueue().add(req);
    }

    /**
     * Add request to RequestQueue this uses default retry policy
     * @param req request to add
     * @param tag tag to associate request with
     * @param uniqueIdentifier A unique identifier which identifies this request,
     *                         this can be later used to query if request is in progress or not
     */
    public <T> void addToRequestQueue(Request<T> req, String tag, String uniqueIdentifier) {
        addToRequestQueue(req, getRetryPolicy(), tag, uniqueIdentifier);
    }

    /**
     * Add request to RequestQueue this uses default retry policy and request is not tracked if its
     * in progress or not
     * @param req request to add
     * @param tag tag to associate request with
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        addToRequestQueue(req, getRetryPolicy(), null, null);
    }

    /**
     * Add request to RequestQueue this uses default retry policy, is associated with default tag
     * and request is not tracked if its in progress or not
     * @param req request to add
     */
    public <T> void addToRequestQueue(Request<T> req) {
        addToRequestQueue(req, getRetryPolicy(), null, null);
    }

    /**
     * Check if a request is in progress or not
     * @param uniqueIdentifier A unique identifer for identifying request
     */
    public boolean isInProgress(String uniqueIdentifier) {
        Iterator iterator = requestsInProgress.iterator();
        while (iterator.hasNext()) {
            Pair pair = (Pair) iterator.next();
            if (pair.second.equals(uniqueIdentifier)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Abstract method to override
     * provides context
     */
    protected abstract Context getContext();

    /**
     * Provides http Stack
     * override this method if you want to a stack other than OkHttp
     */
    protected HttpStack getHttpStack() {
        return new OkHttpStack(new OkHttpClient());
    }

    /**
     * Provides network timeout time for the request
     * By default it returns 30 secs
     * override this method if you want to provide another duration
     */
    protected int getNetworkTimeOutTime() {
        return 1000 * 30;
    }

    /**
     * Provides LruBitmapCache implementation to use in ImageLoader
     * override this method if you want to provide another implementation
     */
    protected ImageLoader.ImageCache getLruBitmapCache() {
        return new LruBitmapCache();
    }

    /**
     * Provides retry policy for the request
     * By default it provides DefaultRetryPolicy with following parameters:
     * NetworkTimeout = getNetworkTimeOutTime()
     * Retries = DEFAULT_MAX_RETRIES
     * Backoff_mult = DEFAULT_BACKOFF_MULT
     * override this method if you want to provide another duration
     */
    protected RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(getNetworkTimeOutTime(),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }



    // ------- Private Section From Here -----------------

    private void setInProgress(Object tag, String uniqueIdentifier, boolean inProgress) {
        if (inProgress) {
            requestsInProgress.add(new Pair(tag, uniqueIdentifier));
        }
        else {
            removeFromInProgress(uniqueIdentifier);
        }
    }

    private void removeFromInProgressWithTag(Object tag) {
        Iterator iterator = requestsInProgress.iterator();
        while (iterator.hasNext()) {
            Pair pair = (Pair) iterator.next();
            if (pair.first.equals(tag)) {
                iterator.remove();
            }
        }
    }

    private void removeFromInProgress(String uniqueIdentifier) {
        Iterator iterator = requestsInProgress.iterator();
        while (iterator.hasNext()) {
            Pair pair = (Pair) iterator.next();
            if (pair.second.equals(uniqueIdentifier)) {
                iterator.remove();
            }
        }
    }


    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private HashSet<Pair<Object, String>> requestsInProgress = new HashSet<>();

}
