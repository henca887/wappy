package wappy.client.groups;

import java.util.ArrayList;

import wappy.client.ResponseHandler;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class CreateForm extends LayoutContainer {
	private Group group;

	private final FormPanel fp = new FormPanel();
	private final TextField<String> grNameField = new TextField<String>();
	private Radio publicRadio = new Radio();
	private Radio privateRadio = new Radio();
	private Radio allowReqRadio = new Radio();
	private Radio denyReqRadio = new Radio();
	
	public CreateForm(final Command onGroupCreated) {
		fp.setHeaderVisible(false);
		fp.collapse();
		
		
		grNameField.setFieldLabel("Group name");
		grNameField.setToolTip("Enter a group name");
		grNameField.setAllowBlank(false);
		grNameField.setMaxLength(64);
		grNameField.setAutoValidate(true);
		grNameField.setValidationDelay(300);
		fp.add(grNameField);
		
		publicRadio.setBoxLabel("Public");  
		publicRadio.setValue(true);
		
	    privateRadio.setBoxLabel("Private");  
	  
	    RadioGroup radioGroup = new RadioGroup();  
	    radioGroup.setFieldLabel("Visibility");  
	    radioGroup.add(publicRadio);  
	    radioGroup.add(privateRadio);
	    fp.add(radioGroup); 
	    
	    allowReqRadio.setBoxLabel("Yes");
	    allowReqRadio.setValue(true);
	    
	    denyReqRadio.setBoxLabel("No");
	    
	    radioGroup = new RadioGroup();
	    radioGroup.setFieldLabel("Allow requests");
	    radioGroup.add(allowReqRadio);
	    radioGroup.add(denyReqRadio);
	    fp.add(radioGroup);
	    
	    fp.setButtonAlign(HorizontalAlignment.CENTER);
	    
	    final ResponseHandler rh = new ResponseHandler() {
	    	@Override
	    	public void onSuccess(JSONValue value) {
				GroupsJSON jsonUtil = new GroupsJSON(value);            
                if (jsonUtil.noErrors()) {
		            Info.display("", "New group created!");
		            collapse();
		            DeferredCommand.addCommand(onGroupCreated);
		         }
		        else {
		            MessageBox.alert("Groups", jsonUtil.getErrorVal(),
		            		null);
		        }
	    	}
	    };
	    fp.addButton(new Button("Create", new SelectionListener<ButtonEvent>(){
	    	@Override
	    	public void componentSelected(ButtonEvent ce) {
	    		if (fp.isValid(false)) {
	    			group = new Group(getName(), true, true,
	    					isPublic(), isRequestsAllowed(),
	    					new ArrayList<Member>());
	    			GroupsComm.createGroup(group, rh);
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

	protected Boolean isRequestsAllowed() {
		return allowReqRadio.getValue();
	}

	private Boolean isPublic() {
		return publicRadio.getValue();
	}

	private String getName() {
		return grNameField.getValue();
	}

	public Group getGroup() {
		return this.group;
	}
	
	private void collapse() {
		fp.collapse();
		grNameField.setValue(null);
		grNameField.clearInvalid();
	}
	
	public void expand() {
		fp.expand();
	}

	public void toggleCollapse() {
		if (fp.isCollapsed()) {
			expand();
		}
		else {
			collapse();
		}
	}
}
