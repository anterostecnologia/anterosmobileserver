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

import br.com.anteros.mobileserver.app.MobileServerContext;
import br.com.anteros.mobileserver.controller.PoolDatasource;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class ConfigurationForm extends CustomComponent {

	private AbsoluteLayout mainLayout;
	private Label lblJNDI;
	private TextField fldJNDI;
	private Label lblTipoPool;
	private ComboBox cbTipoPool;
	private PasswordField fldAccessPassword;
	private Label lblAccessPassword;
	private TextField fldAccessUser;
	private Label lblAccessUser;
	private Label lblAccessControl;
	private Embedded imgConfiguration;
	private Button btnCancel;
	private Button btnOk;
	private CheckBox chFormatSql;
	private CheckBox chShowSql;
	private Label lblAcquireIncrement;
	private TextField fldAcquireIncrement;
	private TextField fldMaxPoolSize;
	private Label lblMaxPoolSize;
	private Label lblMinPoolSize;
	private TextField fldMinPoolSize;
	private Label lblInitialPoolSize;
	private Label label_2;
	private Label lblPool;
	private TextField fldInitPoolSize;
	private Embedded imgAnteros;
	private Label lblSchema;
	private TextField fldSchema;
	private Label lblCatalog;
	private TextField fldCatalog;
	private Label lblPassword;
	private PasswordField fldPassword;
	private Label lblUser;
	private TextField fldUser;
	private Label lblURL;
	private TextField fldURL;
	private Label lblDialect;
	private ComboBox cbDialect;

	public ConfigurationForm() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		// cbDialect
		cbDialect = new ComboBox();
		cbDialect.setImmediate(false);
		cbDialect.setWidth("-1px");
		cbDialect.setHeight("-1px");
		cbDialect.addItem(MobileServerContext.H2);
		cbDialect.addItem(MobileServerContext.ORACLE);
		cbDialect.addItem(MobileServerContext.MYSQL);
		cbDialect.addItem(MobileServerContext.FIREBIRD);
		cbDialect.addItem(MobileServerContext.POSTGRESQL);
		

		// lblDialect
		lblDialect = new Label();
		lblDialect.setImmediate(false);
		lblDialect.setWidth("-1px");
		lblDialect.setHeight("-1px");
		lblDialect.setValue("Dialeto");
		
		

		// fldURL
		fldURL = new TextField();
		fldURL.setImmediate(false);
		fldURL.setWidth("458px");
		fldURL.setHeight("-1px");
		
		// lblURL
		lblURL = new Label();
		lblURL.setImmediate(false);
		lblURL.setWidth("-1px");
		lblURL.setHeight("-1px");
		lblURL.setValue("Url conexão");
		
		

		// fldUser
		fldUser = new TextField();
		fldUser.setImmediate(false);
		fldUser.setWidth("157px");
		fldUser.setHeight("-1px");
		
		// lblUser
		lblUser = new Label();
		lblUser.setImmediate(false);
		lblUser.setWidth("-1px");
		lblUser.setHeight("-1px");
		lblUser.setValue("Usuário");
		
		// fldPassword
		fldPassword = new PasswordField();
		fldPassword.setImmediate(false);
		fldPassword.setWidth("157px");
		fldPassword.setHeight("-1px");
		

		// lblPassword
		lblPassword = new Label();
		lblPassword.setImmediate(false);
		lblPassword.setWidth("-1px");
		lblPassword.setHeight("-1px");
		lblPassword.setValue("Senha");
		

		// fldCatalog
		fldCatalog = new TextField();
		fldCatalog.setImmediate(false);
		fldCatalog.setWidth("157px");
		fldCatalog.setHeight("-1px");
		

		// lblCatalog
		lblCatalog = new Label();
		lblCatalog.setImmediate(false);
		lblCatalog.setWidth("-1px");
		lblCatalog.setHeight("-1px");
		lblCatalog.setValue("Catalog");

		// fldSchema
		fldSchema = new TextField();
		fldSchema.setImmediate(false);
		fldSchema.setWidth("157px");
		fldSchema.setHeight("-1px");

		// lblSchema
		lblSchema = new Label();
		lblSchema.setImmediate(false);
		lblSchema.setWidth("-1px");
		lblSchema.setHeight("-1px");
		lblSchema.setValue("Schema");
		

		// imgAnteros
		imgAnteros = new Embedded();
		imgAnteros.setImmediate(false);
		imgAnteros.setWidth("165px");
		imgAnteros.setHeight("45px");
		imgAnteros.setSource(new ThemeResource("images/anteros_mobile_server45.png"));
		imgAnteros.setType(1);
		imgAnteros.setMimeType("image/png");
		

		// fldInitPoolSize
		fldInitPoolSize = new TextField();
		fldInitPoolSize.setImmediate(false);
		fldInitPoolSize.setWidth("157px");
		fldInitPoolSize.setHeight("-1px");

		// lblPool
		lblPool = new Label();
		lblPool.setImmediate(false);
		lblPool.setWidth("-1px");
		lblPool.setHeight("-1px");
		lblPool.setValue("<b>Pool de conexões</b>");
		lblPool.setContentMode(3);

		// label_2
		label_2 = new Label();
		label_2.setImmediate(false);
		label_2.setWidth("-1px");
		label_2.setHeight("-1px");
		label_2.setValue("<b>Pool de conexões</b>");
		label_2.setContentMode(3);

		// lblInitialPoolSize
		lblInitialPoolSize = new Label();
		lblInitialPoolSize.setImmediate(false);
		lblInitialPoolSize.setWidth("-1px");
		lblInitialPoolSize.setHeight("-1px");
		lblInitialPoolSize.setValue("Tamanho inicial");
		

		// fldMinPoolSize
		fldMinPoolSize = new TextField();
		fldMinPoolSize.setImmediate(false);
		fldMinPoolSize.setWidth("157px");
		fldMinPoolSize.setHeight("-1px");


		// lblMinPoolSize
		lblMinPoolSize = new Label();
		lblMinPoolSize.setImmediate(false);
		lblMinPoolSize.setWidth("-1px");
		lblMinPoolSize.setHeight("-1px");
		lblMinPoolSize.setValue("Tamanho Mínimo");


		// lblMaxPoolSize
		lblMaxPoolSize = new Label();
		lblMaxPoolSize.setImmediate(false);
		lblMaxPoolSize.setWidth("-1px");
		lblMaxPoolSize.setHeight("-1px");
		lblMaxPoolSize.setValue("Tamanho Máximo");


		// fldMaxPoolSize
		fldMaxPoolSize = new TextField();
		fldMaxPoolSize.setImmediate(false);
		fldMaxPoolSize.setWidth("157px");
		fldMaxPoolSize.setHeight("-1px");
		

		// fldAcquireIncrement
		fldAcquireIncrement = new TextField();
		fldAcquireIncrement.setImmediate(false);
		fldAcquireIncrement.setWidth("157px");
		fldAcquireIncrement.setHeight("-1px");


		// lblAcquireIncrement
		lblAcquireIncrement = new Label();
		lblAcquireIncrement.setImmediate(false);
		lblAcquireIncrement.setWidth("-1px");
		lblAcquireIncrement.setHeight("-1px");
		lblAcquireIncrement.setValue("Incremento");


		// chShowSql
		chShowSql = new CheckBox();
		chShowSql.setCaption("Mostrar SQL's no log");
		chShowSql.setImmediate(false);
		chShowSql.setWidth("-1px");
		chShowSql.setHeight("-1px");
		

		// chFormatSql
		chFormatSql = new CheckBox();
		chFormatSql.setCaption("Formatar SQL's ");
		chFormatSql.setImmediate(false);
		chFormatSql.setWidth("-1px");
		chFormatSql.setHeight("-1px");


		// btnOk
		btnOk = new Button();
		btnOk.setCaption("Ok");
		btnOk.setIcon(new ThemeResource("icons/16/ok.png"));
		btnOk.setImmediate(true);
		btnOk.setWidth("-1px");
		btnOk.setHeight("-1px");

		// btnCancel
		btnCancel = new Button();
		btnCancel.setCaption("Cancela");
		btnCancel.setIcon(new ThemeResource("icons/16/cancel.png"));
		btnCancel.setImmediate(true);
		btnCancel.setWidth("-1px");
		btnCancel.setHeight("-1px");
		

		// imgConfiguration
		imgConfiguration = new Embedded();
		imgConfiguration.setImmediate(false);
		imgConfiguration.setWidth("48px");
		imgConfiguration.setHeight("48px");
		imgConfiguration.setSource(new ThemeResource("images/configuration.png"));
		imgConfiguration.setType(1);
		imgConfiguration.setMimeType("image/png");
		

		// lblAccessControl
		lblAccessControl = new Label();
		lblAccessControl.setImmediate(false);
		lblAccessControl.setWidth("-1px");
		lblAccessControl.setHeight("-1px");
		lblAccessControl.setValue("<b>Controle de acesso</b>");
		lblAccessControl.setContentMode(3);
		

		// lblAccessUser
		lblAccessUser = new Label();
		lblAccessUser.setImmediate(false);
		lblAccessUser.setWidth("-1px");
		lblAccessUser.setHeight("-1px");
		lblAccessUser.setValue("Usuário");
		

		// fldAccessUser
		fldAccessUser = new TextField();
		fldAccessUser.setImmediate(false);
		fldAccessUser.setWidth("157px");
		fldAccessUser.setHeight("-1px");
		

		// lblAccessPassword
		lblAccessPassword = new Label();
		lblAccessPassword.setImmediate(false);
		lblAccessPassword.setWidth("-1px");
		lblAccessPassword.setHeight("-1px");
		lblAccessPassword.setValue("Senha");
		

		// fldAccessPassword
		fldAccessPassword = new PasswordField();
		fldAccessPassword.setImmediate(false);
		fldAccessPassword.setWidth("157px");
		fldAccessPassword.setHeight("-1px");
		

		// cbTipoPool
		cbTipoPool = new ComboBox();
		cbTipoPool.setImmediate(false);
		cbTipoPool.setWidth("100.0%");
		cbTipoPool.setHeight("-1px");
		cbTipoPool.addItem(PoolDatasource.POOL_C3P0);
		cbTipoPool.addItem(PoolDatasource.POOL_TOMCAT);
		cbTipoPool.addItem(PoolDatasource.POOL_JNDI);
		cbTipoPool.addItem(PoolDatasource.JDBC_WITHOUT_PO0L);

		// lblTipoPool
		lblTipoPool = new Label();
		lblTipoPool.setImmediate(false);
		lblTipoPool.setWidth("103px");
		lblTipoPool.setHeight("-1px");
		lblTipoPool.setValue("Gerenciador pool");
		

		// fldJNDI
		fldJNDI = new TextField();
		fldJNDI.setImmediate(false);
		fldJNDI.setWidth("300px");
		fldJNDI.setHeight("-1");

		// lblJNDI
		lblJNDI = new Label();
		lblJNDI.setImmediate(false);
		lblJNDI.setWidth("-1px");
		lblJNDI.setHeight("-1px");
		lblJNDI.setValue("Recurso JNDI");
		
		
		mainLayout.addComponent(cbDialect, "top:20.0px;left:269.0px;");
		mainLayout.addComponent(lblDialect, "top:17.0px;left:223.0px;");
		mainLayout.addComponent(fldURL, "top:44.0px;left:269.0px;");
		mainLayout.addComponent(lblURL, "top:41.0px;left:195.0px;");
		mainLayout.addComponent(fldUser, "top:69.0px;left:269.0px;");
		mainLayout.addComponent(lblUser, "top:69.0px;left:218.0px;");
		mainLayout.addComponent(fldPassword, "top:69.0px;left:571.0px;");
		mainLayout.addComponent(lblPassword, "top:69.0px;left:529.0px;");
		mainLayout.addComponent(fldCatalog, "top:94.0px;left:269.0px;");
		mainLayout.addComponent(lblCatalog, "top:94.0px;left:216.0px;");
		mainLayout.addComponent(fldSchema, "top:94.0px;left:571.0px;");
		mainLayout.addComponent(lblSchema, "top:93.0px;left:519.0px;");
		mainLayout.addComponent(imgAnteros, "top:5.0px;left:5.0px;");
		mainLayout.addComponent(cbTipoPool, "top:159.0px;right:230.0px;left:269.0px;");
		mainLayout.addComponent(lblTipoPool, "top:156.0px;left:165.0px;");
		mainLayout.addComponent(lblJNDI, "top:197.0px;left:184.0px;");
		mainLayout.addComponent(fldJNDI, "top:197.0px;left:269.0px;");
		mainLayout.addComponent(fldInitPoolSize, "top:221.0px;left:269.0px;");
		mainLayout.addComponent(lblPool, "top:130.0px;left:269.0px;");
		mainLayout.addComponent(label_2, "top:130.0px;left:269.0px;");
		mainLayout.addComponent(lblInitialPoolSize, "top:221.0px;left:172.0px;");
		mainLayout.addComponent(fldMinPoolSize, "top:246.0px;left:269.0px;");
		mainLayout.addComponent(lblMinPoolSize, "top:247.0px;left:164.0px;");
		mainLayout.addComponent(lblMaxPoolSize, "top:273.0px;left:162.0px;");
		mainLayout.addComponent(fldMaxPoolSize, "top:272.0px;left:269.0px;");
		mainLayout.addComponent(fldAcquireIncrement, "top:297.0px;left:269.0px;");
		mainLayout.addComponent(lblAcquireIncrement, "top:298.0px;left:200.0px;");
		mainLayout.addComponent(chShowSql, "top:234.0px;left:571.0px;");
		mainLayout.addComponent(chFormatSql, "top:258.0px;left:571.0px;");
		mainLayout.addComponent(imgConfiguration, "top:153.0px;left:30.0px;");
		mainLayout.addComponent(lblAccessControl, "top:328.0px;left:180.0px;");
		mainLayout.addComponent(lblAccessUser, "top:350.0px;left:220.0px;");
		mainLayout.addComponent(fldAccessUser, "top:350.0px;left:269.0px;");
		mainLayout.addComponent(lblAccessPassword, "top:349.0px;left:529.0px;");
		mainLayout.addComponent(fldAccessPassword, "top:348.0px;left:571.0px;");
		mainLayout.addComponent(btnOk, "top:394.0px;left:580.0px;");
		mainLayout.addComponent(btnCancel, "top:394.0px;left:648.0px;");
		
		



		return mainLayout;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}

	public Button getBtnOk() {
		return btnOk;
	}

	public TextField getFldSchema() {
		return fldSchema;
	}

	public TextField getFldCatalog() {
		return fldCatalog;
	}

	public TextField getFldAcquireIncrement() {
		return fldAcquireIncrement;
	}

	public TextField getFldMaxPoolSize() {
		return fldMaxPoolSize;
	}

	public TextField getFldMinPoolSize() {
		return fldMinPoolSize;
	}

	public TextField getFldInitPoolSize() {
		return fldInitPoolSize;
	}

	public PasswordField getFldPassword() {
		return fldPassword;
	}

	public TextField getFldUser() {
		return fldUser;
	}

	public TextField getFldURL() {
		return fldURL;
	}

	public ComboBox getCbDialect() {
		return cbDialect;
	}

	public CheckBox getChFormatSql() {
		return chFormatSql;
	}

	public CheckBox getChShowSql() {
		return chShowSql;
	}

	public PasswordField getFldAccessPassword() {
		return fldAccessPassword;
	}

	public TextField getFldAccessUser() {
		return fldAccessUser;
	}

	public TextField getFldJNDI() {
		return fldJNDI;
	}

	public ComboBox getCbTipoPool() {
		return cbTipoPool;
	}

}
