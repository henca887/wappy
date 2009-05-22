package wappy.client.groups;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Member extends BaseModel{

	public Member(String name, String mail, long joinDate, 
			Boolean isOwner, Boolean isAdmin) {
		set("name", name);
		set("email", mail);
		set("joinDate", joinDate);
		set("isOwner", isOwner);
		set("isAdmin", isAdmin);

	}
	
	public String getName() {
		return get("name");
	}
	
	public String getMail() {
		return get("email");
	}
	
	public Boolean isOwner() {
		return get("isOwner");
	}
	
	public Boolean isAdmin() {
		return get("isAdmin");
	}
	
}
