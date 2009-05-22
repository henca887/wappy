package wappy.client.groups;

import wappy.client.ResponseHandler;
import wappy.client.ServerComm;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class AddMemberForm extends LayoutContainer {
	private Member member;
	
	private FormPanel fp = new FormPanel();
	private TextField<String> usrField = new TextField<String>();
	// TODO: Implement group list to choose from when adding member
	private ListField<ModelData> grList = new ListField<ModelData>();
	public AddMemberForm(final Command onMemberAdded) {
		
		
		fp.setHeaderVisible(false);
		fp.collapse();
		
		usrField.setFieldLabel("Username");
		fp.add(usrField);
		
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		
		final ResponseHandler rh = new ResponseHandler() {
	    	@Override
	    	public void on200Response(JSONValue value) {
				GroupsJSON jsonUtil = new GroupsJSON(value);            
                if (jsonUtil.noErrors()) {
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
	    			member = ServerComm.addMember(usrField.getValue(),
	    					getGroupName(), rh);
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
	
	private String getGroupName() {
		return null;
	}

	protected void collapse() {
		fp.collapse();
	}

	public Member getMember() {
		return this.member;
	}

	public void expand() {
		fp.expand();
	}

	public void toggleCollapse() {
		if (fp.isCollapsed()) {
			fp.expand();
		}
		else {
			fp.collapse();
		}
	}

}
