package wappy.client;

import com.google.gwt.json.client.JSONValue;

public interface ResponseHandler {
	public void on200Response(JSONValue root);
}
