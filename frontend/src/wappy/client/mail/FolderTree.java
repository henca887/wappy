package wappy.client.mail;

import com.extjs.gxt.ui.client.widget.ContentPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;

import com.google.gwt.json.client.JSONParser;

import com.pathf.gwt.util.json.client.JSONWrapper;

import wappy.client.SimpleJSON;


public class FolderTree extends ContentPanel/* implements TreeListener */{
    final MessageList messageList;
    final Tree folderTree = new Tree();

    public FolderTree(MessageList messageList) {
        this.messageList = messageList;

        setHeading("Folders");
        folderTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem selected = event.getSelectedItem();
                if (selected != null) {
                    String path = (String)selected.getUserObject();
                    if (path != null) {
                        FolderTree.this.messageList.display(path);
                    }
                }
            }
        });
        add(folderTree);
        
        refresh();
    }

    public void refresh() {
        folderTree.clear();

        new SimpleJSON() {
            public void onSuccess(JSONWrapper response) {
                    buildTree(response.get("result"), null);
            }
        }.query("/mail/folders/", "");
    }

    private void buildTree(JSONWrapper tree, TreeItem node) {
        if (node == null) {
            for (int i = 0; i < tree.size(); i++) {
                TreeItem next = folderTree.addItem(
                    tree.get(i).get("name").stringValue());
                next.setUserObject(next.getText());
                buildTree(tree.get(i).get("childs"), next);
            }
        }
        else {
            for (int i = 0; i < tree.size(); i++) {
                TreeItem next = node.addItem(
                    tree.get(i).get("name").stringValue());
                next.setUserObject(
                    ((String)node.getUserObject()) + "/" + next.getText());
                buildTree(tree.get(i).get("childs"), next);
            }
        }
    }
}
