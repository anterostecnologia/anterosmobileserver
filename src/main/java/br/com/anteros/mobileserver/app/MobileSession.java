package br.com.anteros.mobileserver.app;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import br.com.anteros.mobile.core.synchronism.engine.SynchronismManager;
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.mobileserver.listener.MobileContextListener;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.SQLSessionFactory;

public class MobileSession {

	private HttpSession httpSession;
	private SynchronismManager synchronismManager;
	private Map<ApplicationSynchronism, SynchronismManager> sessionsByApplications = new HashMap<ApplicationSynchronism, SynchronismManager>();
	private MobileServerContext context;

	public MobileSession(HttpSession httpSession, MobileServerContext context) {
		this.httpSession = httpSession;
		this.context = context;
	}

	public MobileServerContext getContext() {
		return context;
	}

	public SynchronismManager getSynchronismManager(ApplicationSynchronism applicationSynchronism) throws Exception {
		SynchronismManager synchronismManager = sessionsByApplications.get(applicationSynchronism);
		if (synchronismManager == null) {
			SQLSessionFactory sqlSessionFactory = context.buildSessionFactory(applicationSynchronism, false);
			synchronismManager = new SynchronismManager(sqlSessionFactory.getCurrentSession(),
					context.getDictionaryManager(), sqlSessionFactory);
			sessionsByApplications.put(applicationSynchronism, synchronismManager);
		}
		return synchronismManager;
	}

	public SynchronismManager getSynchronismManager() throws Exception {
		if (synchronismManager == null) {
			SQLSessionFactory sf = context.buildSessionFactory(false);
			SQLSession session = sf.openSession();
			httpSession.setAttribute(MobileContextListener.SQL_SESSION, session);
			synchronismManager = new SynchronismManager(session, context.getDictionaryManager(), sf);
			httpSession.setAttribute(MobileContextListener.SYNCHRONISM_MANAGER, synchronismManager);
		}
		return synchronismManager;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void clearSessions() {
		sessionsByApplications.clear();
	}

	public void closeSessions() {
		for (SynchronismManager sm : sessionsByApplications.values()) {
			try {
				sm.closeSession();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			synchronismManager.closeSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
