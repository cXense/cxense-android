package com.cxense.cxensesdk.model;

/**
 * Request widget data object for server
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class WidgetRequest {
    public String widgetId;
    public ContentUser user;
    public WidgetContext context;
    public String con;

    public WidgetRequest() {
    }

    public WidgetRequest(String widgetId, WidgetContext widgetContext, ContentUser widgetUser, String consent) {
        this();
        this.widgetId = widgetId;
        user = widgetUser;
        context = widgetContext;
        con = consent;
    }
}
