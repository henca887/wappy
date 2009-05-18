package wappy.client;

import wappy.client.calendar.Calendar;
import wappy.client.mail.MailClient;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class Frontend implements EntryPoint {
    Window mailWindow = new Window();
    Window calendarWindow = new Window();
    HorizontalPanel toolBar = new HorizontalPanel();
    
    private Calendar calendar = new Calendar();
    private Desktop desktop = new Desktop();
    
    public void onModuleLoad() {
        mailWindow.setSize(800, 600);  
        mailWindow.setPlain(true);  
        mailWindow.setHeading("Mail");  
        mailWindow.setLayout(new FitLayout());  
        mailWindow.add(new MailClient());

        calendarWindow.setSize(800, 600);  
        if (calendar.isGridView()) { // could use setMonitorWindowResize
        	calendarWindow.setResizable(false);
        }
        calendarWindow.setPlain(true);  
        calendarWindow.setHeading("Calendar");  
        calendarWindow.setLayout(new FitLayout());  
        calendarWindow.setMinimizable(true);
//        calendarWindow.addWindowListener(new WindowListener(WindowEvent) {
//        	
//        });
        calendarWindow.add(calendar);
        
//        Button mailButton = new Button("Mail");  
//        mailButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//            @Override  
//            public void componentSelected(ButtonEvent ce) {  
//                openMail();
//            }  
//        });
//
//        Button calendarButton = new Button("Calendar");
//        calendarButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//            @Override  
//            public void componentSelected(ButtonEvent ce) {  
//            	openCalendar();
//            }  
//        });
//        
//        Button logoutButton = new Button("Logout");  
//        logoutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//            @Override  
//            public void componentSelected(ButtonEvent ce) {  
//                com.google.gwt.user.client.Window.Location.assign(
//                    "/accounts/logout/");
//            }  
//        });  
//
//        toolBar.add(mailButton);
//        toolBar.add(calendarButton);
//        toolBar.add(logoutButton);
        initDesktop();
//        RootPanel.get().add(toolBar);
    }

	private void initDesktop() {
		MenuItem openMail = new MenuItem("Mail",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						openMail();
					}
			});
		MenuItem openCalendar = new MenuItem("Calendar",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						openCalendar();
					}
				});
		MenuItem logout = new MenuItem("Logout",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						com.google.gwt.user.client.Window.Location.assign(
	                    "/accounts/logout/");
					}
				});
		openMail.setTitle("Open the Mail Client");
		openCalendar.setTitle("Open the Calendar");
				
		Shortcut mailShortcut = new Shortcut();
		mailShortcut.setText("Mail Client");
		mailShortcut.setId("wappy-mail-shortcut");
		mailShortcut.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				openMail();
			}
		});
		
		Shortcut calShortcut = new Shortcut();
		calShortcut.setText("Calendar");
		calShortcut.setId("wappy-calendar-shortcut");
		calShortcut.addSelectionListener(new SelectionListener<ComponentEvent>() {
			public void componentSelected(ComponentEvent ce) {
				openCalendar();
			}
		});
		
		desktop.getDesktop().setStyleName("wappy-desktop-background");
		desktop.addShortcut(mailShortcut);
		desktop.addShortcut(calShortcut);
				
        desktop.getStartMenu().add(openMail);
        desktop.getStartMenu().add(openCalendar);
        desktop.getStartMenu().add(logout);
	}
	
	private void openMail() {
		mailWindow.show();
        mailWindow.toFront();
	}
	
	private void openCalendar() {
		calendarWindow.show();
        calendarWindow.toFront();
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
