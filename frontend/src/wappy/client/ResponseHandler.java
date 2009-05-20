package wappy.client;

import com.pathf.gwt.util.json.client.JSONWrapper;

public interface ResponseHandler {
	public void on200Response(JSONWrapper root);
}
