package wappy.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class ServerComm {
	
	public static void sendPostRequest(String url,
			String msg, final ResponseHandler rh) {
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, url);

        try {
        	builder.sendRequest(msg, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                	rh.onError();
                }

                public void onResponseReceived(Request request, Response response) {
                	if (response.getStatusCode() == 200) {
                		JSONValue jsonValue = 
                			JSONParser.parse(response.getText());
                		rh.onSuccess(jsonValue);
                    }
                    else {
                    	rh.onFailure();
                    }
                }       
            });
        }
        catch (RequestException e) {
        	rh.onException();
        }
	}
	
}
