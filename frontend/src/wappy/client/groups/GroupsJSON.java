package wappy.client.groups;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class GroupsJSON {
	private JSONWrapper wrapper;
	
	public GroupsJSON(JSONValue value) {
		this.wrapper = new JSONWrapper(
				JSONParser.parse(value.toString()));
	}

	public boolean noErrors() {
		return wrapper.get("error").isNull();
	}
	
	public String getErrorVal() {
		return wrapper.get("error").getValue().toString();
	}
	
	private JSONWrapper getResult() {
		return wrapper.get("result");
	}

	private Boolean toBoolean(JSONWrapper wrapper) {
		return wrapper.getValue().isBoolean().booleanValue();
	}
	
	public List<Group> getGroups() {
		JSONWrapper result = getResult();
		List<Group> groups = new ArrayList<Group>();
		for (int i = 0; i < result.size(); i++) {
        	JSONWrapper groupWrapper = result.get(i).get("group");
			String name = groupWrapper.get("name").stringValue();
			Boolean isPublic = toBoolean(groupWrapper.get("is_public"));
    		Boolean isRequestsAllowed = 
    			toBoolean(groupWrapper.get("requests_allowed"));
        	
    		JSONWrapper membersWrapper = result.get(i).get("members");
    		List<Member> members;
    		if (!membersWrapper.isNull()) {
    			 members = getMembers(membersWrapper);
    		}
    		else {
    			members = null;
    		}
        	Group group = new Group(name, isPublic, isRequestsAllowed,
					members);
        	groups.add(group);
        }
		return groups;
	}

	private List<Member> getMembers(JSONWrapper wrapper) {
		List<Member> members = new ArrayList<Member>();
		for (int j = 0; j < wrapper.size(); j++) {
			JSONWrapper memberWrapper = wrapper.get(j);
			members.add( getMember(memberWrapper) );
		}
		return members;
	}

	private Member getMember(JSONWrapper wrapper) {
		String name = wrapper.get("name").stringValue();
		String mail = wrapper.get("email").stringValue();
		long timestamp = wrapper.get("join_date").longValue()*1000;
		Boolean isOwner = toBoolean(wrapper.get("is_owner"));
		Boolean isAdmin = toBoolean(wrapper.get("is_admin"));
		return new Member(name, mail, timestamp, isOwner, isAdmin);
	}
}
