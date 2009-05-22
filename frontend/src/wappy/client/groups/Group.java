package wappy.client.groups;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class Group extends BaseTreeModel {
	private static int ID = 0;
	
	public Group(String name, Boolean isPublic, Boolean isRequestsAllowed,
			List<Member> members) {
		set("id", ID++);
		set("name", name);
		set("isPublic", isPublic);
		set("isRequestsAllowed", isRequestsAllowed);
		set("members", members);
	}
	
	public Integer getId() {
		return (Integer) get("id");
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
	
	public void setMembers(List<Member> members) {
		set("members", members);
	}
	
	public void insertMember(Member m) {
		List<Member> members = getMembers();
		if (members.add(m)) {
			setMembers(members);
		}
	}
}
