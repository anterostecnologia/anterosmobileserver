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

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class UserLoginForm extends CustomComponent {

	private AbsoluteLayout mainLayout;
	private VerticalLayout verticalLayout;
	private HorizontalLayout horizontalLayout;
	private AbsoluteLayout absoluteLayout;
	private Button btnLogin;
	private PasswordField fldPassword;
	private TextField fldUserName;
	private Label lblUserName;
	private Label lblPassword;
	private Embedded image;

	public UserLoginForm() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		fldUserName.focus();
	}

	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		setWidth("100.0%");
		setHeight("100.0%");

		verticalLayout = buildVerticalLayout();
		mainLayout.addComponent(verticalLayout, "top:0.0px;left:0.0px;");

		return mainLayout;
	}

	private VerticalLayout buildVerticalLayout() {
		verticalLayout = new VerticalLayout();
		verticalLayout.setImmediate(false);
		verticalLayout.setWidth("390px");
		verticalLayout.setHeight("140px");
		verticalLayout.setMargin(false);

		horizontalLayout = buildHorizontalLayout_1();
		verticalLayout.addComponent(horizontalLayout);
		verticalLayout.setExpandRatio(horizontalLayout, 1.0f);
		verticalLayout.setComponentAlignment(horizontalLayout, new Alignment(48));

		return verticalLayout;
	}

	private HorizontalLayout buildHorizontalLayout_1() {
		horizontalLayout = new HorizontalLayout();
		horizontalLayout.setImmediate(false);
		horizontalLayout.setWidth("100.0%");
		horizontalLayout.setHeight("100.0%");
		horizontalLayout.setMargin(false);

		image = new Embedded();
		image.setImmediate(false);
		image.setWidth("64px");
		image.setHeight("64px");
		image.setSource(new ThemeResource("img/login.png"));
		image.setType(1);
		image.setMimeType("image/png");
		horizontalLayout.addComponent(image);
		horizontalLayout.setComponentAlignment(image, new Alignment(33));

		absoluteLayout = buildAbsoluteLayout();
		horizontalLayout.addComponent(absoluteLayout);
		horizontalLayout.setExpandRatio(absoluteLayout, 1.0f);

		return horizontalLayout;
	}

	private AbsoluteLayout buildAbsoluteLayout() {
		absoluteLayout = new AbsoluteLayout();
		absoluteLayout.setImmediate(false);
		absoluteLayout.setWidth("100.0%");
		absoluteLayout.setHeight("100.0%");
		absoluteLayout.setMargin(false);

		lblPassword = new Label();
		lblPassword.setImmediate(false);
		lblPassword.setWidth("-1px");
		lblPassword.setHeight("-1px");
		lblPassword.setValue("Usuário");
		absoluteLayout.addComponent(lblPassword, "top:20.0px;left:16.0px;");

		lblUserName = new Label();
		lblUserName.setImmediate(false);
		lblUserName.setWidth("-1px");
		lblUserName.setHeight("-1px");
		lblUserName.setValue("Senha");
		absoluteLayout.addComponent(lblUserName, "top:54.0px;left:24.0px;");

		fldUserName = new TextField();
		fldUserName.setImmediate(false);
		fldUserName.setWidth("217px");
		fldUserName.setHeight("-1px");
		fldUserName.setSecret(false);
		absoluteLayout.addComponent(fldUserName, "top:20.0px;left:83.0px;");

		fldPassword = new PasswordField();
		fldPassword.setImmediate(false);
		fldPassword.setWidth("217px");
		fldPassword.setHeight("-1px");
		absoluteLayout.addComponent(fldPassword, "top:54.0px;left:83.0px;");

		btnLogin = new Button();
		btnLogin.setCaption("Login");
		btnLogin.setImmediate(true);
		btnLogin.setWidth("100px");
		btnLogin.setHeight("-1px");
		absoluteLayout.addComponent(btnLogin, "top:100.0px;left:200.0px;");

		return absoluteLayout;
	}

	public Button getBtnLogin() {
		return btnLogin;
	}

	public TextField getFldUserName() {
		return fldUserName;
	}

	public PasswordField getFldPassword() {
		return fldPassword;
	}



}
