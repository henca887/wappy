package wappy.client.mail;

import com.google.gwt.user.client.ui.HTML;

import com.extjs.gxt.ui.client.widget.ContentPanel; 


public class MessageView extends ContentPanel {
    public MessageView() {
        setHeaderVisible(false);
        add(new HTML("MessageView"));
    }
}
