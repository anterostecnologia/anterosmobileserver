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
import br.com.anteros.mobileserver.util.UserMessages;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class LoginWindow extends Window implements ClickListener {

	private UserLoginForm loginForm;
	private MobileServerApplication app;

	public LoginWindow(MobileServerApplication app) {
		this.app = app;
		setCaption("Login");
		setModal(true);
		loginForm = new UserLoginForm();
		setClosable(false);
		addComponent(loginForm);
		setResizable(false);

		VerticalLayout layout = (VerticalLayout) this.getContent();
		layout.setSpacing(true);

		layout.setWidth("400px");
		layout.setHeight("150px");

		loginForm.getBtnLogin().addListener(this);
	}

	public UserLoginForm getLoginForm() {
		return loginForm;
	}

	public void setLoginForm(UserLoginForm loginForm) {
		this.loginForm = loginForm;
	}

	public void buttonClick(ClickEvent event) {
		MobileServerContext mobileServerContext = MobileServerData.getMobileServerContext(app);
		if (mobileServerContext.getAccessUser().equals(loginForm.getFldUserName().getValue())
				&& (mobileServerContext.getAccessPassword().equals(loginForm.getFldPassword().getValue()))) {
			app.getMainWindow().removeWindow(this);
		} else {
			UserMessages msg = new UserMessages(getWindow());
			msg.error("Usuário ou senha inválido!");
		}
	}

}
