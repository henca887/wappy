package wappy.client.mail;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.Style.Scroll;

import com.pathf.gwt.util.json.client.JSONWrapper;

import wappy.client.SimpleJSON;

public class MessageView extends ContentPanel {
    HtmlContainer view = new HtmlContainer();

    public MessageView() {
        setHeaderVisible(false);
        setScrollMode(Scroll.AUTOY);
        add(view);
    }

    public void display(String path, String uid) {
        JSONObject requestData = new JSONObject();
        requestData.put("path", new JSONString(path));
        requestData.put("uid", new JSONString(uid));

        new SimpleJSON() {
            public void onSuccess(JSONWrapper response) {
                String content = response.get("content").stringValue();
                if (content != null) {
                    view.setHtml(content);
                }
            }
        }.query("/mail/messages/content/", requestData.toString());
    }
}
