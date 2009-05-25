package wappy.client.groups;

import java.util.ArrayList;
import java.util.List;

import wappy.client.ResponseHandler;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;

public class Groups extends LayoutContainer{
	private CreateForm createForm;
	private AddMemberForm addForm;
	private GroupsView view;
	private ContentPanel panel = new ContentPanel();
	private List<Group> groups = new ArrayList<Group>();
	private List<Group> myAdminGroups = new ArrayList<Group>();
	
	private Button addBtn = new Button("Add user", new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {
			addForm.toggleCollapse();
		}
	});
	
	private Command onGroupCreated = new Command() {
		@Override
		public void execute() {
			Group group = createForm.getGroup();
			myAdminGroups.add(group);
			groups.add(group);
			addBtn.setEnabled(true);
			addForm.updateGroupsList(myAdminGroups);
			view.insert(group);
		}
		
	};
	private Command onMemberAdded = new Command() {
		@Override
		public void execute() {
			Group group = addForm.getGroup();
			Member member = addForm.getMember();
			groups.get(groups.indexOf(group)).insertMember(member);
			view.insertNewMember(group, member);
		}
		
	};
	
	private ResponseHandler removeMemberHandler = new ResponseHandler() {
		@Override
		public void onSuccess(JSONValue value) {
			GroupsJSON jsonUtil = new GroupsJSON(value);            
            if (jsonUtil.noErrors()) {
				view.removeItem();
            }
    		else {
    			MessageBox.alert("Remove", 
    					jsonUtil.getErrorVal(), null);
    		}
		}
	};
	
	private Command removeMember = new Command() {
		@Override
		public void execute() {
			String group = view.getMembersGroupName();
			String member = view.getMemberName();
			GroupsComm.removeMember(group, member, removeMemberHandler);
		}
	};
	
	private ResponseHandler removeGroupHandler = new ResponseHandler() {
		@Override
		public void onSuccess(JSONValue value) {
			GroupsJSON jsonUtil = new GroupsJSON(value);            
            if (jsonUtil.noErrors()) {
            	String groupName = view.getGroupName();
            	removeFromGroupsList(groupName);
            	view.removeItem();
            }
    		else {
    			MessageBox.alert("Remove", 
    					jsonUtil.getErrorVal(), null);
    		}
		}
	};
	
	private Command removeGroup = new Command() {
		@Override
		public void execute() {
			String groupName = view.getGroupName();
			GroupsComm.removeGroup(groupName, removeGroupHandler);
		}
	};
	
	public Groups() {
		getGroups();
		view = new GroupsView(removeMember, removeGroup);
		createForm = new CreateForm(onGroupCreated);
		addForm = new AddMemberForm(onMemberAdded);
				
		panel.setLayout(new FlowLayout());
		panel.setHeaderVisible(false);
		panel.setCollapsible(false);
		
		ToolBar toolBar = new ToolBar();
		
		Button createBtn = new Button("Create group", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				createForm.toggleCollapse();
			}
		});
		toolBar.add(createBtn);
		toolBar.add(new SeparatorToolItem());

		toolBar.add(addBtn);
		
		panel.add(toolBar);
		panel.add(createForm);
		panel.add(addForm);
		panel.add(view);
		add(panel);
	}

	private void removeFromGroupsList(String groupName) {
		for (int i = 0; i < myAdminGroups.size(); i++) {
			Group group = myAdminGroups.get(i);
			if (group.getName() == groupName) {
				myAdminGroups.remove(group);
				if (myAdminGroups.isEmpty()) {
					addBtn.setEnabled(false);
				}
				groups.remove(group);
				addForm.updateGroupsList(myAdminGroups);
				return;
			}
		}
	}

	private  void getGroups() {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void onSuccess(JSONValue value) {
				GroupsJSON jsonUtil = new GroupsJSON(value);            
                if (jsonUtil.noErrors()) {
					groups = jsonUtil.getGroups();
					getMyAdminGroups();
					Info.display("DEBUG:Groups:getGroups: Success",
                    		"Groups have been retrieved!");
        			
					view.insert(groups);
                }
        		else {
        			addBtn.setEnabled(false);
        			Info.display("Groups", jsonUtil.getErrorVal());
        		}
			}
		};
		GroupsComm.getGroups(rh);
	}

	private void getMyAdminGroups() {
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			if (group.isAdmin()) {
				myAdminGroups.add(group);
			}
		}
		if (myAdminGroups.isEmpty()) {
			addBtn.setEnabled(false);
		}
		else {
			addForm.updateGroupsList(myAdminGroups);
		}
	}

}
