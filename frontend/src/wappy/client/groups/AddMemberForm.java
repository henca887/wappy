package wappy.client.groups;

import java.util.List;

import wappy.client.ResponseHandler;

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
	private Radio regularRadio = new Radio();
	private Radio adminRadio = new Radio();
	
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
		
		regularRadio.setBoxLabel("Regular");  
		regularRadio.setValue(true);
		
	      
	    adminRadio.setBoxLabel("Admin");  
	  
	    RadioGroup radioGroup = new RadioGroup();  
	    radioGroup.setFieldLabel("As");
	    radioGroup.setTitle("Give the new member its permission level");
	    radioGroup.add(regularRadio);  
	    radioGroup.add(adminRadio);
	    fp.add(radioGroup);
	    
		final ResponseHandler rh = new ResponseHandler() {
	    	@Override
	    	public void onSuccess(JSONValue value) {
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
	    			GroupsComm.addMember(getGroupName(), getUserName(),
	    					isAdmin(), rh);
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
	
	private Boolean isAdmin() {
		return adminRadio.getValue();
	}
	
	public Group getGroup() {
		return this.group;
	}
	
	public Member getMember() {
		return this.member;
	}
	
	private void collapse() {
		fp.collapse();
		usrField.setValue(null);
		usrField.clearInvalid();
		grComboBox.clearSelections();
		grComboBox.clearInvalid();
		regularRadio.setValue(true);
	}
	
	private void expand() {
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
