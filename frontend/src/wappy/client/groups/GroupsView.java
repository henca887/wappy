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
import com.google.gwt.user.client.Command;

public class GroupsView extends LayoutContainer{
	private Tree tree = new Tree();
	private Command removeMember;
	private Command removeGroup;

	private TreeItem selectedItem;
	
	public GroupsView(Command removeMember, Command removeGroup) {
		setLayout(new FlowLayout());
		this.removeMember = removeMember;
		this.removeGroup = removeGroup;
		
		tree.getStyle().setLeafIconStyle("wappy-groups-member-icon");
		tree.getStyle().setItemStyle("wappy-groups-group-icon");
		tree.setSelectionMode(SelectionMode.MULTI);
		
		Menu contextMenu = createContexMenu(); //new Menu();  
		
		tree.setContextMenu(contextMenu);
		
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
	    		selectedItem = (TreeItem) tree.getSelectionModel().getSelectedItem();  
	    		if (selectedItem != null) {  
	    			if (selectedItem.isLeaf()) {
		    			removeMember.execute();
		    		}
			    	else {
			    		removeGroup.execute();
			    	}
	    		}  
		  	}  
	    });  
	    menu.add(remove);
		return menu;  
	}
	
	public String getMemberName() {
		return selectedItem.getText();
	}
	
	public String getMembersGroupName() {
		return selectedItem.getParentItem().getText();
	}
	
	public String getGroupName() {
		return selectedItem.getText();
	}
	
	public void removeItem() {
		selectedItem.getParentItem().remove(selectedItem);
	}
	
	public void insert(Group group) {
		TreeItem parent = new TreeItem();
		parent.setLeaf(false);
		tree.getRootItem().add(parent);
		parent.setText(group.getName());
		List<Member> members = group.getMembers();
		if(members != null) {
			for (int m = 0; m < members.size(); m++) {
				TreeItem child = new TreeItem();
				child.setText(members.get(m).getName());
				parent.add(child);
			}
		}
		
	}
	
	public void insert(List<Group> groups) {
		for (int g = 0; g < groups.size(); g++) {
			insert(groups.get(g));
		}
	}
	// TODO: null !!!!!
	public void insertMember(Group group, Member member) {
		// GRRRRtree.
	}

	

}
