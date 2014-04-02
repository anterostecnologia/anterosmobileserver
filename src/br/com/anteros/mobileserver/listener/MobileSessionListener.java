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
package br.com.anteros.mobileserver.listener;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.anteros.mobileserver.app.MobileServerContext;
import br.com.anteros.mobileserver.app.MobileSession;
import br.com.anteros.persistence.session.SQLSession;

public class MobileSessionListener implements HttpSessionBindingListener, HttpSessionActivationListener,
		HttpSessionAttributeListener, HttpSessionListener {

	private static Logger log = LoggerFactory.getLogger(MobileSessionListener.class);

	public MobileSessionListener() {
	}

	public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	public void sessionDidActivate(HttpSessionEvent httpSessionEvent) {
	}

	public void sessionWillPassivate(HttpSessionEvent httpSessionEvent) {
	}

	public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	public void sessionCreated(HttpSessionEvent sessionEvent) {
		log.info("Sessão CRIADA " + sessionEvent.getSession().getId());

	}

	public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {

		MobileServerContext mobileServerContext = (MobileServerContext) sessionEvent.getSession().getServletContext()
				.getAttribute("mobileServerContext");
		MobileSession mSession = mobileServerContext.removeMobileSession(sessionEvent.getSession());
		try {
			mSession.getSQLSession().close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("=> Sessão DESTRUÍDA " + sessionEvent.getSession().getId());

	}
}
