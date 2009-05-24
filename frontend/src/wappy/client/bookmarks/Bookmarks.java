package wappy.client.bookmarks;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.event.MenuEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.pathf.gwt.util.json.client.JSONWrapper;
import wappy.client.SimpleJSON;
import wappy.client.shareables.ShareableItem;

public class Bookmarks extends ContentPanel {
    private ToolBar toolBar = new ToolBar();
    private final Tree tree = new Tree();

    public Bookmarks() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
        
        Button addBookmarkButton = new Button("Add Bookmark",
            new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                addBookmark();
            }
        });
        toolBar.add(addBookmarkButton);

        tree.addListener(Events.OnDoubleClick, new Listener<TreeEvent>() {
            public void handleEvent(TreeEvent event) {
                TreeItem item = event.getItem();
                if (item.isLeaf()) {
                    String url = (String)item.getData("url");
                    String uid = ((Long)item.getData("item_id")).toString();
                    com.google.gwt.user.client.Window.open(url, uid, "");
                }
            }
        });

        tree.setContextMenu(buildContexMenu());
        
        loadBookmarks();
        
        setTopComponent(toolBar);
        add(tree);
    }

	private Menu buildContexMenu() {
		Menu menu = new Menu();

		MenuItem remove = new MenuItem();  
	    remove.setText("Remove");  
	    remove.addSelectionListener(new SelectionListener<MenuEvent>() {  
	    	public void componentSelected(MenuEvent ce) {  
	    		TreeItem item = tree.getSelectedItem();
	    		if (item != null) {  
	    			if (item.isLeaf()) {
                        removeBookmark(Long.parseLong(item.getId()));
                    }
	    		}
		  	}  
	    });  
	    menu.add(remove);

		MenuItem share = new MenuItem();  
	    share.setText("Share");  
	    share.addSelectionListener(new SelectionListener<MenuEvent>() {  
	    	public void componentSelected(MenuEvent ce) {  
	    		TreeItem item = tree.getSelectedItem();
	    		if (item != null) {  
	    			if (item.isLeaf()) {
                        ShareableItem.share(Long.parseLong(item.getId()));
                    }
	    		}
		  	}  
	    });  
	    menu.add(share);

		return menu;  
	}

    private void removeBookmark(final long itemId) {
        ShareableItem.remove(itemId, new Command() {
            public void execute() {
                TreeItem item = tree.getItemById("" + itemId);
                item.getParentItem().remove(item);
            }
        });
    }

    private void addBookmark() {
        final Window window = new Window();
        window.setHeading("Add Bookmark");
        window.setWidth(350);
        window.setAutoHeight(true);
        
        final FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
        formPanel.setButtonAlign(HorizontalAlignment.CENTER);
        window.add(formPanel);

        final TextField<String> nameField = new TextField<String>();
		nameField.setFieldLabel("Name");
		nameField.setEmptyText("Enter name of the bookmark");
		nameField.setAllowBlank(false);
		formPanel.add(nameField);

        final TextField<String> urlField = new TextField<String>();
		urlField.setFieldLabel("URL");
		urlField.setEmptyText("Enter URL of the bookmark");
		urlField.setAllowBlank(false);
		formPanel.add(urlField);

		formPanel.addButton(
            new Button("Save", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
                addBookmark(nameField.getValue(), urlField.getValue());
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

    private void addBookmark(String name, String url) {
        JSONObject bookmark = new JSONObject();
        bookmark.put("name", new JSONString(name));
        bookmark.put("url", new JSONString(url));
        new SimpleJSON() {
            public void onSuccess(JSONWrapper response) {
                TreeItem myBookmarks = tree.getItemById("My Bookmarks");
                TreeItem item = new TreeItem();
                item.setLeaf(true);
                item.setText(response.get("name").stringValue());
                item.setId(response.get("item_id").longValue().toString());
                item.setData("url", response.get("url").stringValue());
                item.setData("item_id", response.get("item_id").longValue());
                item.setToolTip(response.get("url").stringValue());
                myBookmarks.add(item);
            }
        }.query("/bookmarks/add/", bookmark.toString());
    }

    private void loadBookmarks() {
        new SimpleJSON() {
            public void onSuccess(JSONWrapper response) {
                buildTree(response, tree.getRootItem());
            }
        }.query("/bookmarks/tree/", "");
    }

    private void buildTree(JSONWrapper data, TreeItem node) {
        for (int i = 0; i < data.size(); i++) {
            JSONWrapper nodeData = data.get(i);
            if (nodeData.get("leaf").longValue().longValue() != 0) {
                TreeItem item = new TreeItem();
                item.setLeaf(true);
                item.setText(nodeData.get("name").stringValue());
                item.setId(nodeData.get("item_id").longValue().toString());
                item.setData("url", nodeData.get("url").stringValue());
                item.setData("item_id", nodeData.get("item_id").longValue());
                item.setToolTip(nodeData.get("url").stringValue());
                node.add(item);
            }
            else {
                TreeItem item = new TreeItem();
                item.setLeaf(false);
                item.setText(nodeData.get("name").stringValue());
                item.setId(nodeData.get("name").stringValue());
                node.add(item);
                buildTree(nodeData.get("childs"), item);
            }
        }
    }
};
