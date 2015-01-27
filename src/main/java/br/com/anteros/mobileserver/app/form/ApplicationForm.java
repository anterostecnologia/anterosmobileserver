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
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerContext;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.mobileserver.controller.PoolDatasource;
import br.com.anteros.mobileserver.util.UserMessages;
import br.com.anteros.persistence.sql.dialect.FirebirdDialect;
import br.com.anteros.persistence.sql.dialect.H2Dialect;
import br.com.anteros.persistence.sql.dialect.MySQLDialect;
import br.com.anteros.persistence.sql.dialect.OracleDialect;
import br.com.anteros.persistence.sql.dialect.PostgreSqlDialect;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ApplicationForm extends VerticalLayout implements ClickListener {

	private TextField fldId;
	private TextField fldName;
	private TextField fldDescription;
	private Button btnOk;
	private HorizontalLayout buttons;
	private Button btnCancel;
	private Form applicationForm;
	private ApplicationSynchronism applicationSynchronism;
	private MobileServerApplication app;
	private ComboBox cbDialect;
	private TextField fldURL;
	private TextField fldUser;
	private PasswordField fldPassword;
	private TextField fldInitPoolSize;
	private TextField fldMinPoolSize;
	private TextField fldMaxPoolSize;
	private TextField fldAcquireIncrement;
	private TextField fldCatalog;
	private TextField fldSchema;
	private CheckBox chActive;
	private ComboBox cbPoolType;
	private AbstractField fldJNDI;

	public ApplicationForm(MobileServerApplication app, ApplicationSynchronism applicationSynchronism) {
		this.app = app;
		this.applicationSynchronism = applicationSynchronism;
		this.setMargin(true);
		createForm();
		createFields();
		createButtons();

		Label lblTitle = new Label("Aplicação");
		lblTitle.setStyleName("h2 color");
		addComponent(lblTitle);
		addComponent(applicationForm);
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

		applicationForm.getFooter().addComponent(buttons);
	}

	private void createFields() {
		fldId = new TextField();
		fldId.setCaption("Id");
		fldId.setWidth("100px");
		fldId.setStyleName("small");

		fldName = new TextField();
		fldName.setCaption("Nome da aplicação");
		fldName.setWidth("200px");
		fldName.setRequired(true);
		fldName.setRequiredError("Informe o nome da aplicação.");
		fldName.setStyleName("small");

		fldDescription = new TextField();
		fldDescription.setCaption("Descrição da aplicação");
		fldDescription.setWidth("500px");
		fldDescription.setRequired(true);
		fldDescription.setRequiredError("Informe a descrição da aplicação.");
		fldDescription.setStyleName("small");

		cbDialect = new ComboBox();
		cbDialect.setImmediate(false);
		cbDialect.setWidth("-1px");
		cbDialect.setHeight("-1px");
		cbDialect.addItem(MobileServerContext.H2);
		cbDialect.addItem(MobileServerContext.ORACLE);
		cbDialect.addItem(MobileServerContext.MYSQL);
		cbDialect.addItem(MobileServerContext.FIREBIRD);
		cbDialect.addItem(MobileServerContext.POSTGRESQL);
		cbDialect.setRequired(true);
		cbDialect.setCaption("Dialeto");
		cbDialect.setRequiredError("Informe a dialeto da aplicação.");
		cbDialect.setStyleName("small");

		fldURL = new TextField();
		fldURL.setImmediate(false);
		fldURL.setWidth("458px");
		fldURL.setHeight("-1px");
		fldURL.setCaption("URL");
		fldURL.setRequired(true);
		fldURL.setRequiredError("Informe a URL de conexão com o banco de dados.");

		fldUser = new TextField();
		fldUser.setImmediate(false);
		fldUser.setWidth("157px");
		fldUser.setHeight("-1px");
		fldUser.setCaption("Usuário");
		fldUser.setRequired(true);
		fldUser.setRequiredError("Informe o usuário para conexão com o banco de dados.");

		fldPassword = new PasswordField();
		fldPassword.setImmediate(false);
		fldPassword.setWidth("157px");
		fldPassword.setHeight("-1px");
		fldPassword.setCaption("Senha");

		cbPoolType = new ComboBox();
		cbPoolType.setImmediate(false);
		cbPoolType.setWidth("-1px");
		cbPoolType.setHeight("-1px");
		cbPoolType.addItem(PoolDatasource.POOL_C3P0);
		cbPoolType.addItem(PoolDatasource.POOL_TOMCAT);
		cbPoolType.addItem(PoolDatasource.POOL_JNDI);
		cbPoolType.addItem(PoolDatasource.JDBC_WITHOUT_PO0L);
		cbPoolType.setRequired(true);
		cbPoolType.setCaption("Gerenciador conexões");
		cbPoolType.setRequiredError("Informe o gerenciador de conexões da aplicação.");
		cbPoolType.setStyleName("small");

		fldJNDI = new TextField();
		fldJNDI.setImmediate(false);
		fldJNDI.setWidth("250px");
		fldJNDI.setHeight("-1px");
		fldJNDI.setCaption("Recurso JNDI");
		fldJNDI.setRequired(false);
		fldJNDI.setRequiredError("Informe o nome do recurso JNDI.");

		fldInitPoolSize = new TextField();
		fldInitPoolSize.setImmediate(false);
		fldInitPoolSize.setWidth("157px");
		fldInitPoolSize.setHeight("-1px");
		fldInitPoolSize.setCaption("Tamanho Inicial do Pool");
		fldInitPoolSize.setRequired(true);
		fldInitPoolSize.setRequiredError("Informe o tamanho inicial para o pool de conexões da aplicação.");

		fldMinPoolSize = new TextField();
		fldMinPoolSize.setImmediate(false);
		fldMinPoolSize.setWidth("157px");
		fldMinPoolSize.setHeight("-1px");
		fldMinPoolSize.setCaption("Tamanho mínimo do Pool");
		fldMinPoolSize.setRequired(true);
		fldMinPoolSize.setRequiredError("Informe o tamanho mínimo para o pool de conexões da aplicação.");

		fldMaxPoolSize = new TextField();
		fldMaxPoolSize.setImmediate(false);
		fldMaxPoolSize.setWidth("157px");
		fldMaxPoolSize.setHeight("-1px");
		fldMaxPoolSize.setCaption("Tamanho máximo do Pool");
		fldMaxPoolSize.setRequired(true);
		fldMaxPoolSize.setRequiredError("Informe o tamanho máximo para o pool de conexões da aplicação.");

		fldAcquireIncrement = new TextField();
		fldAcquireIncrement.setImmediate(false);
		fldAcquireIncrement.setWidth("157px");
		fldAcquireIncrement.setHeight("-1px");
		fldAcquireIncrement.setCaption("Incremento do Pool");
		fldAcquireIncrement.setRequired(true);
		fldAcquireIncrement.setRequiredError("Informe o tamanho para incremento do pool de conexões da aplicação.");

		fldCatalog = new TextField();
		fldCatalog.setImmediate(false);
		fldCatalog.setWidth("157px");
		fldCatalog.setHeight("-1px");
		fldCatalog.setCaption("Catalog");

		fldSchema = new TextField();
		fldSchema.setImmediate(false);
		fldSchema.setWidth("157px");
		fldSchema.setHeight("-1px");
		fldSchema.setCaption("Schema");

		chActive = new CheckBox();
		chActive.setCaption("Ativa?");
		chActive.setImmediate(false);
		chActive.setWidth("-1px");
		chActive.setHeight("-1px");

		applicationForm.addField("fldId", fldId);
		applicationForm.addField("fldName", fldName);
		applicationForm.addField("fldDescription", fldDescription);
		applicationForm.addField("cbDialect", cbDialect);
		applicationForm.addField("cbPoolType", cbPoolType);
		applicationForm.addField("fldJNDI", fldJNDI);
		applicationForm.addField("fldURL", fldURL);
		applicationForm.addField("fldUser", fldUser);
		applicationForm.addField("fldPassword", fldPassword);
		applicationForm.addField("fldInitPoolSize", fldInitPoolSize);
		applicationForm.addField("fldMinPoolSize", fldMinPoolSize);
		applicationForm.addField("fldMaxPoolSize", fldMaxPoolSize);
		applicationForm.addField("fldAcquireIncrement", fldAcquireIncrement);
		applicationForm.addField("fldCatalog", fldCatalog);
		applicationForm.addField("fldSchema", fldSchema);
		applicationForm.addField("chActive", chActive);
	}

	private void createForm() {
		applicationForm = new Form();
		applicationForm.setWriteThrough(false);
		applicationForm.setInvalidCommitted(false);
		applicationForm.getFooter().setMargin(false, false, true, true);
	}

	public void buttonClick(ClickEvent event) {
		final ApplicationForm comp = this;
		final Form f = applicationForm;
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
				userMessages.confirm("Gravar os dados?", new ClickListener() {

					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							try {
								f.setValidationVisible(true);
								f.validate();
								saveData();
								if (MobileServerData.save(app, applicationSynchronism)) {
									changeDataTreeItem();
									app.getTree().select(applicationSynchronism.getId());
									app.removeTab(comp);
								}
							} catch (Exception e) {
								getWindow()
										.showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					}
				});
			}
		}
	}

	private void changeDataTreeItem() {
		Item item = app.getTree().getItem(applicationSynchronism.getId());
		if (item == null) {
			item = app.getTree().addItem(applicationSynchronism.getId());
		}
		item.getItemProperty(MobileServerData.PROPERTY_NAME).setValue(
				applicationSynchronism.getName() + " " + applicationSynchronism.getId());
		item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(applicationSynchronism);
		((HierarchicalContainer) app.getTree().getContainerDataSource()).setChildrenAllowed(
				applicationSynchronism.getId(), true);
	}

	private void loadData() {
		if (applicationSynchronism.getId() != null)
			fldId.setValue(applicationSynchronism.getId());
		if (applicationSynchronism.getName() != null)
			fldName.setValue(applicationSynchronism.getName());
		if (applicationSynchronism.getDescription() != null)
			fldDescription.setValue(applicationSynchronism.getDescription());
		if (applicationSynchronism.getUser() != null)
			fldUser.setValue(applicationSynchronism.getUser());
		if (applicationSynchronism.getPassword() != null)
			fldPassword.setValue(applicationSynchronism.getPassword());
		if (applicationSynchronism.getDialect() != null)
			cbDialect.setValue(applicationSynchronism.getDialect());
		if (applicationSynchronism.getDefaultCatalog() != null)
			fldCatalog.setValue(applicationSynchronism.getDefaultCatalog());
		if (applicationSynchronism.getDefaultSchema() != null)
			fldSchema.setValue(applicationSynchronism.getDefaultSchema());
		if (applicationSynchronism.getJdbcUrl() != null)
			fldURL.setValue(applicationSynchronism.getJdbcUrl());
		if (applicationSynchronism.getAcquireIncrement() != null)
			fldAcquireIncrement.setValue(applicationSynchronism.getAcquireIncrement());
		if (applicationSynchronism.getInitialPoolSize() != null)
			fldInitPoolSize.setValue(applicationSynchronism.getInitialPoolSize());
		if (applicationSynchronism.getMaxPoolSize() != null)
			fldMaxPoolSize.setValue(applicationSynchronism.getMaxPoolSize());
		if (applicationSynchronism.getMinPoolSize() != null)
			fldMinPoolSize.setValue(applicationSynchronism.getMinPoolSize());
		if (applicationSynchronism.getConnectionPoolType() != null)
			cbPoolType.setValue(applicationSynchronism.getConnectionPoolType());
		if (applicationSynchronism.getJndiName() != null)
			fldJNDI.setValue(applicationSynchronism.getJndiName());
		if ("S".equals(applicationSynchronism.getActive()))
			chActive.setValue(true);
		else
			chActive.setValue(false);
	}

	private void saveData() {
		Object value = fldId.getValue();
		if (!StringUtils.isEmpty(value + "")) {
			applicationSynchronism.setId(new Long(fldId.getValue() + ""));
		}
		applicationSynchronism.setName(fldName.getValue() + "");
		applicationSynchronism.setDescription(fldDescription.getValue() + "");
		if (!StringUtils.isEmpty(fldAcquireIncrement.getValue() + ""))
			applicationSynchronism.setAcquireIncrement(new Long(fldAcquireIncrement.getValue() + ""));
		if (!StringUtils.isEmpty(fldMaxPoolSize.getValue() + ""))
			applicationSynchronism.setMaxPoolSize(new Long(fldMaxPoolSize.getValue() + ""));
		if (!StringUtils.isEmpty(fldMinPoolSize.getValue() + ""))
			applicationSynchronism.setMinPoolSize(new Long(fldMinPoolSize.getValue() + ""));
		if (!StringUtils.isEmpty(fldInitPoolSize.getValue() + ""))
			applicationSynchronism.setInitialPoolSize(new Long(fldInitPoolSize.getValue() + ""));
		if (chActive.booleanValue() == true)
			applicationSynchronism.setActive("S");
		else
			applicationSynchronism.setActive("N");

		applicationSynchronism.setDefaultCatalog(fldCatalog.getValue() + "");
		applicationSynchronism.setDefaultSchema(fldSchema.getValue() + "");
		applicationSynchronism.setDialect(cbDialect.getValue() + "");
		applicationSynchronism.setUser(fldUser.getValue() + "");
		applicationSynchronism.setPassword(fldPassword.getValue() + "");
		applicationSynchronism.setConnectionPoolType(cbPoolType.getValue() + "");
		applicationSynchronism.setJndiName(fldJNDI.getValue() + "");

		Class dialectClass = null;
		if (MobileServerContext.H2.equals(cbDialect.getValue() + ""))
			dialectClass = H2Dialect.class;
		else if (MobileServerContext.ORACLE.equals(cbDialect.getValue() + ""))
			dialectClass = oracle.jdbc.driver.OracleDriver.class;
		else if (MobileServerContext.MYSQL.equals(cbDialect.getValue() + ""))
			dialectClass = MySQLDialect.class;
		else if (MobileServerContext.FIREBIRD.equals(cbDialect.getValue() + ""))
			dialectClass = FirebirdDialect.class;
		else if (MobileServerContext.POSTGRESQL.equals(cbDialect.getValue() + ""))
			dialectClass = PostgreSqlDialect.class;
		applicationSynchronism.setDriverClass(dialectClass.getName());

		applicationSynchronism.setJdbcUrl(fldURL.getValue() + "");
	}

}
