package wappy.client.groups;

import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;

public class GroupsView extends LayoutContainer{
	private Tree tree = new Tree();
	
	public GroupsView() {
		setLayout(new FlowLayout());
		
		tree.getStyle().setLeafIconStyle("wappy-groups-member-icon");
		tree.setSelectionMode(SelectionMode.MULTI);
		
		Menu contextMenu = createContexMenu(); //new Menu();  
		
		tree.setContextMenu(contextMenu);
		
		TreeItem item = new TreeItem();
		item.setText("Hej");
		tree.getRootItem().add(item);
		
		add(tree);
	}
	
	private Menu createContexMenu() {
		Menu menu = new Menu();
		menu.setWidth(130);  
		  
	    MenuItem remove = new MenuItem();  
	    remove.setText("Remove Selected");  
	    remove.setIconStyle("wappy-icon-delete");  
	    remove.addSelectionListener(new SelectionListener<MenuEvent>() {  
	      public void componentSelected(MenuEvent ce) {  
	        TreeItem item = (TreeItem) tree.getSelectionModel().getSelectedItem();  
	        if (item != null) {  
	          item.getParentItem().remove(item);  
	        }  
	      }  
	    });  
	    menu.add(remove);
		return menu;  
	}
	
	public void update(Group group) {
		TreeItem item = new TreeItem();
		item.setText(group.getName());
		List<Member> members = group.getMembers();
		for (int m = 0; m < members.size(); m++) {
			TreeItem child = new TreeItem();
			String name = members.get(m).getName();
			child.setText(name);
			item.add(child);	// DEBUG: Blir nåt fel här
		}
		tree.getRootItem().add(item);
	}
	
	public void update(List<Group> groups) {
		for (int g = 0; g < groups.size(); g++) {
			update(groups.get(g));
		}
		
		
	}

}
