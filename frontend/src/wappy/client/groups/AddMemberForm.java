package wappy.client.groups;

import java.util.Date;
import java.util.List;

import wappy.client.ResponseHandler;
import wappy.client.ServerComm;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class AddMemberForm extends LayoutContainer {
	private Member member;
	private Group group;
	
	private FormPanel fp = new FormPanel();
	private TextField<String> usrField = new TextField<String>();
	private ListStore<Group> grStore = new ListStore<Group>();
	private ComboBox<Group> grComboBox = new ComboBox<Group>();
	
	public AddMemberForm(final Command onMemberAdded) {
		fp.setHeaderVisible(false);
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.collapse();
		
		usrField.setFieldLabel("Username");
		usrField.setAllowBlank(false);
		fp.add(usrField);
		
		
//		grStore.add(groups);
//		grStore.setDefaultSort("name", SortDir.ASC);
		grComboBox.setStore(grStore);
		grComboBox.setFieldLabel("Group");
		grComboBox.setEmptyText("Choose a group");
		grComboBox.setDisplayField("name");
		grComboBox.setValueField("name");
		grComboBox.setAllowBlank(false);
		grComboBox.setForceSelection(true);
		fp.add(grComboBox);
		
		final Radio regularRadio = new Radio();  
		regularRadio.setBoxLabel("Regular");  
		regularRadio.setValue(true);
		
	    Radio adminRadio = new Radio();  
	    adminRadio.setBoxLabel("Admin");  
	  
	    RadioGroup radioGroup = new RadioGroup();  
	    radioGroup.setFieldLabel("As");
	    radioGroup.setTitle("Give the new member its permission level");
	    radioGroup.add(regularRadio);  
	    radioGroup.add(adminRadio);
	    fp.add(radioGroup);
	    
		final ResponseHandler rh = new ResponseHandler() {
	    	@Override
	    	public void on200Response(JSONValue value) {
				GroupsJSON jsonUtil = new GroupsJSON(value);            
                if (jsonUtil.noErrors()) {
                	member = GroupsJSON.getCreatedMember();
                	group = grComboBox.getValue();
                	collapse();
                	DeferredCommand.addCommand(onMemberAdded);
		         }
		        else {
		            MessageBox.alert("Groups", jsonUtil.getErrorVal(),
		            		null);
		        }
	    	}
	    };
	    
	    fp.addButton(new Button("Add", new SelectionListener<ButtonEvent>(){
	    	@Override
	    	public void componentSelected(ButtonEvent ce) {
	    		if (fp.isValid(false)) {
	    			Member member = new Member(getUserName(), "", 0,
	    					false, false);
	    			ServerComm.addMember("Add member", getGroupName(), member, rh);
	    		}
	    	}
	    }));
	    
	    fp.addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
	    	@Override
	    	public void componentSelected(ButtonEvent ce) {
				collapse();
			}
		}));
	    
		add(fp);
	}
	
	private String getUserName() {
		return usrField.getValue();
	}
	
	private String getGroupName() {
		return grComboBox.getValue().getName();
	}
	
	public Group getGroup() {
		return this.group;
	}
	
	public Member getMember() {
		return this.member;
	}
	
	protected void collapse() {
		fp.collapse();
		usrField.setValue(null);
		usrField.clearInvalid();
		grComboBox.clearSelections();
		grComboBox.clearInvalid();
	}
	
	public void expand() {
		fp.expand();
	}
	
	public boolean toggleCollapse() {
		if (fp.isCollapsed()) {
			expand();
			return true;
		}
		else {
			collapse();
			return false;
		}
	}
	
	

	public void updateGroupsList(List<Group> groups) {
		grStore.removeAll();
		grStore.add(groups);
		
	}

}
