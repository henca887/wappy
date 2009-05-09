package wappy.client.mail;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import com.pathf.gwt.util.json.client.JSONWrapper;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.Style.Scroll;


public class MessageView extends ContentPanel {
    HtmlContainer view = new HtmlContainer();
    
    public MessageView() {
        setHeaderVisible(false);
        setScrollMode(Scroll.AUTOY);
        add(view);
    }
    
    public void display(String path, String uid) {
        RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, "/mail/messages/content/");

        try {
            JSONObject requestData = new JSONObject();
            requestData.put("path", new JSONString(path));
            requestData.put("uid", new JSONString(uid));
            
            builder.sendRequest(requestData.toString(), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                }

                public void onResponseReceived(Request request,
                                               Response response) {
                    if (response.getStatusCode() != 200) return;
                    JSONWrapper root = new JSONWrapper(
                        JSONParser.parse(response.getText()));
                    String content = root.get("content").stringValue();
                    if (content != null) {
                        view.setHtml(content);
                    }
                }
            });
        }
        catch (RequestException e) {
        }
    }
}
