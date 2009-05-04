package wappy.client.mail;

import java.util.ArrayList; 
import java.util.List;
import java.util.Date;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;   
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.grid.Grid;  
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.google.gwt.i18n.client.DateTimeFormat;  

public class MessageList extends ContentPanel {
    private MessageView messageView;

    public MessageList(MessageView messageView) {
        this.messageView = messageView;

        String url = "/mail/messages/";
        ScriptTagProxy<PagingLoadResult<ModelData>> proxy =
            new ScriptTagProxy<PagingLoadResult<ModelData>>(url);

        ModelType type = new ModelType();
        type.setRoot("results");
        type.setTotalName("total");
        type.addField("subject", "subject");
        type.addField("sender", "sender");

        DataField dateField = new DataField("date", "date");
        dateField.setType(Date.class);
        dateField.setFormat("timestamp");
        type.addField(dateField);

        JsonLoadResultReader<PagingLoadResult<ModelData>> reader = new JsonLoadResultReader<PagingLoadResult<ModelData>>(
            type) {
          @Override
          protected ListLoadResult<ModelData> newLoadResult(Object loadConfig,
              List<ModelData> models) {
            PagingLoadConfig pagingConfig = (PagingLoadConfig) loadConfig;
            PagingLoadResult<ModelData> result = new BasePagingLoadResult<ModelData>(models,
                pagingConfig.getOffset(), pagingConfig.getLimit());
            return result;
          }
        };

        PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
            proxy, reader);

        loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
          public void handleEvent(LoadEvent be) {
            be.<ModelData> getConfig().set("start", be.<ModelData> getConfig().get("offset"));
          }
        });
    
        loader.setRemoteSort(true);

        loader.load(0, 30);

        ListStore<ModelData> store = new ListStore<ModelData>(loader);

        final PagingToolBar toolBar = new PagingToolBar(30);
        toolBar.bind(loader);

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(new ColumnConfig("subject", "Subject", 150));
        columns.add(new ColumnConfig("sender", "Sender", 150));
        ColumnConfig date = new ColumnConfig("date", "Date", 120);
        date.setDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm"));
        columns.add(date);

        ColumnModel cm = new ColumnModel(columns);
        Grid<ModelData> grid = new Grid<ModelData>(store, cm);
        grid.setLoadMask(true);
        grid.setBorders(true);
        grid.setAutoExpandColumn("subject");
        grid.setSize("100%", "100%");

        setFrame(true);
        setCollapsible(false);
        setAnimCollapse(false);
        setButtonAlign(HorizontalAlignment.CENTER);
        setIconStyle("icon-table");
        setHeading("Messages");
        setLayout(new FitLayout());
        add(grid);
        setSize("100%", "100%");
        setBottomComponent(toolBar);
    }

    public void display(String folderPath) {
    }
}
