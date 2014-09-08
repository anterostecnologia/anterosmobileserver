/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package br.com.anteros.mobileserver.app.form;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.anteros.mobile.core.protocol.MobileResponse;
import br.com.anteros.mobile.core.synchronism.model.ActionSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ProcedureSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobile.core.synchronism.model.TableSynchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.mobileserver.util.FieldTypes;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ExecuteForm extends VerticalLayout implements ValueChangeListener {

	private ActionSynchronism actionSynchronism;
	private Panel panelForm;
	private Form executeForm;
	private TabSheet pageControl;
	private Panel textPanel;
	private Button btnClose;
	private Button btnExecute;
	private MobileServerApplication app;
	private final ExecuteForm comp = this;
	private List<Component> fields = new ArrayList<Component>();
	private CheckBox executeCommit;

	public ExecuteForm(MobileServerApplication app, ActionSynchronism actionSynchronism) {
		this.actionSynchronism = actionSynchronism;
		this.app = app;
		setSizeUndefined();
		setWidth("100%");
		setMargin(true);
		setImmediate(true);
		createForm();
	}

	private void createForm() {
		if (actionSynchronism.getItems() != null) {

			Label lblTitle = new Label("Parâmetros de execução Ação " + actionSynchronism.getName() + " ("
					+ actionSynchronism.getId() + ")");
			lblTitle.setStyleName("h2 color");
			lblTitle.setImmediate(false);
			addComponent(lblTitle);
			setComponentAlignment(lblTitle, Alignment.TOP_LEFT);

			Synchronism synchronism = actionSynchronism.getItems().iterator().next();
			executeForm = new Form();
			fields.clear();
			ParameterSynchronism[] parameters = null;
			if (synchronism instanceof TableSynchronism)
				parameters = ((TableSynchronism) synchronism).getParameters();
			if (synchronism instanceof ProcedureSynchronism)
				parameters = ((ProcedureSynchronism) synchronism).getParameters();
			for (ParameterSynchronism param : parameters) {
				if (param.getParameterType().intValue() == ParameterSynchronism.INPUT || param.getParameterType().intValue() == ParameterSynchronism.SUBSTITUITION) {
					String value = FieldTypes.getFieldTypes().get(param.getParameterDataType().intValue() + "");
					if (value != null) {
						if (FieldTypes.UNKNOW.equalsIgnoreCase(value)) {
							TextField field = new TextField();
							field.setCaption(param.getName());
							field.setWidth("400px");
							executeForm.addField(param.getName(), field);
							fields.add(field);
						} else if (FieldTypes.INTEGER.equalsIgnoreCase(value)) {
							TextField field = new TextField();
							field.setCaption(param.getName());
							field.setWidth("150px");
							field.setRequired(true);
							field.setRequiredError("Informe o valor para o campo " + param.getName());
							executeForm.addField(param.getName(), field);
							fields.add(field);
						} else if (FieldTypes.VARCHAR.equalsIgnoreCase(value)) {
							TextField field = new TextField();
							field.setCaption(param.getName());
							field.setWidth("400px");
							field.setRequired(true);
							field.setRequiredError("Informe o valor para o campo " + param.getName());
							executeForm.addField(param.getName(), field);
							fields.add(field);
						} else if (FieldTypes.FLOAT.equalsIgnoreCase(value)) {
							TextField field = new TextField();
							field.setCaption(param.getName());
							field.setWidth("150px");
							field.setRequired(true);
							field.setRequiredError("Informe o valor para o campo " + param.getName());
							executeForm.addField(param.getName(), field);
							fields.add(field);
						} else if (FieldTypes.NUMERIC.equalsIgnoreCase(value)) {
							TextField field = new TextField();
							field.setCaption(param.getName());
							field.setWidth("150px");
							field.setRequired(true);
							field.setRequiredError("Informe o valor para o campo " + param.getName());
							executeForm.addField(param.getName(), field);
							fields.add(field);
						} else if (FieldTypes.DATE.equalsIgnoreCase(value)) {
							PopupDateField field = new PopupDateField();
							field.setCaption(param.getName());
							field.setWidth("150px");
							field.setRequired(true);
							field.setRequiredError("Informe o valor para o campo " + param.getName());
							field.setResolution(PopupDateField.RESOLUTION_DAY);
							executeForm.addField(param.getName(), field);
							fields.add(field);
						} else if (FieldTypes.TIME.equalsIgnoreCase(value)) {
							TextField field = new TextField();
							field.setCaption(param.getName());
							field.setWidth("150px");
							field.setRequired(true);
							field.setRequiredError("Informe o valor para o campo " + param.getName());
							executeForm.addField(param.getName(), field);
							fields.add(field);
						} else if (FieldTypes.TIMESTAMP.equalsIgnoreCase(value)) {
							TextField field = new TextField();
							field.setCaption(param.getName());
							field.setWidth("150px");
							field.setRequired(true);
							field.setRequiredError("Informe o valor para o campo " + param.getName());
							executeForm.addField(param.getName(), field);
							fields.add(field);
						}
					}
				}
			}

			panelForm  = new Panel();
			panelForm.setHeight("100%");
			panelForm.setWidth("100%");
			panelForm.setScrollable(true);
			addComponent(panelForm);
			executeForm.setImmediate(true);
			executeForm.setWidth("100%");
			panelForm.addComponent(executeForm);

			executeCommit = new CheckBox("Executar COMMIT no final do processo?");
			addComponent(executeCommit);

			HorizontalLayout buttons = new HorizontalLayout();
			buttons.setImmediate(false);
			buttons.setWidth("600px");
			buttons.setHeight("-1px");
			buttons.setMargin(false);
			buttons.setSpacing(true);
			addComponent(buttons);

			btnExecute = new Button();
			btnExecute.setCaption("Executar");
			btnExecute.setIcon(new ThemeResource("icons/16/run.png"));
			btnExecute.addListener(clickListener);
			buttons.addComponent(btnExecute);
			buttons.setComponentAlignment(btnExecute, Alignment.MIDDLE_RIGHT);
			buttons.setExpandRatio(btnExecute, 1);

			btnClose = new Button();
			btnClose.setCaption("Fechar");
			btnClose.setIcon(new ThemeResource("icons/16/doorOut.png"));
			btnClose.addListener(clickListener);
			buttons.addComponent(btnClose);

			buttons.setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);
			buttons.setMargin(true, false, true, false);
			addComponent(buttons);

			pageControl = new TabSheet();
			pageControl.setImmediate(true);
			pageControl.setWidth("100.0%");
			pageControl.setHeight("100.0%");

			textPanel = new Panel();
			textPanel.setImmediate(true);
			textPanel.setWidth("100%");
			textPanel.setHeight("100%");
			pageControl.addTab(textPanel, "Resultado", null);
			addComponent(pageControl);
			setExpandRatio(pageControl, 1.0f);
		}
	}

	public void valueChange(ValueChangeEvent event) {
		DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
		Object value = event.getProperty().getValue();
		if (value == null || !(value instanceof Date)) {
			getWindow().showNotification("Data Inválida");
		} else {
			String dateOut = dateFormatter.format(value);
			getWindow().showNotification("Starting date: " + dateOut);
		}

	}

	private ClickListener clickListener = new ClickListener() {

		public void buttonClick(ClickEvent event) {

			if (event.getButton() == btnClose) {
				app.removeTab(comp);
			} else if (event.getButton() == btnExecute) {
				try {
					textPanel.removeAllComponents();
					textPanel.requestRepaint();
					executeForm.setValidationVisible(true);
					executeForm.validate();
					btnClose.setEnabled(false);
					btnExecute.setEnabled(false);
					MobileResponse mobileResponse = MobileServerData.executeActionSynchronism(app, actionSynchronism,
							getParametersByFields(), (Boolean) executeCommit.getValue());
					Label labelResponse = new Label(mobileResponse.getShowDetailsHtml(), Label.CONTENT_XHTML);
					textPanel.addComponent(labelResponse);
				} catch (Exception e) {
					getWindow().showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
				} finally {
					btnClose.setEnabled(true);
					btnExecute.setEnabled(true);
				}

			}

		}
	};

	private String[] getParametersByFields() {
		List<String> result = new ArrayList<String>();
		for (Component comp : fields) {
			if (comp instanceof TextField) {
				result.add(((TextField) comp).getValue() + "");
			} else if (comp instanceof PopupDateField) {
				result.add(new SimpleDateFormat("yyyy-MM-dd").format(((PopupDateField) comp).getValue()));
			}
		}
		return result.toArray(new String[] {});
	}

}
