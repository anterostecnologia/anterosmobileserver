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
package br.com.anteros.mobileserver.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class UserMessages implements Serializable {

	private static final long serialVersionUID = 2344274165863515876L;
	static final String CONFIRM_TITLE = "Confirme";
	static final String CONFIRM_CANCEL_TITLE = "Não";
	static final String CONFIRM_OK_TITLE = "Sim";
	static final int MAX_DESCRIPTION_LINES = 20;
	public static final String USER_CONFIRM_OK = "OK";
	public static final String USER_CONFIRM_CANCEL = "CANCELA";

	private Window win;
	private Window confirm;

	public UserMessages(Window w) {
		win = w;
		confirm = null;
		if (win == null) {
			throw new IllegalStateException("User messages require a window instance");
		}
	}

	public void error(String message) {
		error(message, null, null);
	}

	public void error(String message, String description) {
		error(message, description, null);
	}

	public void error(String message, String description, Throwable t) {
		if (t != null) {
			ByteArrayOutputStream stos = new ByteArrayOutputStream();
			PrintStream sto = new PrintStream(stos, false);
			t.printStackTrace(sto);
			sto.flush();
			try {
				stos.flush();
			} catch (IOException ignored) {
			}
			String st = stos.toString();
			t.printStackTrace();
			if (description == null)
				description = st;
			else
				description += ":\n" + st;
		}
		description = formatDescription(description);
		win.showNotification(escapeXML(message), description, Notification.TYPE_ERROR_MESSAGE);
	}

	public void error(String message, Throwable t) {
		error(message, null, t);
	}

	public void error(Throwable e) {
		error("Unhandled Exception", null, e);
	}

	public void warning(String message) {
		warning(message, null);
	}

	public void warning(String message, String description) {
		win.showNotification(message, formatDescription(description), Notification.TYPE_WARNING_MESSAGE);
	}

	public void trayNotification(String message) {
		trayNotification(message, null);
	}

	public void trayNotification(String message, String description) {
		win.showNotification(message, formatDescription(description), Notification.TYPE_TRAY_NOTIFICATION);
	}

	public void notification(String message) {
		notification(message, null);
	}

	public void notification(String message, String description) {
		win.showNotification(message, formatDescription(description));
	}

	public void alert(String message) {
		alert(message, null);
	}

	public void alert(String message, String description) {
		win.showNotification(message, formatDescription(description), Notification.DELAY_FOREVER);
	}

	private String formatDescription(String description) {
		if (description != null) {
			description = escapeXML(description);
			description = description.replaceAll("\n", "<br />");
			if (description.length() > 80) {
				String orig = description;
				description = "";
				while (orig.length() > 0) {
					int last = Math.min(80, orig.length());
					description += orig.substring(0, last);
					int lastnl = description.lastIndexOf("<br");
					int lastwb = description.lastIndexOf(' ');
					if (lastwb - lastnl > 10 && lastwb < description.length() - 1) {
						description = description.substring(0, lastwb) + "<br />" + description.substring(lastwb);
					}
					orig = last == orig.length() ? "" : orig.substring(last);
				}
			}

			int pos = description.indexOf("<br");
			int lineCount = 1;
			while (lineCount < MAX_DESCRIPTION_LINES && pos > 0 && pos < description.length()) {
				pos = description.indexOf("<br", pos + 3);
				lineCount++;
			}
			if (pos > 0 && lineCount >= MAX_DESCRIPTION_LINES)
				description = description.substring(0, pos) + "<br />(...)";

		}
		return description;
	}

	public Window confirm(String message, ClickListener listener) {
		return confirm(CONFIRM_TITLE, message, CONFIRM_OK_TITLE, CONFIRM_CANCEL_TITLE, listener);
	}

	public Window confirm(String title, String message, Button.ClickListener listener) {
		return confirm(title, message, CONFIRM_OK_TITLE, CONFIRM_CANCEL_TITLE, listener);
	}

	public Window confirm(String title, String message, String okTitle, String cancelTitle,
			Button.ClickListener listener) {

		if (title == null) {
			title = CONFIRM_OK_TITLE;
		}
		if (cancelTitle == null) {
			cancelTitle = CONFIRM_CANCEL_TITLE;
		}
		if (okTitle == null) {
			okTitle = CONFIRM_OK_TITLE;
		}

		final Window confirm = new Window(title);
		this.confirm = confirm;
		win.addWindow(confirm);

		confirm.addListener(new Window.CloseListener() {

			private static final long serialVersionUID = 1971800928047045825L;

			public void windowClose(CloseEvent ce) {
				Object data = ce.getWindow().getData();
				if (data != null) {
					try {
					} catch (Exception exception) {
						error("Unhandled Exception", exception);
					}
				}
			}
		});

		int chrW = 5;
		int chrH = 15;
		int txtWidth = Math.max(250, Math.min(350, message.length() * chrW));
		int btnHeight = 25;
		int vmargin = 100;
		int hmargin = 40;

		int txtHeight = 2 * chrH * (message.length() * chrW) / txtWidth;

		confirm.setWidth((txtWidth + hmargin) + "px");
		confirm.setHeight((vmargin + txtHeight + btnHeight) + "px");
		confirm.getContent().setSizeFull();

		confirm.center();
		confirm.setModal(true);

		Label text = new Label(message);
		text.setWidth("100%");
		text.setHeight("100%");

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setHeight(btnHeight + 5 + "px");
		buttons.setWidth("100%");
		buttons.setSpacing(true);
		buttons.setMargin(false);

		Button cancel = new Button(cancelTitle, listener);
		cancel.setIcon(new ThemeResource("icons/16/no.png"));
		cancel.setData(USER_CONFIRM_CANCEL);
		cancel.setClickShortcut(KeyCode.ESCAPE);
		Button ok = new Button(okTitle, listener);
		ok.setIcon(new ThemeResource("icons/16/yes.png"));
		ok.setData(USER_CONFIRM_OK);
		ok.setClickShortcut(KeyCode.ENTER);
		buttons.addComponent(ok);
		buttons.setExpandRatio(ok, 1);
		buttons.setComponentAlignment(ok, Alignment.MIDDLE_RIGHT);

		buttons.addComponent(cancel);
		buttons.setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);

		confirm.addComponent(text);
		confirm.addComponent(buttons);
		((VerticalLayout) confirm.getContent()).setExpandRatio(text, 1f);
		confirm.setResizable(false);
		return confirm;
	}

	public void removeConfirm() {
		if (this.confirm != null) {
			win.getApplication().getMainWindow().removeWindow(confirm);
		}
	}

	private String escapeXML(String str) {
		return str == null ? null : com.vaadin.terminal.gwt.server.JsonPaintTarget.escapeXML(str);
	}

}
