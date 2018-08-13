package com.cxense.cxensesdk;

/**
 * Interface that is implemented to discover when data loading has finished.
 *
 * @param <T> success data type
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public interface LoadCallback<T> {
    /**
     * Called when load has completed successfully.
     *
     * @param data result data
     */
    void onSuccess(T data);

    /**
     * Called when load has completed with error.
     *
     * @param throwable error
     */
    void onError(Throwable throwable);
}
