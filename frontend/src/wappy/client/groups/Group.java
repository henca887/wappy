package wappy.client.groups;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Group extends BaseModel {
	
	public Group(String name, Boolean isPublic, Boolean isRequestsAllowed,
			List<Member> members) {
		set("name", name);
		set("isPublic", isPublic);
		set("isRequestsAllowed", isRequestsAllowed);
		set("members", members);
	}
	
	public String getName() {
		return get("name");
	}
	
	public Boolean isPublic() {
		return get("isPublic");
	}
	
	public Boolean isRequestsAllowed() {
		return get("isRequestsAllowed");
	}

	public List<Member> getMembers() {
		return get("members");
	}
}
