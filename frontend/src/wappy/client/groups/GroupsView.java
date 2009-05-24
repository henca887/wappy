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

// TODO: behöver inte skicka in hela klasser, utan bara strängar
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
		
		Menu contextMenu = createContexMenu();
		
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

	private TreeItem insertMember(TreeItem parent, Member member) {
		TreeItem child = new TreeItem();
		String name = member.getName();
		child.setText(name);
		child.setId(name);
		parent.add(child);
		if (member.isOwner()) {
			tree.getItemById(name).setStyleName("wappy-groups-owner-icon");
		}
		else if (member.isAdmin()) {
			tree.getItemById(name).setStyleName("wappy-groups-admin-icon");
		}
		return child;
	}

	public void insert(Group group) {
		TreeItem parent = new TreeItem();
		parent.setLeaf(false);
		String grName = group.getName();
		parent.setId(grName);
		
		tree.getRootItem().add(parent);
		parent.setText(grName);
		if (group.isOwner()) {
			tree.getItemById(grName).setStyleName("wappy-groups-owner-icon");
		}
		else if (group.isAdmin()) {
			tree.getItemById(grName).setStyleName("wappy-groups-admin-icon");
		}
		List<Member> members = group.getMembers();
		if(members != null) {
			for (int m = 0; m < members.size(); m++) {
				insertMember(parent, members.get(m));
			}
		}
		
	}

	public void insert(List<Group> groups) {
		for (int g = 0; g < groups.size(); g++) {
			insert(groups.get(g));
		}
	}
	
	public void insertNewMember(Group group, Member member) {
		TreeItem parent = tree.getItemById(group.getName());
		TreeItem child = insertMember(parent, member);
		tree.expandPath(child.getPath());
	}
}
