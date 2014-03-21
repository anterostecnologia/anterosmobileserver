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

import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;

import com.vaadin.Application;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class ParameterWindow extends Window {

	private ParameterForm formParameter;

	public ParameterWindow(MobileServerApplication app,
			ParameterSynchronism parameterSynchronism, Synchronism objectOwner) {
		if ((parameterSynchronism.getId() == null)
				|| (parameterSynchronism.getId().intValue() == 0)) {
			setCaption("Adicionando");
		} else
			setCaption("Editando");
		setModal(true);
		setTheme(Reindeer.THEME_NAME);
		formParameter = new ParameterForm(app, parameterSynchronism, this, objectOwner,false);
		addComponent(formParameter);

		VerticalLayout layout = (VerticalLayout) this.getContent();

		layout.setWidth("700px");
		layout.setHeight("265px");

	}

	public ParameterForm getFormParameter() {
		return formParameter;
	}

}
