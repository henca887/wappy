package wappy.client;

import wappy.client.calendar.Calendar;
import wappy.client.groups.Groups;
import wappy.client.mail.MailClient;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.desktop.client.TaskBar;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class Frontend implements EntryPoint {
	private Window mailWindow = new Window();
    private Window calendarWindow = new Window();
    private HorizontalPanel toolBar = new HorizontalPanel();
    
    private Calendar calendar = new Calendar();
    private Groups groups = new Groups();
    private Desktop desktop;
    
    public void onModuleLoad() {
        mailWindow.setSize(800, 600);  
        mailWindow.setPlain(true);  
        mailWindow.setHeading("Mail");  
        mailWindow.setLayout(new FitLayout());
        mailWindow.setMinimizable(true);
        mailWindow.setMaximizable(true);
        mailWindow.add(new MailClient());

        calendarWindow.setSize(800, 600);  
//        if (calendar.isGridView()) { // could use setMonitorWindowResize
//        	calendarWindow.setResizable(false);
//        }
        calendarWindow.setPlain(true);  
        calendarWindow.setHeading("Calendar");  
        calendarWindow.setLayout(new FitLayout());  
        calendarWindow.setMinimizable(true);
        calendarWindow.setMaximizable(true);
//        calendarWindow.addWindowListener(new WindowListener() {
//        	@Override
//        	public void windowMinimize(WindowEvent we) {
//        		calendarWindow.hide();
//        		desktop.getTaskBar().addTaskButton(calendarWindow);
//        	
//        	}
//        	
//        	@Override
//        	public void windowRestore(WindowEvent we) {
//        		calendarWindow.show();
//        	}
//        });
        calendarWindow.add(calendar);

//        initDesktopView();
        initSimpleView();

    }

	private void initSimpleView() {
      Button mailButton = new Button("Mail");  
      mailButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
          @Override  
          public void componentSelected(ButtonEvent ce) {  
              openMail();
          }  
      });

      Button calendarButton = new Button("Calendar");
      calendarButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
          @Override  
          public void componentSelected(ButtonEvent ce) {  
          	openCalendar();
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
      
      VerticalPanel vp = new VerticalPanel();
      vp.add(toolBar);
      vp.add(groups);
      
      RootPanel.get().add(vp);
	}
/** 
 * initDesktopView - Under development
 */
	private void initDesktopView() {
		desktop = new Desktop();
		desktop.getDesktop().setStyleName("wappy-desktop-background");
		final Shortcut mailShortcut = new Shortcut();
		final Shortcut calShortcut = new Shortcut();
		final MenuItem mailMenuItem = new MenuItem("Mail");
		final MenuItem calMenuItem = new MenuItem("Calendar");
		
		SelectionListener<ComponentEvent> shortcutListener = new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				Window w = null;
				if (ce.getSource() == mailShortcut) {
					w = ce.getComponent().getData("mailWindow");
				}
				else if (ce.getSource() == calShortcut) {
					w = ce.getComponent().getData("calendarWindow");
				}
				
				if (!desktop.getWindows().contains(w)) {
					desktop.addWindow(w);
				}
				
				if (w != null && !w.isVisible()) {
					w.show();
				}
				else {
					w.toFront();
				}
			}
		};
		
		SelectionListener<MenuEvent> menuListener = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Window w = null;
				if (ce.getSource() == mailMenuItem) {
					w = ce.getComponent().getData("mailWindow");
				}
				else if (ce.getSource() == calMenuItem) {
					w = ce.getComponent().getData("calendarWindow");
				}
				
				if (!desktop.getWindows().contains(w)) {
					desktop.addWindow(w);
				}
				
				if (w != null && !w.isVisible()) {
					w.show();
				}
				else {
					w.toFront();
				}
			}
		};


		mailShortcut.setText("Mail Client");
		mailShortcut.setId("wappy-mail-shortcut");
		mailShortcut.setData("mailWindow", mailWindow);
		mailShortcut.addSelectionListener(shortcutListener);
		desktop.addShortcut(mailShortcut);
		
		calShortcut.setText("Calendar");
		calShortcut.setId("wappy-calendar-shortcut");
		calShortcut.setData("calendarWindow", calendarWindow);
		calShortcut.addSelectionListener(shortcutListener);
		desktop.addShortcut(calShortcut);
		
		TaskBar taskBar = desktop.getTaskBar();

		StartMenu startMenu = taskBar.getStartMenu();
		startMenu.setHeading("Wappy developer");
		startMenu.setIconStyle("wappy-startmenu-user");
		
		mailMenuItem.setData("window", mailWindow);
		mailMenuItem.setTitle("Open the Mail Client");
		mailMenuItem.setIconStyle("wappy-startmenu-mail");
		mailMenuItem.addSelectionListener(menuListener);
		startMenu.add(mailMenuItem);
		
		calMenuItem.setData("window", calendarWindow);
		calMenuItem.setTitle("Open the Calendar");
		calMenuItem.setIconStyle("wappy-startmenu-calendar");
		calMenuItem.addSelectionListener(menuListener);
		startMenu.add(calMenuItem);
		
		MenuItem logout = new MenuItem("Logout", new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				com.google.gwt.user.client.Window.Location.assign(
			    "/accounts/logout/");
			}
		});
		logout.setStyleName("wappy-startmenu-logout");
		startMenu.addTool(logout);
		

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
