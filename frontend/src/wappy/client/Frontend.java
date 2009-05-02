package wappy.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.HTML;

import wappy.client.mail.MailClient;

public class Frontend implements EntryPoint {
    public void onModuleLoad() {
        VerticalPanel vp = new VerticalPanel();
        final TabPanel tp = new TabPanel();
        tp.add(new HTML("home"), "Home");
        tp.add(new MailClient(), "Mail");
        tp.add(new HTML("calendar program"), "Calendar");
        tp.selectTab(0);
        tp.setWidth("100%");

        final Widget header = this.createHeader();
        vp.add(header);
        vp.add(tp);

        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                   adjustSize(header, tp,
                    Window.getClientWidth(), Window.getClientHeight());
            }
        });

        RootPanel.get().add(vp);

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                adjustSize(header, tp,
                   Window.getClientWidth(), Window.getClientHeight());
            }
        });
    }

    private void adjustSize(Widget header, TabPanel tp,
                            int newWidth, int newHeight) {
        int width = newWidth - 24;
        int height = newHeight -
                     header.getOffsetHeight() -
                     tp.getTabBar().getOffsetHeight() - 24;
        tp.getDeckPanel().setWidth(width + "px");
        tp.getDeckPanel().setHeight(height + "px");
    }

    private Widget createHeader() {
        DockPanel header = new DockPanel();
        header.setWidth("100%");
        header.setSpacing(10);

        header.add(new HTML("wappy"), DockPanel.WEST);

        HTML logout = new HTML("<a href='/accounts/logout/'>logout</a>");
        header.add(logout, DockPanel.EAST);
        header.setCellHorizontalAlignment(logout,
            HasHorizontalAlignment.ALIGN_RIGHT);

        return header;
    }
}
