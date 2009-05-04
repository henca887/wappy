package wappy.client.mail;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import com.google.gwt.json.client.JSONParser;

import com.pathf.gwt.util.json.client.JSONWrapper;

import com.extjs.gxt.ui.client.widget.ContentPanel; 

//public class FolderTree extends Composite {
public class FolderTree extends ContentPanel {
    private MessageList messageList;
    private Tree folderTree = new Tree();

    public FolderTree(MessageList messageList) {
        this.messageList = messageList;
        setHeading("Folders");
        
        folderTree.addItem("Hello").addItem("World");
        
        refresh();
        
        messageList.display("INBOX");

        add(folderTree);
        //initWidget(folderTree);
    }

    public void refresh() {
        folderTree.clear();

        RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, "/mail/folders/");

        try {
            builder.sendRequest("", new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                }

                public void onResponseReceived(Request request,
                                               Response response) {
                    if (response.getStatusCode() != 200) return;
                    JSONWrapper root = new JSONWrapper(
                        JSONParser.parse(response.getText()));
                    JSONWrapper result = root.get("result");

                    buildTree(result, null);
                }
            });
        }
        catch (RequestException e) {
        }
    }

    private void buildTree(JSONWrapper tree, TreeItem node) {
        if (node == null) {
            for (int i = 0; i < tree.size(); i++) {
                TreeItem next = folderTree.addItem(
                    tree.get(i).get("name").stringValue());
                buildTree(tree.get(i).get("childs"), next);
            }
        }
        else {
            for (int i = 0; i < tree.size(); i++) {
                TreeItem next = node.addItem(
                    tree.get(i).get("name").stringValue());
                buildTree(tree.get(i).get("childs"), next);
            }
        }
    }
}
