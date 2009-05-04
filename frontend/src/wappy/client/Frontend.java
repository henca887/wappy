package wappy.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.RootPanel;

import com.extjs.gxt.ui.client.event.ButtonEvent;  
import com.extjs.gxt.ui.client.event.SelectionListener;  
import com.extjs.gxt.ui.client.widget.LayoutContainer;  
import com.extjs.gxt.ui.client.widget.TabItem;  
import com.extjs.gxt.ui.client.widget.TabPanel;  
import com.extjs.gxt.ui.client.widget.Window;  
import com.extjs.gxt.ui.client.widget.button.Button;  
import com.extjs.gxt.ui.client.widget.layout.FitData;  
import com.extjs.gxt.ui.client.widget.layout.FitLayout;  
import com.extjs.gxt.ui.client.widget.layout.FlowLayout; 

import wappy.client.mail.MailClient;

public class Frontend implements EntryPoint {
    public void onModuleLoad() {
        final Window window = new Window();  
        window.setSize(800, 600);  
        window.setPlain(true);  
        window.setHeading("Mail");  
        window.setLayout(new FitLayout());  
        
        window.add(new MailClient());

        Button btn = new Button("Mail");  
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {  
          @Override  
          public void componentSelected(ButtonEvent ce) {  
            window.show();
            window.toFront();
          }  
        });  
        
        RootPanel.get().add(btn);
    }

    // private Widget createHeader() {
        // DockPanel header = new DockPanel();
        // header.setWidth("100%");
        // header.setSpacing(10);

        // header.add(new HTML("wappy"), DockPanel.WEST);

        // HTML logout = new HTML("<a href='/accounts/logout/'>logout</a>");
        // header.add(logout, DockPanel.EAST);
        // header.setCellHorizontalAlignment(logout,
            // HasHorizontalAlignment.ALIGN_RIGHT);

        // return header;
    // }
}
