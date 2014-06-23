package br.com.anteros.mobileserver.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.anteros.mobile.core.communication.HttpConnectionServer;
import br.com.anteros.mobile.core.protocol.MobileRequest;
import br.com.anteros.mobile.core.protocol.MobileResponse;
import br.com.anteros.mobile.core.synchronism.engine.SynchronismManager;
import br.com.anteros.mobile.core.synchronism.exception.ActionNotFoundException;
import br.com.anteros.mobile.core.synchronism.exception.ApplicationNotFoundException;
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.mobileserver.app.MobileServerContext;
import br.com.anteros.mobileserver.app.MobileSession;
import br.com.anteros.mobileserver.listener.MobileContextListener;

public class MobileServletController extends HttpConnectionServer {

	
	private static Logger log = LoggerFactory.getLogger(MobileServletController.class);
	private static final long serialVersionUID = 1L;

	@Override
	public MobileResponse executeAction(HttpSession session, MobileRequest mobileRequest, HttpServletRequest request,
			HttpServletResponse response) {
		
		response.addHeader("Connection", "Keep-Alive");
		response.addHeader("Keep-Alive", "timeout=120000");
		if (session.getAttribute(MobileContextListener.STATUS) != null) {
			if (!((String) session.getAttribute(MobileContextListener.STATUS)).equals(MobileContextListener.LIBERADO)) {
				log.debug("A execução da sua última requisição a este servidor está sendo processada. Por favor aguarde um tempo e tente novamente. Caso não tenha sucesso em suas próximas tentativas favor contactar o Administrador do Sistema."
						+ " ##" + mobileRequest.getClientId());
				MobileResponse mobileResponse = new MobileResponse();
				mobileResponse
						.setStatus("A execução da sua última requisição a este servidor está sendo processada. Por favor aguarde um tempo e tente novamente. Caso não tenha sucesso em suas próximas tentativas favor contactar o Administrador do Sistema.");
				return mobileResponse;
			}
		}

		log.info("Id da Sessão: " + session.getId() + " ##" + mobileRequest.getClientId());

		MobileServerContext mobileServerContext = (MobileServerContext) session.getServletContext().getAttribute(
				"mobileServerContext");
		if (!mobileServerContext.isConfigured()) {
			log.info("ATENÇÃO - O servidor ainda não foi configurado. Acesse a tela de configuração no browser.");
			MobileResponse mobileResponse = new MobileResponse();
			mobileResponse
					.setStatus("ATENÇÃO - O servidor ainda não foi configurado. Acesse a tela de configuração no browser.");
			return mobileResponse;
		}		

		log.info(session.getId() + " Requisição recebida" + " ##" + mobileRequest.getClientId());
		log.info(mobileRequest.toString() + " ##" + mobileRequest.getClientId());

		MobileResponse mobileResponse = new MobileResponse();

		/*
		 * Obtém a MobileSession da Sessão do Usuário
		 */
		MobileSession mobileSession = null;
		try {
			mobileSession = this.getMobileSession(session);
		} catch (Exception ex) {
			log.error(new StringBuffer().append(session.getId()).append(" Erro requisição -> ").append(" ##")
					.append(mobileRequest.getClientId()).toString());
			mobileResponse.setStatus(ex.getMessage());
		}

		if (mobileSession != null) {
			/*
			 * Obtém o SynchronismManager da sessão do usuário
			 */
			
			

			SynchronismManager synchronismManager = null;
			try {
				ApplicationSynchronism app = mobileServerContext.getDictionaryManager().getApplicationByName(mobileRequest.getApplication(), mobileRequest.getClientId());
				synchronismManager = mobileSession.getSynchronismManager(app);
			} catch (Exception e) {
				log.error("Ocorreu um erro obtendo o objeto de sincronismo." + e.getMessage() + " ##"
						+ mobileRequest.getClientId());
				mobileResponse.setStatus(e.getMessage());
				e.printStackTrace();
				return mobileResponse;
			}

			try {
				session.setAttribute(MobileContextListener.STATUS, MobileContextListener.EXECUTANDO);
				mobileResponse = synchronismManager.executeRequest(mobileRequest);
				log.info(session.getId() + " Resposta a ser enviada" + " ##" + mobileRequest.getClientId());
				mobileResponse.showDetails();
				if (mobileResponse.getStatus().startsWith(MobileResponse.OK)) {
					log.info(new StringBuffer().append(session.getId())
							.append(" Fim requisição - Executou -> C O M M I T").append(" ##")
							.append(mobileRequest.getClientId()).toString());
					synchronismManager.getSqlSession().commit();
				} else {
					log.info(new StringBuffer().append("  ").append(session.getId())
							.append(" Fim requisição - Executou -> R O L L B A C K").append(" ##")
							.append(mobileRequest.getClientId()).toString());
					synchronismManager.getSqlSession().rollback();
				}

			} catch (ApplicationNotFoundException ex) {
				log.error(
						new StringBuffer().append(" ").append(session.getId()).append(" Erro requisição -> ")
								.append(" ##").append(mobileRequest.getClientId()).toString(), ex);
				mobileResponse.setStatus(ex.getMessage());
				try {
					synchronismManager.getSqlSession().rollback();
				} catch (Exception e) {
				}
				log.info(new StringBuffer().append(" ").append(session.getId())
						.append(" Fim requisição - Executou -> R O L L B A C K").append(" ##")
						.append(mobileRequest.getClientId()).toString());
			} catch (ActionNotFoundException ex) {
				log.error(
						new StringBuffer().append(" ").append(session.getId()).append(" Erro requisição -> ")
								.append(" ##").append(mobileRequest.getClientId()).toString(), ex);
				mobileResponse.setStatus(ex.getMessage());
				try {
					synchronismManager.getSqlSession().rollback();
				} catch (Exception e) {
				}
				log.info(new StringBuffer().append(" ").append(session.getId())
						.append(" Fim requisição - Executou -> R O L L B A C K").append(" ##")
						.append(mobileRequest.getClientId()).toString());
			} catch (Exception ex) {
				log.error(
						new StringBuffer().append(" ").append(session.getId()).append(" Erro requisição -> ")
								.append(" ##").append(mobileRequest.getClientId()).toString(), ex);
				mobileResponse.setStatus(new StringBuffer(" Erro executando Requisição - Exceção: ").append(
						ex.getMessage()).toString());
				try {
					synchronismManager.getSqlSession().rollback();
				} catch (Exception e) {
				}
				log.info(new StringBuffer().append(" ").append(session.getId())
						.append(" Fim requisição - Executou -> R O L L B A C K").append(" ##")
						.append(mobileRequest.getClientId()).toString());
			} finally {
				session.setAttribute(MobileContextListener.STATUS, MobileContextListener.LIBERADO);
			}
		}
		return mobileResponse;
	}

	public MobileSession getMobileSession(HttpSession session) throws Exception {
		synchronized (session) {
			MobileServerContext mobileServerContext = (MobileServerContext) this.getServletContext().getAttribute(
					MobileContextListener.MOBILE_SERVER_CONTEXT);
			return mobileServerContext.getMobileSession(session);
		}
	}

}
