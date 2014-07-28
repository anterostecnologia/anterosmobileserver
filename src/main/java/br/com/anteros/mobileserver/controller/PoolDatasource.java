package br.com.anteros.mobileserver.controller;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.persistence.sql.datasource.JDBCDataSource;
import br.com.anteros.persistence.sql.datasource.JNDIDataSourceFactory;

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
				((ComboPooledDataSource) dataSource).setUser(user);
				((ComboPooledDataSource) dataSource).setPassword(password);
				((ComboPooledDataSource) dataSource).setDriverClass(driverClass);
				((ComboPooledDataSource) dataSource).setJdbcUrl(url);
				((ComboPooledDataSource) dataSource).setAutoCommitOnClose(false);

				/*
				 * # Número de conexõs que o pool tentará adiquirur durante a
				 * inicialização. Deve ser um número entre minPoolSize e
				 * maxPoolSize.
				 */
				((ComboPooledDataSource) dataSource).setInitialPoolSize(initialPoolSize);
				/*
				 * # Número mínimo de conexões que o pool irá manter.
				 */
				((ComboPooledDataSource) dataSource).setMinPoolSize(minPoolSize);
				/*
				 * # Número máximo de conexões que o pool irá manter.
				 */
				((ComboPooledDataSource) dataSource).setMaxPoolSize(maxPoolSize);
				/*
				 * # Segundos que uma Conexão será mantida no pool sem ser
				 * usada, antes de ser descartada. Zero significa que a conexão
				 * nunca expira.
				 */
				((ComboPooledDataSource) dataSource).setMaxIdleTime(300);
				/*
				 * # O tamanho do cache do C3P0 para PreparedStatements. Se o
				 * valor de ambos, maxStatements e maxStatementsPerConnection, é
				 * zero, o cache será desabilitado. Se maxStatements é zero mas
				 * maxStatementsPerConnection é um valor diferente de zero, o
				 * cache será habilitado, mas sem um limite global, apenas com
				 * um limite por conexão. maxStatements controla o número total
				 * de Statements dos quais é feito cache, para todas as
				 * conexões. Se setado, deve ser um valor relativamente alto, já
				 * que cada Conexão do pool terá um determinado número de
				 * statements colocado em cache. Como um exemplo, considere
				 * quantos PreparedStatements distintos são frequentemente
				 * usados na sua aplicação e multiplique esse número por
				 * maxPoolSize para chegar num valor apropriado. Apesar do
				 * parâmetro maxStatements ser o padrão para o JDBC controlar o
				 * cache de statements, usuários podem achar mais intuitivo o
				 * uso do parâmetro maxStatementsPerConnection.
				 */
				((ComboPooledDataSource) dataSource).setMaxStatements(0);
				/*
				 * # O número de PreparedStatements que o c3p0 irá colocar em
				 * cache, para cada conexão do pool. Se ambos maxStatements e
				 * maxStatementsPerConnection são zero, o cache de consultas
				 * ficará inativo. Se maxStatementsPerConnection é zero, mas
				 * maxStatements é um valor não nulo, o cache de consultas será
				 * habilitado, e um limite global imposto, mas por outro lado,
				 * não existirá nenhum limite individual por conexão. Se setado,
				 * maxStatementsPerConnection deveria ser um valor, aproximado,
				 * do número de PreparedStatements, distintos, que são
				 * frequentemente usados na sua aplicação mais dois ou três,
				 * para que as consultas menos comuns não tirem as mais comuns
				 * do cache. Apesar de maxStatements ser o parâmetro padrão em
				 * JDBC para controlar o cache de consultas, o usuário pode
				 * achar mais intuitivo usar o parâmetro
				 * maxStatementsPerConnection.
				 */
				((ComboPooledDataSource) dataSource).setMaxStatementsPerConnection(10);
				/*
				 * # Determina quantas conexões por vez o c3p0 tenta adquirir
				 * quando o pool não tem conexões inativas para serem usadas.
				 */
				((ComboPooledDataSource) dataSource).setAcquireIncrement(acquireIncrement);
				/*
				 * # Se idleConnectionTestPeriod é um número maior que zero,
				 * c3p0 irá testar todas as conexões inativas, que estão no pool
				 * e não fizeram o check-out, de X em X segundos, onde X é o
				 * valor de idleConnectionTestPeriod.
				 */
				((ComboPooledDataSource) dataSource).setIdleConnectionTestPeriod(3000);
				/*
				 * # O número de milisegundos que um cliente chamando
				 * getConnection() irá esperar por uma Conexão, via check-in ou
				 * uma nova conexão adquirida quando o pool estiver esgotado.
				 * Zero siginifica esperar indefinidademento. Setar qualquer
				 * valor positivo causará um time-out com uma SQLException
				 * depois de passada a quantidade especificada de milisegundos.
				 */
				((ComboPooledDataSource) dataSource).setCheckoutTimeout(5000);
				/*
				 * # Tempo em milisegundos que o c3p0 irá esperar entre tentivas
				 * de aquisição.
				 */
				((ComboPooledDataSource) dataSource).setAcquireRetryDelay(1000);
				/*
				 * # Define quantas vezes o c3p0 tentará adquirir uma nova
				 * Conexão da base de dados antes de desistir. Se esse valor é
				 * menor ou igual a zero, c3p0 tentará adquirir uma nova conexão
				 * indefinidamente.
				 */
				((ComboPooledDataSource) dataSource).setAcquireRetryAttempts(5);
				/*
				 * # Se true, um pooled DataSource declarará a si mesmo quebrado
				 * e ficará permanentemente fechado caso não se consiga uma
				 * Conexão do banco depois de tentar acquireRetryAttempts vezes.
				 * Se falso, o fracasso para obter uma Conexão jogará uma
				 * exceção, porém o DataSource permanecerá valido, e tentará
				 * adquirir novamente, seguindo uma nova chamada para
				 * getConnection().
				 */
				((ComboPooledDataSource) dataSource).setBreakAfterAcquireFailure(false);
				/*
				 * # Número de segundos que conexões acima do limite minPoolSize
				 * deverão permanecer inativas no pool antes de serem fechadas.
				 * Destinado para aplicações que desejam reduzir agressivamente
				 * o número de conexões abertas, diminuindo o pool novamente
				 * para minPoolSize, se, seguindo um pico, o nível de load
				 * diminui e Conexões não são mais requeridas. Se maxIdleTime
				 * está definido, maxIdleTimeExcessConnections deverá ser um
				 * valor menor para que o parâmetro tenho efeito. Zero significa
				 * que não existirá nenhuma imposição, Conexões em excesso não
				 * serão mais fechadas.
				 */
				((ComboPooledDataSource) dataSource).setMaxIdleTimeExcessConnections(10);
				/*
				 * # c3p0 é muito assíncrono. Operações JDBC lentas geralmente
				 * são executadas por helper threads que não detém travas de
				 * fechamento. Separar essas operações atravéz de múltiplas
				 * threads pode melhorar significativamente a performace,
				 * permitindo que várias operações sejam executadas ao mesmo
				 * tempo.
				 */
				((ComboPooledDataSource) dataSource).setNumHelperThreads(3);
				/*
				 * # Se true, e se unreturnedConnectionTimeout está definido com
				 * um valor positivo, então o pool capturará a stack trace (via
				 * uma exceção) de todos os checkouts de Conexões, e o stack
				 * trace será impresso quando o checkout de Conexões der
				 * timeout. Este paramêtro é destinado para debug de aplicações
				 * com leak de Conexões, isto é, aplicações que ocasionalmente
				 * falham na liberação/fechamento de Conexões, ocasionando o
				 * crescimento do pool, e eventualmente na sua exaustão (quando
				 * o pool atinge maxPoolSize com todas as suas conexões em uso e
				 * perdidas). Este paramêtro deveria ser setado apenas para
				 * debugar a aplicação, já que capturar o stack trace deixa mais
				 * o lento o precesso de check-out de Conexões.
				 */
				((ComboPooledDataSource) dataSource).setDebugUnreturnedConnectionStackTraces(false);
				/*
				 * # Segundos. Se setado, quando uma aplicação realiza o
				 * check-out e falha na realização do check-in [i.e. close()] de
				 * um Conexão, dentro de período de tempo especificado, o pool
				 * irá, sem cerimonias, destruir a conexão [i.e. destroy()].
				 * Isto permite que aplicações com ocasionais leaks de conexão
				 * sobrevivam, ao invéz de exaurir o pool. E Isto é uma pena.
				 * Zero significa sem timeout, aplicações deveriam fechar suas
				 * próprias Conexões. Obviamente, se um valor positivo é
				 * definido, este valor deve ser maior que o maior valor que uma
				 * conexão deveria permanecer em uso. Caso contrário, o pool irá
				 * ocasionalmente matar conexões ativas, o que é ruim. Isto
				 * basicamente é uma péssima idéia, porém é uma funcionalidade
				 * pedida com frequência. Consertem suas aplicações para que não
				 * vazem Conexões!!! Use esta funcionalidade temporariamente em
				 * combinação com debugUnreturnedConnectionStackTraces para
				 * descobrir onde as conexões esão vazando!
				 */
				((ComboPooledDataSource) dataSource).setUnreturnedConnectionTimeout(0);
			} else if (POOL_JNDI.equals(connectionPoolType)) {
				dataSource = JNDIDataSourceFactory.getDataSource(jndiName);
			} else if (POOL_TOMCAT.equals(connectionPoolType)) {
				Class<?> propertiesClass = Class.forName("org.apache.tomcat.jdbc.pool.PoolProperties");
				Object properties = propertiesClass.newInstance();
				ReflectionUtils.invokeMethod(properties, "setUrl", url);
				ReflectionUtils.invokeMethod(properties, "setDriverClassName", driverClass);
				ReflectionUtils.invokeMethod(properties, "setUsername", user);
				ReflectionUtils.invokeMethod(properties, "setPassword", password);
				/*
				 * If set to true, the connection pool creates a
				 * ConnectionPoolMBean object that can be registered with JMX to
				 * receive notifications and state about the pool.
				 */
				ReflectionUtils.invokeMethod(properties, "setJmxEnabled", true);
				/*
				 * Set to true if query validation should take place while the
				 * connection is idle.
				 */
				ReflectionUtils.invokeMethod(properties, "setTestWhileIdle", false);
				/*
				 * The indication of whether objects will be validated before
				 * being borrowed from the pool.
				 */
				ReflectionUtils.invokeMethod(properties, "setTestOnBorrow", true);
				/*
				 * The indication of whether objects will be validated after
				 * being returned to the pool.
				 */
				ReflectionUtils.invokeMethod(properties, "setTestOnReturn", true);
				/*
				 * The number of milliseconds to sleep between runs of the idle
				 * connection validation, abandoned cleaner and idle pool
				 * resizing.
				 */
				ReflectionUtils.invokeMethod(properties, "setTimeBetweenEvictionRunsMillis", 30000);
				/*
				 * The maximum number of active connections that can be
				 * allocated from this pool at the same time.
				 */
				ReflectionUtils.invokeMethod(properties, "setMaxActive", maxPoolSize);
				/*
				 * Set the number of connections that will be established when
				 * the connection pool is started.
				 */
				ReflectionUtils.invokeMethod(properties, "setInitialSize", initialPoolSize);
				/*
				 * The maximum number of milliseconds that the pool will wait
				 * (when there are no available connections and the
				 * PoolConfiguration.getMaxActive() has been reached) for a
				 * connection to be returned before throwing an exception.
				 */
				ReflectionUtils.invokeMethod(properties, "setMaxWait", 10000);
				/*
				 * The time in seconds before a connection can be considered
				 * abandoned.
				 */
				ReflectionUtils.invokeMethod(properties, "setRemoveAbandonedTimeout", 300);
				/*
				 * The minimum amount of time an object must sit idle in the
				 * pool before it is eligible for eviction.
				 */
				ReflectionUtils.invokeMethod(properties, "setMinEvictableIdleTimeMillis", 60000);
				/*
				 * The minimum number of established connections that should be
				 * kept in the pool at all times.
				 */
				ReflectionUtils.invokeMethod(properties, "setMinIdle", minPoolSize);
				/*
				 * The maximum number of connections that should be kept in the idle pool if PoolConfiguration.isPoolSweeperEnabled() returns false.
				 */
				ReflectionUtils.invokeMethod(properties, "setMaxIdle", minPoolSize);

				ReflectionUtils.invokeMethod(properties, "setLogAbandoned", true);
				/*
				 * boolean flag to remove abandoned connections if they exceed
				 * the removeAbandonedTimout.
				 */
				ReflectionUtils.invokeMethod(properties, "setRemoveAbandoned", true);
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
			} else if (JDBC_WITHOUT_PO0L.equals(connectionPoolType)) {
				dataSource = new JDBCDataSource(driverClass, user, password, url);
			}
			dataSources.put(connectionPoolType + "_" + url, dataSource);
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
