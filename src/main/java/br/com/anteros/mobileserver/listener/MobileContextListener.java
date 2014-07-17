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

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.anteros.mobile.core.synchronism.engine.DictionaryManager;
import br.com.anteros.mobileserver.app.MobileServerContext;

public class MobileContextListener implements ServletContextListener, ServletContextAttributeListener {

	public static final String MOBILE_SERVER_CONTEXT = "mobileServerContext";
	public static final String DICTIONARY_MANAGER = "dictionaryManager";
	public static final String SYNCHRONISM_MANAGER = "synchronismManager";
	public static final String SQL_SESSION = "sqlSession";
	public static final String STATUS = "status";
	public static final Object LIBERADO = "liberado";
	public static final Object EXECUTANDO = "executando";
	
	private static Logger log = LoggerFactory.getLogger(MobileContextListener.class);

	public MobileContextListener() {
	}

	public void contextInitialized(ServletContextEvent event) {

		log.info("INICIALIZAÇÃO DO CONTEXTO DA APLICAÇÃO ANTEROS MOBILE SERVER");

		try {
			MobileServerContext mobileServerContext = new MobileServerContext(new DictionaryManager());
			event.getServletContext().setAttribute(MOBILE_SERVER_CONTEXT, mobileServerContext);
			if (!mobileServerContext.isConfigured())
				log.info("ATENÇÃO - O servidor ainda não foi configurado. Acesse a tela de configuração no browser.");
			else
				mobileServerContext.initializeContext("ANTEROS_MOBILE_SERVER");
		} catch (Exception e) {
			log.info("ATENÇÃO - Ocorrreu um ERRO iniciando CONEXÃO com o BANCO de DADOS " + e.getMessage());
			e.printStackTrace();
		}
		log.info("FIM DA INICIALIZAÇÃO DO CONTEXTO DA APLICAÇÃO ANTEROS MOBILE SERVER");
	}

	public void attributeAdded(ServletContextAttributeEvent arg0) {
	}

	public void attributeReplaced(ServletContextAttributeEvent servletContextAttributeEvent) {
	}

	public void attributeRemoved(ServletContextAttributeEvent servletContextAttributeEvent) {
	}

	public void contextDestroyed(ServletContextEvent event) {
		try {
			MobileServerContext mobileServerContext = (MobileServerContext) event.getServletContext().getAttribute(
					MOBILE_SERVER_CONTEXT);
			mobileServerContext.finalizeContext();
			event.getServletContext().removeAttribute(MOBILE_SERVER_CONTEXT);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		log.info("FINALIZAÇÃO DO CONTEXTO DA APLICAÇÃO ANTEROS MOBILE SERVER");
	}
}
