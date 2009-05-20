package wappy.client.groups;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.util.TreeBuilder;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FlexTable;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class Groups extends LayoutContainer{
	private static final String URL_CREATE_GROUP = "/groups/create_group/";
	private static final String URL_GET_GROUPS = "/groups/get_groups/";
	private static final String URL_ADD_MEMBER = "/groups/add_member/";
	
	private ContentPanel panel = new ContentPanel();
	private FlexTable table = new FlexTable();
	private List<Group> groups = new ArrayList<Group>();
	private Tree groupTree = new Tree();
	class Group {
		private String name;
		private List<String> members;
		
		public Group(String name, List<String> members) {
			this.name = name;
			this.members = members;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setMembers(List<String> members) {
			this.members = members;
		}
		public List<String> getMembers() {
			return members;
		}
		
	}
	public Groups() {
		panel.setLayout(new FlowLayout());
		getGroups();
		
		
		groupTree.getStyle().setLeafIconStyle("wappy-groups-member-icon");
//		TextField<String> userField = new TextField<String>();
//		Button addBtn = new Button("Add user");
		panel.add(table);
		panel.add(groupTree);
		add(panel);
		
	}

	private  void getGroups() {
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, URL_GET_GROUPS);

        try {
        	builder.sendRequest("", new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert("Alert:Groups:getGroups", "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                	if (response.getStatusCode() == 200) {
                		JSONWrapper root = new JSONWrapper(
                                JSONParser.parse(response.getText()));
                        JSONWrapper result = root.get("result");
                        JSONWrapper error = root.get("error");

                        if (error.isNull()) { // properly checked?
                			Info.display("DEBUG:Groups:getGroups: Success",
                            		"Groups have been retrieved!");
                			for (int i = 0; i < result.size(); i++) {
                            	String name = result.get(i).get("group").stringValue();
                            	List<String> members = new ArrayList<String>();
                        		JSONArray jsArr = 
                        			result.get(i).get("members").getValue().isArray();
                        		
                        		for (int j = 0; j < jsArr.size(); j++) {
                        			members.add(jsArr.get(j).isString().stringValue());
                        		}
                            	Group group = new Group(name, members);
                            	groups.add(group);
                            }
//                            groupsView.update(groups);
                            udateGroupView();
                            updateTree(groups);
                        }
                		else {
                			MessageBox.alert("DEBUG:Groups", 
                					error.getValue().toString(), null);
                		}
                    }
                    else {
                    	MessageBox.alert("Alert:Groups:getGroups:", "Http Error =(", null);
                    }
                }       
            });
        }
        catch (RequestException e) {
        	MessageBox.alert("Alert:Groups:getGroups:", "Http Error =(, Exception", null);
        }
		
	}
	
	protected void updateTree(List<Group> groups) {
//		TreeBuilder.buildTree(tree, root)
	}

	protected void udateGroupView() {
		if (groups == null) {
			table.setText(0, 0, "You don't participate in any group.");
		}
		else {
			for (int i = 0; i < groups.size(); i++) {
				String grName = groups.get(i).getName();
				List<String> members = groups.get(i).getMembers();
				table.setText(i, 0, grName);
				for (int j = i; j < members.size(); j++) {
					table.setText(j+1, 1, members.get(j));
				}
			}
		}
		
	}

	public void createGroup(String grName) {
		
	}
	
	public void addMember(String userName) {
		
	}
}
