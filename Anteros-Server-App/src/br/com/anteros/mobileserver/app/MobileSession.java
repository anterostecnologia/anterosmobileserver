package br.com.anteros.mobileserver.app;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import br.com.anteros.mobile.core.synchronism.engine.SynchronismManager;
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.SQLSessionFactory;

public class MobileSession {

	private HttpSession httpSession;
	private SynchronismManager synchronismManager;
	private SQLSession sqlSessionContext;
	private Map<ApplicationSynchronism, SynchronismManager> sessionsByApplications = new HashMap<ApplicationSynchronism, SynchronismManager>();
	private MobileServerContext context;

	public MobileSession(HttpSession httpSession, MobileServerContext context) {
		this.httpSession = httpSession;
		this.context = context;
	}

	public void setSqlSessionContext(SQLSession sqlSessionContext) {
		this.sqlSessionContext = sqlSessionContext;
	}

	public MobileServerContext getContext() {
		return context;
	}

	public SynchronismManager getSynchronismManager(ApplicationSynchronism applicationSynchronism) throws Exception {
		SynchronismManager synchronismManager = sessionsByApplications.get(applicationSynchronism);
		if (synchronismManager == null) {
			SQLSessionFactory sqlSessionFactory = context.buildSessionFactory(applicationSynchronism, false);
			if (sqlSessionFactory != null) {
				synchronismManager = new SynchronismManager(sqlSessionFactory.getNewSession(),
						context.getDictionaryManager(), context.buildSessionFactory(false));
				sessionsByApplications.put(applicationSynchronism, synchronismManager);
			}
		}
		return synchronismManager;
	}

	public SynchronismManager getSynchronismManager() throws Exception {
		if (synchronismManager == null) {
			SQLSession session = getSQLSession();
			synchronismManager = new SynchronismManager(session, context.getDictionaryManager(),
					context.buildSessionFactory(false));
			httpSession.setAttribute("synchronismManager", synchronismManager);
		}
		sqlSessionContext = getSQLSession();
		return synchronismManager;
	}

	public void removeSQLSession() {
		sqlSessionContext = null;
		sessionsByApplications.clear();
	}

	public SQLSession getSQLSession() throws Exception {
		if (sqlSessionContext == null) {
			SQLSessionFactory sf = context.buildSessionFactory(false);
			sqlSessionContext = sf.getNewSession();
			httpSession.setAttribute("sqlSession", sqlSessionContext);
		}
		return sqlSessionContext;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void clearSessions() {
		sqlSessionContext = null;
		sessionsByApplications.clear();
	}

}
