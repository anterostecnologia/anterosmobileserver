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

import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerContext;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.persistence.util.StringUtils;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfigurationWindow extends Window implements ClickListener {

	private ConfigurationForm configurationForm;
	private MobileServerApplication app;

	public ConfigurationWindow(MobileServerApplication app) {
		this.app = app;
		setCaption("Configuração do Servidor");
		setModal(true);
		configurationForm = new ConfigurationForm();
		setClosable(false);
		addComponent(configurationForm);
		setResizable(false);
		setDraggable(false);

		VerticalLayout layout = (VerticalLayout) this.getContent();
		layout.setSpacing(true);

		layout.setWidth("765px");
		layout.setHeight("460px");

		configurationForm.getBtnOk().addListener(this);
		configurationForm.getBtnCancel().addListener(this);

		loadPreferences();
	}

	private void loadPreferences() {
		MobileServerData.readPreferences(app);
		MobileServerContext mobileServerContext = MobileServerData.getMobileServerContext(app);
		configurationForm.getCbDialect().setValue(mobileServerContext.getDialect());
		configurationForm.getFldURL().setValue(mobileServerContext.getJdbcUrl());
		configurationForm.getFldUser().setValue(mobileServerContext.getUser());
		configurationForm.getFldPassword().setValue(mobileServerContext.getPassword());
		configurationForm.getFldCatalog().setValue(mobileServerContext.getDefaultCatalog());
		configurationForm.getFldSchema().setValue(mobileServerContext.getDefaultSchema());
		configurationForm.getFldInitPoolSize().setValue(mobileServerContext.getInitialPoolSize());
		configurationForm.getFldMinPoolSize().setValue(mobileServerContext.getMinPoolSize());
		configurationForm.getFldMaxPoolSize().setValue(mobileServerContext.getMaxPoolSize());
		configurationForm.getFldAcquireIncrement().setValue(mobileServerContext.getAcquireIncrement());
		configurationForm.getChShowSql().setValue(mobileServerContext.isShowSql());
		configurationForm.getChFormatSql().setValue(mobileServerContext.isFormatSql());
		configurationForm.getFldAccessUser().setValue(mobileServerContext.getAccessUser());
		configurationForm.getFldAccessPassword().setValue(mobileServerContext.getAccessPassword());
		configurationForm.getCbTipoPool().setValue(mobileServerContext.getConnectionPoolType());
		configurationForm.getFldJNDI().setValue(mobileServerContext.getJndiName());
		configurationForm.getFldQueryTimeout().setValue(mobileServerContext.getQueryTimeout());
	}

	private void savePreferences() {
		MobileServerContext mobileServerContext = MobileServerData.getMobileServerContext(app);
		mobileServerContext.writePreferences(configurationForm.getFldURL().getValue() + "", configurationForm
				.getFldUser().getValue() + "", configurationForm.getFldPassword().getValue() + "", new Integer(
				configurationForm.getFldAcquireIncrement().getValue() + ""), new Integer(configurationForm
				.getFldInitPoolSize().getValue() + ""), new Integer(configurationForm.getFldMaxPoolSize().getValue()
				+ ""), new Integer(configurationForm.getFldAcquireIncrement().getValue() + ""), new Boolean(
				configurationForm.getChShowSql().getValue() + "").booleanValue(), configurationForm.getCbDialect()
				.getValue() + "", new Boolean(configurationForm.getChFormatSql().getValue() + "").booleanValue(),
				configurationForm.getFldSchema().getValue() + "",configurationForm.getFldCatalog().getValue() + "",
				configurationForm.getFldAccessUser().getValue() + "", configurationForm.getFldAccessPassword()
						.getValue() + "", configurationForm.getCbTipoPool()
						.getValue() + "", configurationForm.getFldJNDI()
						.getValue() + "",new Integer(configurationForm
								.getFldQueryTimeout().getValue() + ""));
	}

	public void buttonClick(ClickEvent event) {
		if (event.getSource() == configurationForm.getBtnOk()) {
			if ((configurationForm.getCbDialect().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getCbDialect().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe o dialeto para o dicionário do servidor!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getCbDialect().focus();
			} else if ((configurationForm.getFldURL().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getFldURL().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe a URL de conexão para o dicionário do servidor!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getFldURL().focus();
			} else if ((configurationForm.getFldUser().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getFldUser().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe o usuário de conexão para o dicionário do servidor!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getFldUser().focus();
			} else if ((configurationForm.getFldInitPoolSize().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getFldInitPoolSize().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe o tamanho inicial para o pool de conexões do dicionário do servidor!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getFldInitPoolSize().focus();
			} else if ((configurationForm.getFldMinPoolSize().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getFldMinPoolSize().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe o tamanho mínimo para o pool de conexões do dicionário do servidor!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getFldMinPoolSize().focus();
			} else if ((configurationForm.getFldMaxPoolSize().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getFldMaxPoolSize().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe o tamanho máximo para o pool de conexões do dicionário do servidor!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getFldMaxPoolSize().focus();
			} else if ((configurationForm.getFldAcquireIncrement().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getFldAcquireIncrement().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe o tamanho para incrementar o pool de conexões do dicionário do servidor!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getFldAcquireIncrement().focus();
			} else if ((configurationForm.getFldQueryTimeout().getValue() == null)
					|| (StringUtils.isEmpty(configurationForm.getFldQueryTimeout().getValue() + ""))) {
				app.getMainWindow().showNotification("Atenção",
						"<br/>Informe 0 ou um valor para o tempo de espera na execução das Querys no servidor !",
						Window.Notification.TYPE_WARNING_MESSAGE);
				configurationForm.getFldQueryTimeout().focus();	
			} else {
				boolean configured = false;
				try {
					configurationForm.getBtnOk().setEnabled(false);
					savePreferences();
					configured = (MobileServerData.reconfigureSession(app));
				} finally {
					if (configured) {
						app.getMainWindow().removeWindow(this);
						app.createMainLayout();
					} else
						configurationForm.getBtnOk().setEnabled(true);
				}
			}
		} else if (event.getSource() == configurationForm.getBtnCancel()) {
			app.getMainWindow().removeWindow(this);
		}
	}

	public ConfigurationForm getConfigurationForm() {
		return configurationForm;
	}

}
