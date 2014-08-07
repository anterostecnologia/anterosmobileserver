package br.com.anteros.mobileserver.app;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.mobile.core.synchronism.engine.DictionaryManager;
import br.com.anteros.mobile.core.synchronism.model.ActionSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.mobile.core.synchronism.model.FieldSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ProcedureSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobile.core.synchronism.model.TableSynchronism;
import br.com.anteros.mobileserver.controller.PoolDatasource;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.configuration.AnterosPersistenceConfiguration;
import br.com.anteros.persistence.session.configuration.AnterosPersistenceProperties;
import br.com.anteros.persistence.sql.dialect.FirebirdDialect;
import br.com.anteros.persistence.sql.dialect.H2Dialect;
import br.com.anteros.persistence.sql.dialect.MySQLDialect;
import br.com.anteros.persistence.sql.dialect.OracleDialect;
import br.com.anteros.persistence.sql.dialect.PostgreSqlDialect;

public class MobileServerContext {

	public static final String H2 = "H2";
	public static final String ORACLE = "Oracle";
	public static final String MYSQL = "MySql";
	public static final String FIREBIRD = "Firebird";
	public static final String POSTGRESQL = "PostgreSql";

	private SQLSessionFactory sessionFactory;
	private String driverClass;
	private String jdbcUrl;
	private String user;
	private String password;
	private int acquireIncrement;
	private int initialPoolSize;
	private int maxPoolSize;
	private int minPoolSize;
	private int queryTimeout;
	private boolean showSql;
	private boolean formatSql;
	private String defaultSchema;
	private String defaultCatalog;
	private String dialect;
	private String accessUser = "admin";
	private String accessPassword = "1234789";
	private String connectionPoolType = PoolDatasource.POOL_C3P0;
	private String jndiName = "";
	private PoolDatasource dataSources = new PoolDatasource();
	private Map<ApplicationSynchronism, SQLSessionFactory> sessionFactories = new HashMap<ApplicationSynchronism, SQLSessionFactory>();
	private Set<MobileSession> mobileSessions = new HashSet<MobileSession>();
	private DictionaryManager dictionaryManager;
	private static Logger log = LoggerFactory.getLogger(MobileServerContext.class);

	public MobileServerContext(DictionaryManager dictionaryManager) {
		this.dictionaryManager = dictionaryManager;
	}

	public boolean isConfigured() {
		readPreferences();
		if (("".equals(driverClass) || ("".equals(jdbcUrl)) || ("".equals(user))))
			return false;
		return true;
	}

	public boolean isConnected() {
		return (sessionFactory != null);
	}

	/**
	 * Cria e retorna uma fábrica de sessões para ser usada pela aplicação
	 * cadastrada no dicionário. Cada aplicação pode estar apontando para um
	 * banco de dados e usando pool de conexões diferentes.
	 * 
	 * @param applicationSynchronism
	 *            Aplicação
	 * @param rebuild
	 *            Recria fábrica de sessões
	 * @return Fábrica de sessões
	 * @throws Exception
	 */
	protected SQLSessionFactory buildSessionFactory(ApplicationSynchronism applicationSynchronism, boolean rebuild)
			throws Exception {
		if (rebuild)
			sessionFactories.remove(applicationSynchronism);

		SQLSessionFactory sqlSessionFactory = sessionFactories.get(applicationSynchronism);
		if (sqlSessionFactory == null) {
			if (!StringUtils.isEmpty(applicationSynchronism.getJdbcUrl())) {
				DataSource dataSource = dataSources.getDataSource(applicationSynchronism.getJdbcUrl(),
						applicationSynchronism.getUser(), applicationSynchronism.getPassword(), applicationSynchronism
								.getDriverClass(), (applicationSynchronism.getInitialPoolSize() == null ? 5
								: applicationSynchronism.getInitialPoolSize().intValue()), (applicationSynchronism
								.getAcquireIncrement() == null ? 2 : applicationSynchronism.getAcquireIncrement()
								.intValue()), (applicationSynchronism.getMaxPoolSize() == null ? 100
								: applicationSynchronism.getMaxPoolSize().intValue()), (applicationSynchronism
								.getMinPoolSize() == null ? 5 : applicationSynchronism.getMinPoolSize().intValue()),
						applicationSynchronism.getConnectionPoolType(), applicationSynchronism.getJndiName());

				Class<?> dialectClass = null;
				if (MobileServerContext.H2.equals(applicationSynchronism.getDialect()))
					dialectClass = H2Dialect.class;
				else if (MobileServerContext.ORACLE.equals(applicationSynchronism.getDialect()))
					dialectClass = OracleDialect.class;
				else if (MobileServerContext.MYSQL.equals(applicationSynchronism.getDialect()))
					dialectClass = MySQLDialect.class;
				else if (MobileServerContext.FIREBIRD.equals(applicationSynchronism.getDialect()))
					dialectClass = FirebirdDialect.class;
				else if (MobileServerContext.POSTGRESQL.equals(applicationSynchronism.getDialect()))
					dialectClass = PostgreSqlDialect.class;

				sqlSessionFactory = new AnterosPersistenceConfiguration(dataSource)
						.addProperty(AnterosPersistenceProperties.DIALECT, dialectClass.getName())
						.addProperty(AnterosPersistenceProperties.SHOW_SQL, "true")//String.valueOf(showSql))
						.addProperty(AnterosPersistenceProperties.FORMAT_SQL, "true")//String.valueOf(formatSql))
						.addProperty(AnterosPersistenceProperties.JDBC_CATALOG,
								(applicationSynchronism.getDefaultCatalog() == null ? "" : applicationSynchronism
										.getDefaultCatalog()))
						.addProperty(AnterosPersistenceProperties.JDBC_SCHEMA,
								applicationSynchronism.getDefaultSchema())
						.addProperty(AnterosPersistenceProperties.QUERY_TIMEOUT, queryTimeout + "")
						.addProperty(AnterosPersistenceProperties.CONNECTION_CLIENTINFO,
								applicationSynchronism.getName())
						.buildSessionFactory();
				sessionFactories.put(applicationSynchronism, sqlSessionFactory);
			} else
				log.error("Ocorreu um erro inicializando pool de conexões da aplicação "
						+ applicationSynchronism.getName() + ". Verifique as configurações da aplicação.");
		}
		return sqlSessionFactory;
	}

	/**
	 * Cria e retorna uma fábrica de sessões. Esta fábrica vai criar sessões
	 * para o dicionário de dados que pode estar em servidor diferente do
	 * servidor onde está os dados da aplicação.
	 * 
	 * @param rebuild
	 *            Recria a fábrica
	 * @return Fábrica de sessões
	 * @throws Exception
	 */
	protected SQLSessionFactory buildSessionFactory(boolean rebuild) throws Exception {
		if (rebuild)
			sessionFactory = null;

		if (isConfigured()) {
			if (sessionFactory == null) {
				DataSource dataSource = dataSources.getDataSource(this.jdbcUrl, user, password, driverClass,
						initialPoolSize, acquireIncrement, maxPoolSize, minPoolSize, connectionPoolType, jndiName);
				Class<?> dialectClass = null;
				if (MobileServerContext.H2.equals(dialect))
					dialectClass = H2Dialect.class;
				else if (MobileServerContext.ORACLE.equals(dialect))
					dialectClass = OracleDialect.class;
				else if (MobileServerContext.MYSQL.equals(dialect))
					dialectClass = MySQLDialect.class;
				else if (MobileServerContext.FIREBIRD.equals(dialect))
					dialectClass = FirebirdDialect.class;
				else if (MobileServerContext.POSTGRESQL.equals(dialect))
					dialectClass = PostgreSqlDialect.class;

				sessionFactory = new AnterosPersistenceConfiguration(dataSource).addAnnotatedClass(Synchronism.class)
						.addAnnotatedClass(ActionSynchronism.class).addAnnotatedClass(ApplicationSynchronism.class)
						.addAnnotatedClass(FieldSynchronism.class).addAnnotatedClass(ParameterSynchronism.class)
						.addAnnotatedClass(ProcedureSynchronism.class).addAnnotatedClass(TableSynchronism.class)
						.addProperty(AnterosPersistenceProperties.DIALECT, dialectClass.getName())
						.addProperty(AnterosPersistenceProperties.SHOW_SQL, "true")//String.valueOf(showSql))
						.addProperty(AnterosPersistenceProperties.FORMAT_SQL, "true")//String.valueOf(formatSql))
						.addProperty(AnterosPersistenceProperties.JDBC_CATALOG, defaultCatalog)
						.addProperty(AnterosPersistenceProperties.JDBC_SCHEMA, defaultSchema)
						.addProperty(AnterosPersistenceProperties.QUERY_TIMEOUT, queryTimeout + "")
						.addProperty(AnterosPersistenceProperties.CONNECTION_CLIENTINFO, "Anteros-MobileServer")
						.buildSessionFactory();
			}
		}
		return sessionFactory;
	}

	public void readPreferences() {
		Preferences prefsRoot = Preferences.userRoot();
		Preferences anterosPrefs = prefsRoot.node("anteros/mobile/server/preferences");
		driverClass = anterosPrefs.get("driverClass", "");
		jdbcUrl = anterosPrefs.get("jdbcUrl", "");
		user = anterosPrefs.get("user", "");
		password = anterosPrefs.get("password", "");
		acquireIncrement = anterosPrefs.getInt("acquireIncrement", 2);
		initialPoolSize = anterosPrefs.getInt("initialPoolSize", 5);
		maxPoolSize = anterosPrefs.getInt("maxPoolSize", 50);
		minPoolSize = anterosPrefs.getInt("minPoolSize", 5);

		showSql = anterosPrefs.getBoolean("showSql", false);
		dialect = anterosPrefs.get("dialect", "H2");
		formatSql = anterosPrefs.getBoolean("formatSql", false);
		defaultSchema = anterosPrefs.get("defaultSchema", "");
		defaultCatalog = anterosPrefs.get("defaultCatalog", "");
		accessUser = anterosPrefs.get("accessUser", "admin");
		accessPassword = anterosPrefs.get("accessPassword", "1234789");
		connectionPoolType = anterosPrefs.get("connectionPoolType", PoolDatasource.POOL_C3P0);
		jndiName = anterosPrefs.get("jndiName", "");
		queryTimeout = anterosPrefs.getInt("queryTimeout", 20);
	}

	public void writePreferences(String jdbcUrl, String user, String password, int acquireIncrement,
			int initialPoolSize, int maxPoolSize, int minPoolSize, boolean showSql, String dialect, boolean formatSql,
			String defaultSchema, String defaultCatalog, String accessUser, String accessPassword,
			String connectionPoolType, String jndiName, int queryTimeout) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.acquireIncrement = acquireIncrement;
		this.initialPoolSize = initialPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.minPoolSize = minPoolSize;
		this.showSql = showSql;
		this.dialect = dialect;
		this.formatSql = formatSql;
		this.defaultSchema = defaultSchema;
		this.defaultCatalog = defaultCatalog;
		this.accessUser = accessUser;
		this.accessPassword = accessPassword;
		this.connectionPoolType = connectionPoolType;
		this.jndiName = jndiName;
		this.queryTimeout = queryTimeout;

		Preferences prefsRoot = Preferences.userRoot();
		Preferences anterosPrefs = prefsRoot.node("anteros/mobile/server/preferences");

		if ("H2".equals(dialect))
			this.driverClass = org.h2.Driver.class.getName();
		else if ("Oracle".equals(dialect))
			this.driverClass = oracle.jdbc.driver.OracleDriver.class.getName();
		else if ("MySql".equals(dialect))
			this.driverClass = com.mysql.jdbc.Driver.class.getName();
		else if ("Firebird".equals(dialect))
			this.driverClass = org.firebirdsql.jdbc.FBDriver.class.getName();
		else if ("PostgreSql".equals(dialect))
			this.driverClass = org.postgresql.Driver.class.getName();

		anterosPrefs.put("driverClass", this.driverClass);
		anterosPrefs.put("jdbcUrl", this.jdbcUrl);
		anterosPrefs.put("user", this.user);
		anterosPrefs.put("password", this.password);
		anterosPrefs.putInt("acquireIncrement", this.acquireIncrement);
		anterosPrefs.putInt("initialPoolSize", this.initialPoolSize);
		anterosPrefs.putInt("maxPoolSize", this.maxPoolSize);
		anterosPrefs.putInt("minPoolSize", this.minPoolSize);
		anterosPrefs.putInt("queryTimeout", this.queryTimeout);

		anterosPrefs.putBoolean("showSql", this.showSql);
		anterosPrefs.put("dialect", this.dialect);
		anterosPrefs.putBoolean("formatSql", this.formatSql);
		anterosPrefs.put("defaultSchema", this.defaultSchema);
		if (!dialect.equals("Oracle"))
			anterosPrefs.put("defaultCatalog", this.defaultCatalog);
		anterosPrefs.put("accessUser", this.accessUser);
		anterosPrefs.put("accessPassword", this.accessPassword);
		anterosPrefs.put("connectionPoolType", this.connectionPoolType);
		anterosPrefs.put("jndiName", this.jndiName);
	}

	public MobileSession getMobileSession(HttpSession httpSession) {
		for (MobileSession session : mobileSessions) {
			if (session.getHttpSession().equals(httpSession))
				return session;
		}
		MobileSession newSession = new MobileSession(httpSession, this);
		mobileSessions.add(newSession);
		return newSession;
	}

	public MobileSession removeMobileSession(HttpSession httpSession) {
		for (MobileSession session : mobileSessions) {
			if (session.getHttpSession().equals(httpSession))
				mobileSessions.remove(session);
			return session;
		}
		return null;
	}

	public DictionaryManager getDictionaryManager() throws Exception {
		if (dictionaryManager.getSqlSession() == null)
			dictionaryManager.setSqlSession(buildSessionFactory(false).getCurrentSession());
		return dictionaryManager;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getAcquireIncrement() {
		return acquireIncrement;
	}

	public void setAcquireIncrement(int acquireIncrement) {
		this.acquireIncrement = acquireIncrement;
	}

	public int getInitialPoolSize() {
		return initialPoolSize;
	}

	public void setInitialPoolSize(int initialPoolSize) {
		this.initialPoolSize = initialPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public boolean isFormatSql() {
		return formatSql;
	}

	public void setFormatSql(boolean formatSql) {
		this.formatSql = formatSql;
	}

	public String getDefaultSchema() {
		return defaultSchema;
	}

	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}

	public String getDefaultCatalog() {
		return defaultCatalog;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public String getAccessUser() {
		return accessUser;
	}

	public void setAccessUser(String accessUser) {
		this.accessUser = accessUser;
	}

	public String getAccessPassword() {
		return accessPassword;
	}

	public void setAccessPassword(String accessPassword) {
		this.accessPassword = accessPassword;
	}

	public void initializeContext(String clientId) throws Exception {
		buildSessionFactory(false);
		List<ApplicationSynchronism> allApplications = getDictionaryManager().getAllApplications(clientId);
		if (allApplications != Collections.EMPTY_LIST) {
			for (ApplicationSynchronism app : allApplications)
				buildSessionFactory(app, false);
		}
	}

	public void finalizeContext() {
		sessionFactories.clear();
		sessionFactory = null;
		dataSources.clear();
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getConnectionPoolType() {
		return connectionPoolType;
	}

	public void setConnectionPoolType(String connectionPoolType) {
		this.connectionPoolType = connectionPoolType;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

}
