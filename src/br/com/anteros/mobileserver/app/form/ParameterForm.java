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

import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobile.core.synchronism.model.TableSynchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.mobileserver.util.FieldTypes;
import br.com.anteros.mobileserver.util.UserMessages;
import br.com.anteros.persistence.util.StringUtils;

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

public class ParameterForm extends VerticalLayout implements ClickListener {

	private TextField fldId;
	private TextField fldName;
	private TextField fldDescription;
	private ComboBox fldParameterDataType;
	private ComboBox fldParameterType;
	private Form parameterForm;
	private Button btnOk;
	private HorizontalLayout buttons;
	private Button btnCancel;
	private ParameterSynchronism parameterSynchronism;
	private MobileServerApplication app;
	private final Window win;
	private String lastAction = UserMessages.USER_CONFIRM_CANCEL;
	private Synchronism objectOwner;
	private boolean isParameters;

	public ParameterForm(MobileServerApplication app, ParameterSynchronism parameterSynchronism, Window win,
			Synchronism objectOwner, boolean isParameters) {
		this.setMargin(true);
		this.app = app;
		this.parameterSynchronism = parameterSynchronism;
		this.win = win;
		this.isParameters = isParameters;
		this.objectOwner = objectOwner;

		createForm();
		createFields();
		createButtons();

		Label lblTitle = new Label("Parâmetro");
		lblTitle.setStyleName("h2 color");
		addComponent(lblTitle);
		addComponent(parameterForm);
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

		parameterForm.getFooter().addComponent(buttons);
	}

	private void createFields() {
		fldId = new TextField();
		fldId.setCaption("Id");
		fldId.setWidth("100px");
		fldId.setStyleName("small");

		fldName = new TextField();
		fldName.setCaption("Nome do parâmetro");
		fldName.setWidth("200px");
		fldName.setRequired(true);
		fldName.setRequiredError("Informe o nome da aplicação.");
		fldName.setStyleName("small");

		fldDescription = new TextField();
		fldDescription.setCaption("Descrição do parâmetro");
		fldDescription.setWidth("500px");
		fldDescription.setRequired(true);
		fldDescription.setRequiredError("Informe a descrição do parâmetro.");
		fldDescription.setStyleName("small");

		fldParameterDataType = new ComboBox();
		fldParameterDataType.setCaption("Tipo de dado do parâmetro");
		fldParameterDataType.setWidth("200px");
		fldParameterDataType.setRequired(true);
		fldParameterDataType.setRequiredError("Informe o tipo de dado do parâmetro.");
		fldParameterDataType.setStyleName("small");
		Iterator<String> it = FieldTypes.getFieldTypes().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = (String) FieldTypes.getFieldTypes().get(key);
			fldParameterDataType.addItem(value);
		}

		fldParameterType = new ComboBox();
		fldParameterType.setCaption("Tipo do parâmetro");
		fldParameterType.setWidth("200px");
		fldParameterType.setRequired(true);
		fldParameterType.setRequiredError("Informe o tipo do parâmetro.");
		fldParameterType.addItem("INPUT");
		fldParameterType.addItem("OUTPUT");
		fldParameterType.setStyleName("small");

		parameterForm.addField("fldId", fldId);
		parameterForm.addField("fldName", fldName);
		parameterForm.addField("fldDescription", fldDescription);
		parameterForm.addField("fldParameterDataType", fldParameterDataType);
		parameterForm.addField("fldParameterType", fldParameterType);
	}

	private void createForm() {
		parameterForm = new Form();
		parameterForm.setWriteThrough(false);
		parameterForm.setInvalidCommitted(false);
		parameterForm.getFooter().setMargin(false, false, true, true);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		final ParameterForm comp = this;
		final Form f = parameterForm;

		if (event.getButton() == btnCancel) {
			if (this.getParent() instanceof TabSheet) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Cancelar a Edição da Aplicação?", new ClickListener() {
					@Override
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
				userMessages.confirm("Gravar os dados?", new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							try {
								f.setValidationVisible(true);
								f.validate();
								saveData();
								if (MobileServerData.save(app, parameterSynchronism)) {
									changeDataTreeItem();
									app.removeTab(comp);
									app.getTree().select(parameterSynchronism.getId());
								}
							} catch (Exception e) {
								getWindow()
										.showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);

							}
						}
					}
				});
			} else {
				try {
					parameterForm.setValidationVisible(true);
					parameterForm.validate();
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
			parameterSynchronism.setId(new Long(fldId.getValue() + ""));
		}
		parameterSynchronism.setName(fldName.getValue() + "");
		parameterSynchronism.setDescription(fldDescription.getValue() + "");
		parameterSynchronism.setObjectOwner(objectOwner);
		parameterSynchronism.setParameterDataType(new Long(FieldTypes.getFieldValueByName(fldParameterDataType
				.getValue() + "")));
		parameterSynchronism.setParameterType(fldParameterType.getValue() == "INPUT" ? new Long(0) : new Long(1));
	}

	protected void changeDataTreeItem() {
		Item item = app.getTree().getItem(parameterSynchronism.getId());
		if (item == null) {
			item = app.getTree().addItem(parameterSynchronism.getId());

			if (isParameters) {
				if (parameterSynchronism.getObjectOwner() instanceof TableSynchronism) {
					((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(
							parameterSynchronism.getId(), parameterSynchronism.getObjectOwner().getId()
									+ MobileServerData.ID_TABLE_PARAMETERS);
				} else {
					((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(
							parameterSynchronism.getId(), parameterSynchronism.getObjectOwner().getId()
									+ MobileServerData.ID_PROCEDURE_PARAMETERS);
				}
			} else {
				((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(
						parameterSynchronism.getId(), parameterSynchronism.getObjectOwner().getId());
			}
		}
		item.getItemProperty(MobileServerData.PROPERTY_NAME).setValue(
				parameterSynchronism.getName() + " " + parameterSynchronism.getId());
		item.getItemProperty(MobileServerData.PROPERTY_ICON).setValue(MobileServerData.PARAMETER_IMG);
		item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(parameterSynchronism);
		((HierarchicalContainer) app.getTree().getContainerDataSource()).setChildrenAllowed(
				parameterSynchronism.getId(), false);
	}

	private void loadData() {
		if (parameterSynchronism.getId() != null)
			fldId.setValue(parameterSynchronism.getId());
		if (parameterSynchronism.getDescription() != null)
			fldDescription.setValue(parameterSynchronism.getDescription());
		if (parameterSynchronism.getName() != null)
			fldName.setValue(parameterSynchronism.getName());
		if (parameterSynchronism.getParameterDataType() != null) {
			fldParameterDataType.setValue(FieldTypes.getFieldNameByValue(parameterSynchronism.getParameterDataType()
					.intValue() + ""));
		}
		if (parameterSynchronism.getParameterType() != null)
			fldParameterType.setValue((parameterSynchronism.getParameterType().intValue() == 0 ? "INPUT" : "OUTPUT"));
	}

	public String getLastAction() {
		return lastAction;
	}

}
