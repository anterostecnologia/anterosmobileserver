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

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.mobile.core.synchronism.model.ActionSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.mobileserver.util.UserMessages;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

public class ActionForm extends VerticalLayout implements ClickListener {
	private TextField fldId;
	private TextField fldName;
	private TextField fldDescription;
	private Form actionForm;
	private HorizontalLayout buttons;
	private Button btnOk;
	private Button btnCancel;
	private MobileServerApplication app;
	private ActionSynchronism actionSynchronism;
	private Synchronism objectOwner;

	public ActionForm(MobileServerApplication app, ActionSynchronism actionSynchronism, Synchronism objectOwner) {
		this.app = app;
		this.actionSynchronism = actionSynchronism;
		this.objectOwner = objectOwner;
		this.setMargin(true);
		createForm();
		createFields();
		createButtons();

		Label lblTitle = new Label("Ação");
		lblTitle.setStyleName("h2 color");
		addComponent(lblTitle);
		addComponent(actionForm);

		fldName.focus();

		loadData();
	}

	private void createButtons() {
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setWidth("715px");

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

		actionForm.getFooter().addComponent(buttons);
	}

	private void createForm() {
		actionForm = new Form();
		actionForm.setWriteThrough(false);
		actionForm.setInvalidCommitted(false);
		actionForm.getFooter().setMargin(false, false, true, true);
	}

	private void createFields() {
		fldId = new TextField();
		fldId.setCaption("Id");
		fldId.setWidth("100px");
		fldId.setStyleName("small");

		fldName = new TextField();
		fldName.setCaption("Nome da Ação");
		fldName.setWidth("400px");
		fldName.setRequired(true);
		fldName.setRequiredError("Informe o nome da ação.");
		fldName.setStyleName("small");

		fldDescription = new TextField();
		fldDescription.setCaption("Descrição da Ação");
		fldDescription.setWidth("600px");
		fldDescription.setRequired(true);
		fldDescription.setRequiredError("Informe a descrição da ação.");
		fldDescription.setStyleName("small");

		actionForm.addField("fldId", fldId);
		actionForm.addField("fldName", fldName);
		actionForm.addField("fldDescription", fldDescription);

	}


	public void buttonClick(ClickEvent event) {
		final ActionForm comp = this;
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

			}
		} else if (event.getButton() == btnOk) {
			if (this.getParent() instanceof TabSheet) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				final Form f = actionForm;
				userMessages.confirm("Gravar os dados?", new ClickListener() {

					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							try {
								f.setValidationVisible(true);
								f.validate();
								saveData();
								if (MobileServerData.save(app, actionSynchronism)) {
									changeDataTreeItem();
									app.removeTab(comp);
								}
							} catch (Exception e) {
								getWindow()
										.showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);

							}
						}
					}
				});
			}
		}
	}

	private void saveData() {
		if (!StringUtils.isEmpty(fldId.getValue() + "")) {
			actionSynchronism.setId(new Long(fldId.getValue() + ""));
		}
		actionSynchronism.setName(fldName.getValue() + "");
		actionSynchronism.setDescription(fldDescription.getValue() + "");
		actionSynchronism.setObjectOwner(objectOwner);
	}

	protected void changeDataTreeItem() {
		Item item = app.getTree().getItem(actionSynchronism.getId());
		if (item == null) {
			item = app.getTree().addItem(actionSynchronism.getId());
			((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(actionSynchronism.getId(),
					actionSynchronism.getObjectOwner().getId());
		}
		item.getItemProperty(MobileServerData.PROPERTY_NAME).setValue(
				actionSynchronism.getName() + " " + actionSynchronism.getId());
		item.getItemProperty(MobileServerData.PROPERTY_ICON).setValue(MobileServerData.ACTION_IMG);
		item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(actionSynchronism);
		((HierarchicalContainer) app.getTree().getContainerDataSource()).setChildrenAllowed(actionSynchronism.getId(),
				true);
	}

	private void loadData() {
		if (actionSynchronism.getId() != null)
			fldId.setValue(actionSynchronism.getId());
		if (actionSynchronism.getDescription() != null)
			fldDescription.setValue(actionSynchronism.getDescription());
		if (actionSynchronism.getName() != null)
			fldName.setValue(actionSynchronism.getName());
	}

}
