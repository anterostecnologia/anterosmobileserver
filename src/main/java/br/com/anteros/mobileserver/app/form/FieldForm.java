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

import java.util.Iterator;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.mobile.core.synchronism.model.FieldSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.mobileserver.util.FieldTypes;
import br.com.anteros.mobileserver.util.UserMessages;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class FieldForm extends VerticalLayout implements ClickListener {

	private TextField fldId;
	private TextField fldName;
	private TextField fldDescription;
	private TextField fldSQLFieldName;
	private ComboBox fldFieldType;
	private HorizontalLayout buttons;
	private Button btnOk;
	private Button btnCancel;
	private Form fieldForm;
	private FieldSynchronism fieldSynchronism;
	private MobileServerApplication app;
	private final Window win;
	private String lastAction = UserMessages.USER_CONFIRM_CANCEL;
	private Synchronism objectOwner;
	private boolean isFields;

	public FieldForm(MobileServerApplication app, FieldSynchronism fieldSynchronism, Window win,
			Synchronism objectOwner, boolean isFields) {

		this.app = app;
		this.fieldSynchronism = fieldSynchronism;
		this.objectOwner = objectOwner;
		this.win = win;
		this.isFields = isFields;
		this.setMargin(true);
		createForm();
		createFields();
		createButtons();

		Label lblTitle = new Label("Campo");
		lblTitle.setStyleName("h2 color");
		addComponent(lblTitle);
		addComponent(fieldForm);
		fldName.focus();

		loadData();
	}

	private void createButtons() {
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setWidth("640px");

		btnOk = new Button("Ok", this);
		btnOk.addStyleName("default");
		btnOk.setIcon(new ThemeResource("icons/16/ok.png"));
		buttons.addComponent(btnOk);
		buttons.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);
		buttons.setExpandRatio(btnOk, 1);

		btnCancel = new Button("Cancela", this);
		btnCancel.setIcon(new ThemeResource("icons/16/cancel.png"));
		buttons.addComponent(btnCancel);
		buttons.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		fieldForm.getFooter().addComponent(buttons);
	}

	private void createFields() {
		fldId = new TextField();
		fldId.setCaption("Id");
		fldId.setWidth("100px");
		fldId.setStyleName("small");

		fldName = new TextField();
		fldName.setCaption("Nome do campo");
		fldName.setWidth("200px");
		fldName.setRequired(true);
		fldName.setRequiredError("Informe o nome da campo.");
		fldName.setStyleName("small");

		fldDescription = new TextField();
		fldDescription.setCaption("Descrição do campo");
		fldDescription.setWidth("500px");
		fldDescription.setRequired(true);
		fldDescription.setRequiredError("Informe a descrição do campo.");
		fldDescription.setStyleName("small");

		fldSQLFieldName = new TextField();
		fldSQLFieldName.setCaption("Nome do campo SQL");
		fldSQLFieldName.setWidth("200px");
		fldSQLFieldName.setRequired(true);
		fldSQLFieldName.setRequiredError("Informe o nome do campo SQL.");
		fldSQLFieldName.setStyleName("small");

		fldFieldType = new ComboBox();
		fldFieldType.setCaption("Tipo de Campo");
		fldFieldType.setWidth("200px");
		fldFieldType.setRequired(true);
		fldFieldType.setRequiredError("Informe o tipo do campo.");
		fldFieldType.setStyleName("small");

		Iterator<String> it = FieldTypes.getFieldTypes().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = (String) FieldTypes.getFieldTypes().get(key);
			fldFieldType.addItem(value);
		}

		fieldForm.addField("fldId", fldId);
		fieldForm.addField("fldName", fldName);
		fieldForm.addField("fldDescription", fldDescription);
		fieldForm.addField("fldSQLFieldName", fldSQLFieldName);
		fieldForm.addField("fldFieldType", fldFieldType);
	}

	private void createForm() {
		fieldForm = new Form();
		fieldForm.setWriteThrough(false);
		fieldForm.setInvalidCommitted(false);
		fieldForm.getFooter().setMargin(false, false, true, true);
	}


	public void buttonClick(ClickEvent event) {
		final FieldForm comp = this;
		if (event.getButton() == btnCancel) {
			if (this.getParent() instanceof TabSheet) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Cancelar a Edição da Aplicação?", new ClickListener() {

					public void buttonClick(ClickEvent event) {
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							app.removeTab(comp);
						}
						userMessages.removeConfirm();
					}
				});

			} else if (win != null) {
				lastAction = UserMessages.USER_CONFIRM_CANCEL;
				((Window) win.getParent()).removeWindow(win);
			}
		} else if (event.getButton() == btnOk) {
			if (this.getParent() instanceof TabSheet) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				final Form f = fieldForm;
				userMessages.confirm("Gravar os dados?", new ClickListener() {

					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							try {
								f.setValidationVisible(true);
								f.validate();
								saveData();
								if (MobileServerData.save(app, fieldSynchronism)) {
									changeDataTreeItem();
									app.removeTab(comp);
									app.getTree().select(fieldSynchronism.getId());
								}
							} catch (Exception e) {
								e.printStackTrace();
								getWindow()
										.showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);

							}
						}
					}
				});

			} else {
				try {
					fieldForm.setValidationVisible(true);
					fieldForm.validate();
					saveData();
					lastAction = UserMessages.USER_CONFIRM_OK;
					((Window) win.getParent()).removeWindow(win);
				} catch (Exception e) {
					getWindow().showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);

				}
			}
		}
	}

	private void saveData() {
		if (!StringUtils.isEmpty(fldId.getValue() + "")) {
			fieldSynchronism.setId(new Long(fldId.getValue() + ""));
		}
		fieldSynchronism.setName(fldName.getValue() + "");
		fieldSynchronism.setDescription(fldDescription.getValue() + "");
		fieldSynchronism.setObjectOwner(objectOwner);
		fieldSynchronism.setFieldType(new Long(FieldTypes.getFieldValueByName(fldFieldType.getValue() + "")));
		fieldSynchronism.setSqlFieldName(fldSQLFieldName.getValue() + "");
	}

	protected void changeDataTreeItem() {
		Item item = app.getTree().getItem(fieldSynchronism.getId());
		if (item == null) {
			item = app.getTree().addItem(fieldSynchronism.getId());
			if (isFields) {
				((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(fieldSynchronism.getId(),
						fieldSynchronism.getObjectOwner().getId() + MobileServerData.ID_TABLE_FIELDS);
			} else {
				((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(fieldSynchronism.getId(),
						fieldSynchronism.getObjectOwner().getId());
			}
		}
		item.getItemProperty(MobileServerData.PROPERTY_NAME).setValue(
				fieldSynchronism.getName() + " " + fieldSynchronism.getId());
		item.getItemProperty(MobileServerData.PROPERTY_ICON).setValue(MobileServerData.FIELD_IMG);
		item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(fieldSynchronism);
		((HierarchicalContainer) app.getTree().getContainerDataSource()).setChildrenAllowed(fieldSynchronism.getId(),
				false);
	}

	private void loadData() {
		if (fieldSynchronism.getId() != null)
			fldId.setValue(fieldSynchronism.getId());
		if (fieldSynchronism.getDescription() != null)
			fldDescription.setValue(fieldSynchronism.getDescription());

		if (fieldSynchronism.getName() != null)
			fldName.setValue(fieldSynchronism.getName());
		if (fieldSynchronism.getFieldType() != null)
			fldFieldType.setValue(FieldTypes.getFieldNameByValue(fieldSynchronism.getFieldType().intValue() + ""));
		if (fieldSynchronism.getSqlFieldName() != null)
			fldSQLFieldName.setValue(fieldSynchronism.getSqlFieldName());
	}

	public String getLastAction() {
		return lastAction;
	}
}
