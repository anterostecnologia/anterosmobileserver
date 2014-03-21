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

import br.com.anteros.mobile.core.synchronism.model.FieldSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;

import com.vaadin.Application;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FieldWindow extends Window {

	private FieldForm fieldForm;

	public FieldWindow(MobileServerApplication app,
			FieldSynchronism fieldSynchronism, Synchronism objectOwner) {
		if ((fieldSynchronism.getId() == null)
				|| (fieldSynchronism.getId().intValue() == 0)) {
			setCaption("Adicionando");
		} else
			setCaption("Editando");
		setModal(true);
		fieldForm = new FieldForm(app, fieldSynchronism, this, objectOwner, false);
		addComponent(fieldForm);

		VerticalLayout layout = (VerticalLayout) this.getContent();

		layout.setWidth("700px");
		layout.setHeight("270px");

	}

	public FieldForm getFieldForm() {
		return fieldForm;
	}

}
