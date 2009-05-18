package wappy.client.calculator;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.ui.FlexTable;

public class Calculator extends LayoutContainer{

//	private ContentPanel panel = new ContentPanel();
	private VerticalPanel numPanel = new VerticalPanel();
	private VerticalPanel opPanel = new VerticalPanel();
	private HorizontalPanel btnsPanel = new HorizontalPanel();
	private FlexTable table = new FlexTable();
	private VerticalPanel vp = new VerticalPanel();
	private final NumberField display = new NumberField();

	public Calculator() {
		ButtonBar numRow0 = new ButtonBar();
		ButtonBar numRow1 = new ButtonBar();
		ButtonBar numRow2 = new ButtonBar();
		ButtonBar numRow3 = new ButtonBar();
		
		Button btn1 = new Button("1");
		Button btn2 = new Button("2");
		Button btn3 = new Button("3");
		Button btn4 = new Button("4");
		Button btn5 = new Button("5");
		Button btn6 = new Button("6");
		Button btn7 = new Button("7");
		Button btn8 = new Button("8");
		Button btn9 = new Button("9");
		Button btn0 = new Button("0");
		
		Button addBtn = new Button("+");
		Button subBtn = new Button("-");
		Button multBtn = new Button("*");
		Button divBtn = new Button("/");
		Button resultBtn = new Button("=");
		
		numRow0.add(btn1);
		numRow0.add(btn2);
		numRow0.add(btn3);
		numRow1.add(btn4);
		numRow1.add(btn5);
		numRow1.add(btn6);
		numRow2.add(btn7);
		numRow2.add(btn8);
		numRow2.add(btn9);
		numRow3.add(btn0);
		numPanel.add(numRow0);
		numPanel.add(numRow1);
		numPanel.add(numRow2);
		numPanel.add(numRow3);
		
		opPanel.add(addBtn);
		opPanel.add(subBtn);
		opPanel.add(multBtn);
		opPanel.add(divBtn);
		opPanel.add(resultBtn);
		
		btnsPanel.add(numPanel);
		btnsPanel.add(opPanel);
		
//		panel.setHeading("Calculator");
//		panel.setSize(100, 160);
//		panel.setCollapsible(true);
//		panel.add(display);
//		panel.add(btnsPanel);
//		
		
		
		table.setWidget(0, 0, btn1);
		table.setWidget(0, 1, btn2);
		table.setWidget(0, 2, btn3);
		table.setWidget(0, 4, addBtn);
		table.setWidget(1, 0, btn4);
		table.setWidget(1, 1, btn5);
		table.setWidget(1, 2, btn6);
		table.setWidget(1, 4, subBtn);
		table.setWidget(2, 0, btn7);
		table.setWidget(2, 1, btn8);
		table.setWidget(2, 2, btn9);
		table.setWidget(2, 4, multBtn);
		table.setWidget(3, 0, btn0);
		table.setWidget(3, 3, resultBtn);
		table.setWidget(3, 4, divBtn);
		
		
		vp.add(display);
		vp.add(table);
		setLayout(new FlowLayout());
		setWidth(100);
//		add(panel);
		add(vp);
	}
}
