package wappy.client;

import wappy.client.calendar.Calendar;
import wappy.client.groups.Groups;
import wappy.client.mail.MailClient;
import wappy.client.bookmarks.Bookmarks;
import wappy.client.desktop.Desktop;
import wappy.client.desktop.Shortcut;
import wappy.client.desktop.StartMenu;
import wappy.client.desktop.TaskBar;

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
    private Window groupsWindow = new Window();
    private Window bookmarksWindow = new Window();
    private HorizontalPanel toolBar = new HorizontalPanel();

    private Calendar calendar = new Calendar();
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
        calendarWindow.setPlain(true);  
        calendarWindow.setHeading("Calendar");  
        calendarWindow.setLayout(new FitLayout());  
        calendarWindow.setMinimizable(true);
        calendarWindow.setMaximizable(true);
        calendarWindow.add(calendar);

        groupsWindow.setSize(350, 550);
        groupsWindow.setPlain(true);  
        groupsWindow.setHeading("Groups");  
        groupsWindow.setLayout(new FitLayout());  
        groupsWindow.setMinimizable(true);
        groupsWindow.setMaximizable(true);
        groupsWindow.add(new Groups());

        bookmarksWindow.setSize(300, 500);
        bookmarksWindow.setPlain(true);  
        bookmarksWindow.setHeading("Bookmarks");  
        bookmarksWindow.setLayout(new FitLayout());  
        bookmarksWindow.setMinimizable(true);
        bookmarksWindow.setMaximizable(true);
        bookmarksWindow.add(new Bookmarks());

        initDesktopView();
        //initSimpleView();
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
      vp.add(new Groups());
      
      RootPanel.get().add(vp);
	}

	private void initDesktopView() {
		desktop = new Desktop();
		desktop.getDesktop().setStyleName("wappy-desktop-background");

		SelectionListener<MenuEvent> menuListener =
        new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                Window w = null;

                if (ce instanceof MenuEvent) {
                    MenuEvent me = (MenuEvent)ce;
                    w = me.getItem().getData("window");
                }
                else {
                    w = ce.getComponent().getData("window");
                }

                if (!desktop.getWindows().contains(w)) {
                    desktop.addWindow(w);
                }
                
                if (!w.isVisible()) {
                    w.show();
                }
                else {
                    w.toFront();
                }
			}
		};

		TaskBar taskBar = desktop.getTaskBar();

		StartMenu startMenu = taskBar.getStartMenu();
		startMenu.setHeading("wappy online applications");
		startMenu.setIconStyle("wappy-startmenu-user");

        final MenuItem mailMenuItem = new MenuItem("Mail");
		mailMenuItem.setData("window", mailWindow);
		mailMenuItem.setTitle("Open the Mail Client");
		mailMenuItem.setIconStyle("wappy-startmenu-mail");
		mailMenuItem.addSelectionListener(menuListener);
		startMenu.add(mailMenuItem);

        final MenuItem calMenuItem = new MenuItem("Calendar");
		calMenuItem.setData("window", calendarWindow);
		calMenuItem.setTitle("Open the Calendar");
		calMenuItem.setIconStyle("wappy-startmenu-calendar");
		calMenuItem.addSelectionListener(menuListener);
		startMenu.add(calMenuItem);

		final MenuItem groupsMenuItem = new MenuItem("Groups");
		groupsMenuItem.setData("window", groupsWindow);
		groupsMenuItem.setTitle("Open groups viewer");
		groupsMenuItem.setIconStyle("wappy-startmenu-groups");
		groupsMenuItem.addSelectionListener(menuListener);
		startMenu.add(groupsMenuItem);

		final MenuItem bookmarksMenuItem = new MenuItem("Bookmarks");
		bookmarksMenuItem.setData("window", bookmarksWindow);
		bookmarksMenuItem.setTitle("Open bookmark viewer");
		bookmarksMenuItem.setIconStyle("wappy-startmenu-bookmarks");
		bookmarksMenuItem.addSelectionListener(menuListener);
		startMenu.add(bookmarksMenuItem);

		MenuItem logout = new MenuItem("Logout",
        new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				logout();
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

    private void logout() {
        com.google.gwt.user.client.Window.Location.assign("/accounts/logout/");
    }
}
