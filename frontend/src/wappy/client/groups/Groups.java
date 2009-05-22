package wappy.client.groups;

import java.util.ArrayList;
import java.util.List;

import wappy.client.ResponseHandler;
import wappy.client.ServerComm;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;

public class Groups extends LayoutContainer{
	private CreateForm createForm;
	private GroupsView view = new GroupsView();
	
	private ContentPanel panel = new ContentPanel();
	private List<Group> groups = new ArrayList<Group>();
	
	
	private Command onGroupCreated = new Command() {
		@Override
		public void execute() {
			Group group = createForm.getGroup();
			groups.add(group);
			view.update(group);
		}
		
	};
	
	public Groups() {
		createForm = new CreateForm(onGroupCreated);
		
		panel.setLayout(new FlowLayout());
		panel.setHeading("Groups");
		panel.setCollapsible(true);
		getGroups();
		
		
		ToolBar toolBar = new ToolBar();
		
		Button createBtn = new Button("Create group", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				createForm.open();
			}
		});
		toolBar.add(createBtn);
		
		Button debugBtn = new Button("Debug:getgroups", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				getGroups();
			}
		});
		toolBar.add(debugBtn);
		
		panel.add(toolBar);
		panel.add(view);
		add(panel);
		
	}

	private  void getGroups() {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void on200Response(JSONValue value) {
				GroupsJSON jsonUtil = new GroupsJSON(value);            
                if (jsonUtil.noErrors()) {
					groups = jsonUtil.getGroups();
					Info.display("DEBUG:Groups:getGroups: Success",
                    		"Groups have been retrieved!");
        			
					view.update(groups);
                }
        		else {
        			Info.display("Groups", jsonUtil.getErrorVal());
        		}

			}
		};
		ServerComm.getGroups("Groups", rh);
	}

//	public void createGroup() {
//	}
//	
//	public void addMember(String userName) {
//		
//	}
}
