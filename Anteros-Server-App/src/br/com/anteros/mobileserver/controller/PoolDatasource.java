package br.com.anteros.mobileserver.controller;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import br.com.anteros.persistence.sql.datasource.JDBCDataSource;
import br.com.anteros.persistence.sql.datasource.JNDIDataSourceFactory;
import br.com.anteros.persistence.util.ReflectionUtils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class PoolDatasource {

	public static final String POOL_C3P0 = "CP30";
	public static final String POOL_TOMCAT = "Tomcat JDBC Pool";
	public static final String POOL_JNDI = "Via JNDI";
	public static final String JDBC_WITHOUT_PO0L = "JDBC sem Pool";

	private Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

	public DataSource getDataSource(String url, String user, String password, String driverClass, int initialPoolSize,
			int acquireIncrement, int maxPoolSize, int minPoolSize, String connectionPoolType, String jndiName)
			throws Exception {

		DataSource dataSource = (DataSource) dataSources.get(connectionPoolType + "_" + url);
		if (dataSource == null) {
			if (POOL_C3P0.equals(connectionPoolType)) {
				dataSource = new ComboPooledDataSource();
				((ComboPooledDataSource) dataSource).setAcquireIncrement(acquireIncrement);
				((ComboPooledDataSource) dataSource).setAutoCommitOnClose(false);
				((ComboPooledDataSource) dataSource).setDriverClass(driverClass);
				((ComboPooledDataSource) dataSource).setIdleConnectionTestPeriod(3000);
				((ComboPooledDataSource) dataSource).setInitialPoolSize(initialPoolSize);
				((ComboPooledDataSource) dataSource).setJdbcUrl(url);
				((ComboPooledDataSource) dataSource).setMaxIdleTime(3600);
				((ComboPooledDataSource) dataSource).setUser(user);
				((ComboPooledDataSource) dataSource).setPassword(password);
				((ComboPooledDataSource) dataSource).setMaxPoolSize(maxPoolSize);
				((ComboPooledDataSource) dataSource).setMinPoolSize(minPoolSize);
				dataSources.put(connectionPoolType + "_" + url, ((ComboPooledDataSource) dataSource));
			} else if (POOL_JNDI.equals(connectionPoolType)) {
				dataSource = JNDIDataSourceFactory.getDataSource(jndiName);
			} else if (POOL_TOMCAT.equals(connectionPoolType)) {
				Class<?> propertiesClass = Class.forName("org.apache.tomcat.jdbc.pool.PoolProperties");
				Object properties = propertiesClass.newInstance();
				ReflectionUtils.invokeMethod(properties, "setUrl", url);
				ReflectionUtils.invokeMethod(properties, "setDriverClassName", driverClass);
				ReflectionUtils.invokeMethod(properties, "setUsername", user);
				ReflectionUtils.invokeMethod(properties, "setPassword", password);
				ReflectionUtils.invokeMethod(properties, "setJmxEnabled", true);
				ReflectionUtils.invokeMethod(properties, "setTestWhileIdle", false);
				ReflectionUtils.invokeMethod(properties, "setTestOnBorrow", true);
				ReflectionUtils.invokeMethod(properties, "setTestOnReturn", false);
				// ReflectionUtils.invokeMethod(properties,
				// "setValidationInterval", 30000);
				ReflectionUtils.invokeMethod(properties, "setTimeBetweenEvictionRunsMillis", 30000);
				ReflectionUtils.invokeMethod(properties, "setMaxActive", maxPoolSize);
				ReflectionUtils.invokeMethod(properties, "setInitialSize", initialPoolSize);
				ReflectionUtils.invokeMethod(properties, "setMaxWait", 10000);
				ReflectionUtils.invokeMethod(properties, "setRemoveAbandonedTimeout", 2000);
				ReflectionUtils.invokeMethod(properties, "setMinEvictableIdleTimeMillis", 30000);
				ReflectionUtils.invokeMethod(properties, "setMinIdle", 15);
				ReflectionUtils.invokeMethod(properties, "setMaxIdle", 50);
				ReflectionUtils.invokeMethod(properties, "setLogAbandoned", true);
				// ReflectionUtils.invokeMethod(properties,
				// "setRemoveAbandoned", true);
				ReflectionUtils.invokeMethod(properties, "setJdbcInterceptors",
						"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
								+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
				// ReflectionUtils.invokeMethod(properties, "defaultAutoCommit",
				// false);

				/*
				 * 
				 * 
				 * 
				 * p.setUrl("jdbc:mysql://localhost:3306/mysql");
				 * p.setDriverClassName("com.mysql.jdbc.Driver");
				 * p.setUsername("root"); p.setPassword("password");
				 * p.setJmxEnabled(true); p.setTestWhileIdle(false);
				 * p.setTestOnBorrow(true); p.setValidationQuery("SELECT 1");
				 * p.setTestOnReturn(false); p.setValidationInterval(30000);
				 * p.setTimeBetweenEvictionRunsMillis(30000);
				 * p.setMaxActive(100); p.setInitialSize(10);
				 * p.setMaxWait(10000); p.setRemoveAbandonedTimeout(60);
				 * p.setMinEvictableIdleTimeMillis(30000); p.setMinIdle(10);
				 * p.setLogAbandoned(true); p.setRemoveAbandoned(true);
				 * p.setJdbcInterceptors(
				 * "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
				 * "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer"
				 * );
				 */

				Class<?> dataSourceClass = Class.forName("org.apache.tomcat.jdbc.pool.DataSource");
				Object ds = dataSourceClass.newInstance();
				ReflectionUtils.invokeMethod(ds, "setPoolProperties", properties);
				dataSource = (DataSource) ds;
			}else if (JDBC_WITHOUT_PO0L.equals(connectionPoolType)) {
				dataSource = new JDBCDataSource(driverClass, user, password, url);
			}
		}
		return dataSource;
	}

	public void removeDataSource(DataSource dataSource) {
		for (String url : dataSources.keySet()) {
			if (dataSources.get(url) == dataSource) {
				dataSources.remove(url);
				break;
			}
		}
	}

	public void removeDataSource(String url) {
		dataSources.remove(url);
	}

	public void clear() {
		for (String url : dataSources.keySet()) {
			DataSource ds = dataSources.get(url);
			if (ds instanceof ComboPooledDataSource)
				((ComboPooledDataSource) ds).close(true);
		}
		dataSources.clear();
	}
}
