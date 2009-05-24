package wappy.client.shareables;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.Window;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.pathf.gwt.util.json.client.JSONWrapper;
import wappy.client.SimpleJSON;

public class ShareableItem {
    public static void remove(long itemId, final Command onSuccess) {
        new SimpleJSON() {
            public void onSuccess(JSONWrapper response) {
                String error = response.get("error").stringValue();
                if (error == null) {
                    onSuccess.execute();
                }
                else {
                    MessageBox.alert("Failure!", error, null);
                }
            }
        }.query("/shareables/remove/", "" + itemId);
    }

    public static void share(final long itemId) {
        final Window window = new Window();
        window.setHeading("Add Bookmark");
        window.setWidth(350);
        window.setAutoHeight(true);

        final FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
        formPanel.setButtonAlign(HorizontalAlignment.CENTER);
        window.add(formPanel);

        final TextField<String> usernameField = new TextField<String>();
		usernameField.setFieldLabel("Username");
		usernameField.setEmptyText("Share resource with...");
		usernameField.setAllowBlank(false);
		formPanel.add(usernameField);

		formPanel.addButton(
            new Button("Share", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
                share(itemId, usernameField.getValue());
                window.hide();
			}
		}));

        window.show();
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                window.toFront();
            }
        });
    }

    private static void share(long itemId, String username) {
        JSONObject data = new JSONObject();
        data.put("item_id", new JSONString("" + itemId));
        data.put("username", new JSONString(username));
        new SimpleJSON() {
            public void onSuccess(JSONWrapper response) {
                String error = response.get("error").stringValue();
                if (error != null) {
                    MessageBox.alert("Failure!", error, null);
                }
            }
        }.query("/shareables/share/", data.toString());
    }
}
