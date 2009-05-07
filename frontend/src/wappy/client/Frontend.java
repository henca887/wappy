package wappy.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import wappy.client.mail.MailClient;
import wappy.client.calendar.Calendar;

public class Frontend implements EntryPoint {
    Window mailWindow = new Window();
    Window calendarWindow = new Window();
    HorizontalPanel toolBar = new HorizontalPanel();

    public void onModuleLoad() {
        mailWindow.setSize(800, 600);  
        mailWindow.setPlain(true);  
        mailWindow.setHeading("Mail");  
        mailWindow.setLayout(new FitLayout());  
        mailWindow.add(new MailClient());

        calendarWindow.setSize(800, 600);  
        calendarWindow.setPlain(true);  
        calendarWindow.setHeading("Calendar");  
        calendarWindow.setLayout(new FitLayout());  
        calendarWindow.add(new Calendar());

        Button mailButton = new Button("Mail");  
        mailButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
            @Override  
            public void componentSelected(ButtonEvent ce) {  
                mailWindow.show();
                mailWindow.toFront();
            }  
        });

        Button calendarButton = new Button("Calendar");
        calendarButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
            @Override  
            public void componentSelected(ButtonEvent ce) {  
                calendarWindow.show();
                calendarWindow.toFront();
            }  
        });
        
        Button logoutButton = new Button("Logout");  
        logoutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
            @Override  
            public void componentSelected(ButtonEvent ce) {  
                com.google.gwt.user.client.Window.Location.assign(
                    "/accounts/logout/");
            }  
        });  

        toolBar.add(mailButton);
        toolBar.add(calendarButton);
        toolBar.add(logoutButton);

        RootPanel.get().add(toolBar);
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
