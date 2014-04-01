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
import br.com.anteros.mobileserver.util.LogbackHelper;
import ch.qos.logback.core.Appender;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class LogForm extends VerticalLayout implements Button.ClickListener {

	private MobileServerApplication app;
	private VerticalLayout header;
	private Label textLog;
	private Panel textPanel;
	private Button btnRefresh;
	private MobileServerLogAppender appender;

	public LogForm(MobileServerApplication app) {
		this.app = app;
		setSizeUndefined();
		setWidth("1200px");
		setMargin(true);
		setImmediate(true);
		createForm();

		Appender appender = LogbackHelper.getAppenderbyName("MOBILE_SERVER");
		if (appender instanceof MobileServerLogAppender) {
			this.appender = ((MobileServerLogAppender) appender);
			((MobileServerLogAppender) appender).setLogForm(this);
		}
	}

	private void createForm() {
		btnRefresh = new Button("Atualizar");
		btnRefresh.addListener(this);
		header = new VerticalLayout();
		header.addComponent(btnRefresh);
		header.setWidth("100%");
		header.setComponentAlignment(btnRefresh, Alignment.MIDDLE_LEFT);
		addComponent(header);

		textPanel = new Panel();
		textPanel.setImmediate(true);
		textPanel.setHeight("100%");
		textPanel.setWidth("100%");

		textLog = new Label();
		textLog.setContentMode(Label.CONTENT_XHTML);
		textLog.setWidth("100%");
		textLog.setHeight("100%");
		addComponent(textPanel);
		textPanel.addComponent(textLog);
	}

	public Label getTextLog() {
		return textLog;
	}

	public Panel getTextPanel() {
		return textPanel;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (appender != null)
			appender.refresh();
	}

}
