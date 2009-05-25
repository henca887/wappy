package wappy.client;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.json.client.JSONValue;

public abstract class ResponseHandler {
	public void onSuccess(JSONValue root){}

	public void onError() {
		MessageBox.alert("Alert", "Request error!", null);
	}
	
	public void onFailure() {
		MessageBox.alert("Alert", "HTTP error!", null);
	}
	
	public void onException() {
		MessageBox.alert("Alert", "Request exception raised!", null);
	}
}
