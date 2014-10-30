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
package br.com.anteros.mobileserver.app;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import br.com.anteros.mobile.core.protocol.MobileAction;
import br.com.anteros.mobile.core.protocol.MobileRequest;
import br.com.anteros.mobile.core.protocol.MobileResponse;
import br.com.anteros.mobile.core.synchronism.engine.SynchronismManager;
import br.com.anteros.mobile.core.synchronism.exception.ActionNotFoundException;
import br.com.anteros.mobile.core.synchronism.exception.ApplicationNotFoundException;
import br.com.anteros.mobile.core.synchronism.model.ActionSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.mobile.core.synchronism.model.FieldSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ProcedureSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobile.core.synchronism.model.TableSynchronism;
import br.com.anteros.mobileserver.util.UserMessages;
import br.com.anteros.persistence.schema.definition.StoredFunctionSchema;
import br.com.anteros.persistence.schema.definition.StoredParameterSchema;
import br.com.anteros.persistence.schema.definition.StoredProcedureSchema;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.transaction.Transaction;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;

@SuppressWarnings("unchecked")
public class MobileServerData {

	public static final Object PROPERTY_NAME = "name";
	public static final Object PROPERTY_ICON = "icon";
	public static final Object PROPERTY_DATA = "data";

	public static final String ACTION_QUEUE = "AQ";
	public static final String ACTION_EXECUTE_QUEUE = "AE";
	public static final String ACTION_EXECUTE_IMMEDIATE = "AI";

	public static final ThemeResource APPLICATION_IMG = new ThemeResource("icons/16/application.png");
	public static final ThemeResource ACTION_IMG = new ThemeResource("icons/16/action.png");
	public static final ThemeResource TABLE_IMG = new ThemeResource("icons/16/table.png");
	public static final ThemeResource PROCEDURE_IMG = new ThemeResource("icons/16/procedure.png");
	public static final ThemeResource FIELD_IMG = new ThemeResource("icons/16/field.png");
	public static final ThemeResource FIELDS_IMG = new ThemeResource("icons/16/fields.png");
	public static final ThemeResource PARAMETER_IMG = new ThemeResource("icons/16/parameter.png");
	public static final ThemeResource PARAMETERS_IMG = new ThemeResource("icons/16/parameters.png");

	public static final Long ID_TABLE_FIELDS = new Long(2000000);
	public static final Long ID_TABLE_PARAMETERS = new Long(3000000);
	public static final Long ID_PROCEDURE_PARAMETERS = new Long(4000000);

	public static void reloadServer(MobileServerApplication application) {
		try {
			SynchronismManager synchronismManager = getMobileSession(application).getSynchronismManager();
			synchronismManager.clearDictionary();
		} catch (Exception e) {
			application.getMainWindow().showNotification("Ocorreu um erro recarregando servidor. " + e.getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static HierarchicalContainer getApplications(MobileServerApplication application) {
		Item item = null;
		HierarchicalContainer hwContainer = null;
		try {
			SQLSession sqlSession = getSQLSession(application);
			List<ApplicationSynchronism> applications = null;
			applications = (List<ApplicationSynchronism>) sqlSession.createQuery(
					"SELECT * FROM MOBILE_OBJETO WHERE TP_OBJETO = 'APLICACAO' ORDER BY NOME_OBJETO",
					ApplicationSynchronism.class).getResultList();
			hwContainer = new HierarchicalContainer();

			hwContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
			hwContainer.addContainerProperty(PROPERTY_ICON, ThemeResource.class, APPLICATION_IMG);
			hwContainer.addContainerProperty(PROPERTY_DATA, Object.class, null);

			if (applications != null) {
				for (ApplicationSynchronism app : applications) {
					hwContainer.removeItem(app.getId());
					item = hwContainer.addItem(app.getId());
					item.getItemProperty(PROPERTY_NAME).setValue(app.getName() + " " + app.getId());
					item.getItemProperty(PROPERTY_DATA).setValue(app);
					hwContainer.setChildrenAllowed(app.getId(), true);
				}
			}

		} catch (Exception e) {
			application.getMainWindow().showNotification("Ocorreu um erro lendo as Aplicações " + e.getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		return hwContainer;
	}

	public static void loadActions(MobileServerApplication application, Item itemToLoad) {
		Item item = null;

		List<ActionSynchronism> actions = null;

		ApplicationSynchronism app = (ApplicationSynchronism) itemToLoad.getItemProperty(PROPERTY_DATA).getValue();
		Transaction transaction = null;
		try {
			SQLSession sqlSession = getSQLSession(application);
			transaction = sqlSession.getTransaction();
			transaction.begin();
			actions = (List<ActionSynchronism>) sqlSession.createQuery(
					"SELECT * FROM MOBILE_OBJETO WHERE TP_OBJETO = 'ACAO' AND ID_OBJETO_PAI = '" + app.getId()
							+ "' ORDER BY NOME_OBJETO", ActionSynchronism.class).getResultList();
		} catch (Exception e) {
			application.getMainWindow().showNotification(
					"Ocorreu um erro lendo as Ações " + e.getMessage() + " da aplicação " + app.getName(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}finally{
			try {
				transaction.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (actions != null) {
			HierarchicalContainer hwContainer = (HierarchicalContainer) application.getTree().getContainerDataSource();
			for (ActionSynchronism action : actions) {
				hwContainer.removeItemRecursively(action.getId());
				item = hwContainer.addItem(action.getId());
				item.getItemProperty(PROPERTY_NAME).setValue(action.getName() + " " + action.getId());
				item.getItemProperty(PROPERTY_DATA).setValue(action);
				item.getItemProperty(PROPERTY_ICON).setValue(ACTION_IMG);
				hwContainer.setChildrenAllowed(action.getId(), true);
				hwContainer.setParent(action.getId(), app.getId());
			}
		}

	}

	public static void loadTablesAndProcedures(MobileServerApplication application, Item itemToLoad) {
		Item item = null;
		List<Synchronism> tablesAndProcedures = null;

		ActionSynchronism action = (ActionSynchronism) itemToLoad.getItemProperty(PROPERTY_DATA).getValue();
		Transaction transaction = null;
		try {
			SQLSession sqlSession = getSQLSession(application);
			transaction = sqlSession.getTransaction();
			transaction.begin();
			tablesAndProcedures = (List<Synchronism>) sqlSession.createQuery(
					"SELECT * FROM MOBILE_OBJETO WHERE TP_OBJETO IN('TABELA','PROCEDIMENTO') AND ID_OBJETO_PAI = '"
							+ action.getId() + "' ORDER BY NOME_OBJETO", Synchronism.class).getResultList();
		} catch (Exception e) {
			application.getMainWindow()
					.showNotification(
							"Ocorreu um erro lendo as Tabelas/Procedimentos " + e.getMessage() + " da Ação "
									+ action.getName(), Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}finally{
			try {
				transaction.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HierarchicalContainer hwContainer = (HierarchicalContainer) application.getTree().getContainerDataSource();

		if (tablesAndProcedures != null) {

			for (Synchronism synchronism : tablesAndProcedures) {
				hwContainer.removeItemRecursively(synchronism.getId());
				item = hwContainer.addItem(synchronism.getId());
				item.getItemProperty(PROPERTY_NAME).setValue(synchronism.getName() + " " + synchronism.getId());
				item.getItemProperty(PROPERTY_DATA).setValue(synchronism);

				if (synchronism instanceof TableSynchronism)
					item.getItemProperty(PROPERTY_ICON).setValue(TABLE_IMG);
				else
					item.getItemProperty(PROPERTY_ICON).setValue(PROCEDURE_IMG);

				hwContainer.setChildrenAllowed(action.getId(), true);
				hwContainer.setParent(synchronism.getId(), action.getId());
			}
		}

	}

	public static void loadTableFields(MobileServerApplication application, Item itemToLoad) {
		Item item = null;
		List<FieldSynchronism> fields = null;

		TableSynchronism table = (TableSynchronism) itemToLoad.getItemProperty(PROPERTY_DATA).getValue();
		try {
			SQLSession sqlSession = getSQLSession(application);
			sqlSession.getTransaction().begin();
			fields = (List<FieldSynchronism>) sqlSession.createQuery(
					"SELECT * FROM MOBILE_OBJETO WHERE TP_OBJETO = 'CAMPO' AND ID_OBJETO_PAI = '" + table.getId()
							+ "' ORDER BY NOME_OBJETO", FieldSynchronism.class).getResultList();
		} catch (Exception e) {
			application.getMainWindow().showNotification(
					"Ocorreu um erro lendo os Campos " + e.getMessage() + " da Tabela " + table.getName(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		HierarchicalContainer hwContainer = (HierarchicalContainer) application.getTree().getContainerDataSource();

		item = hwContainer.addItem(ID_TABLE_FIELDS + table.getId());
		item.getItemProperty(PROPERTY_NAME).setValue("Campos");
		item.getItemProperty(PROPERTY_ICON).setValue(FIELDS_IMG);
		hwContainer.setChildrenAllowed(ID_TABLE_FIELDS + table.getId(), true);
		hwContainer.setParent(ID_TABLE_FIELDS + table.getId(), table.getId());

		if (fields != null) {

			for (FieldSynchronism field : fields) {
				hwContainer.removeItemRecursively(field.getId());
				item = hwContainer.addItem(field.getId());
				item.getItemProperty(PROPERTY_NAME).setValue(field.getName() + " " + field.getId());
				item.getItemProperty(PROPERTY_DATA).setValue(field);
				item.getItemProperty(PROPERTY_ICON).setValue(FIELD_IMG);
				hwContainer.setChildrenAllowed(field.getId(), false);
				hwContainer.setParent(field.getId(), ID_TABLE_FIELDS + table.getId());
			}
		}

	}

	public static void loadTableParameters(MobileServerApplication application, Item itemToLoad) {
		Item item = null;
		List<ParameterSynchronism> parameters = null;

		TableSynchronism table = (TableSynchronism) itemToLoad.getItemProperty(PROPERTY_DATA).getValue();
		try {
			SQLSession sqlSession = getSQLSession(application);
			parameters = (List<ParameterSynchronism>) sqlSession.createQuery(
					"SELECT * FROM MOBILE_OBJETO WHERE TP_OBJETO = 'PARAMETRO' AND ID_OBJETO_PAI = '" + table.getId()
							+ "' ORDER BY NOME_OBJETO", ParameterSynchronism.class).getResultList();
		} catch (Exception e) {
			application.getMainWindow().showNotification(
					"Ocorreu um erro lendo os Parâmetros " + e.getMessage() + " da Tabela " + table.getName(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		HierarchicalContainer hwContainer = (HierarchicalContainer) application.getTree().getContainerDataSource();

		item = hwContainer.addItem(ID_TABLE_PARAMETERS + table.getId());
		item.getItemProperty(PROPERTY_NAME).setValue("Parâmetros");
		item.getItemProperty(PROPERTY_ICON).setValue(PARAMETERS_IMG);
		hwContainer.setChildrenAllowed(ID_TABLE_PARAMETERS + table.getId(), true);
		hwContainer.setParent(ID_TABLE_PARAMETERS + table.getId(), table.getId());

		if (parameters != null) {
			for (ParameterSynchronism parameter : parameters) {
				hwContainer.removeItem(parameter.getId());
				item = hwContainer.addItem(parameter.getId());
				item.getItemProperty(PROPERTY_NAME).setValue(parameter.getName() + " " + parameter.getId());
				item.getItemProperty(PROPERTY_DATA).setValue(parameter);
				item.getItemProperty(PROPERTY_ICON).setValue(PARAMETER_IMG);
				hwContainer.setChildrenAllowed(parameter.getId(), false);
				hwContainer.setParent(parameter.getId(), ID_TABLE_PARAMETERS + table.getId());
			}
		}

	}

	public static void loadProcedureParameters(MobileServerApplication application, Item itemToLoad) {
		Item item = null;
		List<ParameterSynchronism> parameters = null;

		ProcedureSynchronism procedure = (ProcedureSynchronism) itemToLoad.getItemProperty(PROPERTY_DATA).getValue();
		try {
			SQLSession sqlSession = getSQLSession(application);
			parameters = (List<ParameterSynchronism>) sqlSession.createQuery(
					"SELECT * FROM MOBILE_OBJETO WHERE TP_OBJETO = 'PARAMETRO' AND ID_OBJETO_PAI = '"
							+ procedure.getId() + "' ORDER BY SEQUENCE_PARAMETER", ParameterSynchronism.class).getResultList();
		} catch (Exception e) {
			application.getMainWindow().showNotification(
					"Ocorreu um erro lendo os Parâmetros " + e.getMessage() + " da Procedure " + procedure.getName(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		HierarchicalContainer hwContainer = (HierarchicalContainer) application.getTree().getContainerDataSource();

		item = hwContainer.addItem(ID_PROCEDURE_PARAMETERS + procedure.getId());
		item.getItemProperty(PROPERTY_NAME).setValue("Parâmetros");
		item.getItemProperty(PROPERTY_ICON).setValue(PARAMETERS_IMG);
		hwContainer.setChildrenAllowed(ID_PROCEDURE_PARAMETERS + procedure.getId(), true);
		hwContainer.setParent(ID_PROCEDURE_PARAMETERS + procedure.getId(), procedure.getId());

		if (parameters != null) {
			for (ParameterSynchronism parameter : parameters) {
				hwContainer.removeItem(parameter.getId());
				item = hwContainer.addItem(parameter.getId());
				item.getItemProperty(PROPERTY_NAME).setValue(parameter.getName() + " " + parameter.getId());
				item.getItemProperty(PROPERTY_DATA).setValue(parameter);
				item.getItemProperty(PROPERTY_ICON).setValue(PARAMETER_IMG);
				hwContainer.setChildrenAllowed(parameter.getId(), false);
				hwContainer.setParent(parameter.getId(), ID_PROCEDURE_PARAMETERS + procedure.getId());
			}
		}
	}

	private static SQLSession getSQLSession(Application application) throws Exception {
		MobileSession mobileSession = getMobileSession(application);
		SQLSession sqlSession = mobileSession.getSynchronismManager().getSqlSession();
		sqlSession.setClientId("ANTEROS_MOBILE_SERVER");
		return sqlSession;
	}

	public static MobileSession getMobileSession(Application application) throws Exception {
		HttpSession httpSession = ((WebApplicationContext) application.getContext()).getHttpSession();
		MobileSession mobileSession = getMobileServerContext(application).getMobileSession(httpSession);
		return mobileSession;
	}

	public static boolean reconfigureSession(Application application) {
		try {
			HttpSession httpSession = ((WebApplicationContext) application.getContext()).getHttpSession();
			MobileServerContext mobileServerContext = getMobileServerContext(application);
			mobileServerContext.buildSessionFactory(true);
			MobileSession mobileSession = mobileServerContext.getMobileSession(httpSession);
			mobileSession.clearSessions();
			return true;
		} catch (Exception e) {
			application.getMainWindow().showNotification(
					"Ocorreu um erro configurando a Sessão. " + e.getCause().getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}
		return false;
	}

	public static MobileServerContext getMobileServerContext(Application application) {
		HttpSession httpSession = ((WebApplicationContext) application.getContext()).getHttpSession();
		MobileServerContext mobileServerContext = (MobileServerContext) httpSession.getServletContext().getAttribute(
				"mobileServerContext");
		return mobileServerContext;
	}

	public static IndexedContainer loadAllProcedures(MobileServerApplication application) {
		IndexedContainer result = new IndexedContainer();
		try {
			SQLSession sqlSession = getSQLSession(application);
			sqlSession.getTransaction().begin();
			Set<StoredProcedureSchema> procedures = sqlSession.getDialect().getStoredProcedures(
					sqlSession.getConnection(), false);

			result.addContainerProperty(PROPERTY_NAME, String.class, null);
			result.addContainerProperty(PROPERTY_ICON, ThemeResource.class, null);
			result.addContainerProperty(PROPERTY_DATA, StoredProcedureSchema.class, null);

			for (StoredProcedureSchema procedure : procedures) {
				Item item = result.addItem(procedure.getName());
				item.getItemProperty(PROPERTY_NAME).setValue(procedure.getName());
				item.getItemProperty(PROPERTY_ICON).setValue(PROCEDURE_IMG);
				item.getItemProperty(PROPERTY_DATA).setValue(procedure);
			}
			result.sort(new Object[] { PROPERTY_NAME }, new boolean[] { true });

			sqlSession.getTransaction().rollback();
		} catch (Exception e) {
			application.getMainWindow().showNotification("Ocorreu um erro lendo os Procedimentos " + e.getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}

		return result;
	}

	public static void loadParametersByProcedure(MobileServerApplication application,
			StoredProcedureSchema storedProcedure) {
		try {
			SQLSession sqlSession = getSQLSession(application);
			if (storedProcedure instanceof StoredFunctionSchema) {
				Set<StoredFunctionSchema> procedures = sqlSession.getDialect().getStoredFunctions(
						sqlSession.getConnection(), storedProcedure.getName(), true);
				if (procedures.size() > 0) {
					StoredProcedureSchema sp = procedures.iterator().next();
					for (StoredParameterSchema parameter : sp.getParameters()) {
						storedProcedure.addParameter(parameter);
					}
				}
			} else {
				Set<StoredProcedureSchema> procedures = sqlSession.getDialect().getStoredProcedures(
						sqlSession.getConnection(), storedProcedure.getName(), true);
				if (procedures.size() > 0) {
					StoredProcedureSchema sp = procedures.iterator().next();
					for (StoredParameterSchema parameter : sp.getParameters()) {
						storedProcedure.addParameter(parameter);
					}
				}
			}

		} catch (Exception e) {
			application.getMainWindow().showNotification("Ocorreu um erro lendo os Procedimentos " + e.getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static MobileResponse executeActionSynchronism(MobileServerApplication application,
			ActionSynchronism action, String[] params, Boolean executeCommit) throws Exception {
		SynchronismManager synchronismManager = getMobileSession(application).getSynchronismManager();
		SQLSession sqlSession = synchronismManager.getSqlSession();
		

		MobileRequest mr = new MobileRequest();

		mr.setApplication(((ApplicationSynchronism) action.getObjectOwner()).getName());
		mr.setClientId("ANTEROS_MOBILE_SERVER");
		mr.setRequestMode(ACTION_EXECUTE_IMMEDIATE);
		mr.setUserAgent("Anteros-MobileServer");
		MobileAction ma = new MobileAction();
		ma.setName(action.getName());
		ma.addParameter(params);
		mr.addAction(ma);
		MobileResponse result = new MobileResponse();
		try {
			sqlSession.getTransaction().begin();
			result = synchronismManager.executeRequest(mr);
			if (executeCommit)
				sqlSession.getTransaction().commit();
			else
				sqlSession.getTransaction().rollback();
		} catch (ApplicationNotFoundException e) {
			new UserMessages(application.getMainWindow()).error(e);
			result.setStatus(e.getMessage());
			try {
				sqlSession.getTransaction().rollback();
			} catch (Exception ex) {

			}
		} catch (ActionNotFoundException e) {
			new UserMessages(application.getMainWindow()).error(e);
			result.setStatus(e.getMessage());
			try {
				sqlSession.getTransaction().rollback();
			} catch (Exception ex) {

			}
		} catch (Exception e) {
			new UserMessages(application.getMainWindow()).error(e);
			result.setStatus(e.getMessage());
			try {
				sqlSession.getTransaction().rollback();
			} catch (Exception ex) {

			}
		}
		return result;
	}

	public static boolean save(MobileServerApplication application, Synchronism synchronism) throws Exception {
		SQLSession sqlSession = getSQLSession(application);
		try {
			sqlSession.save(synchronism);
			sqlSession.getTransaction().commit();
			return true;
		} catch (Exception e) {
			try {
				e.printStackTrace();
				sqlSession.getTransaction().rollback();
			} catch (Exception e1) {
			}
			new UserMessages(application.getMainWindow()).error(e.getMessage());
		}
		return false;
	}

	public static boolean remove(MobileServerApplication application, Synchronism synchronism) {
		SQLSession sqlSession;
		try {
			sqlSession = getSQLSession(application);
			try {
				sqlSession.remove(synchronism);
				sqlSession.getTransaction().commit();
				return true;
			} catch (Exception e) {
				sqlSession.getTransaction().rollback();
				new UserMessages(application.getMainWindow()).error(e.getMessage());
			}
		} catch (Exception e1) {
			new UserMessages(application.getMainWindow()).error(e1.getMessage());
			e1.printStackTrace();
		}

		return false;
	}

	public static Synchronism refreshItemData(MobileServerApplication application, Item item) throws Exception {
		Synchronism synchronism = (Synchronism) item.getItemProperty(MobileServerData.PROPERTY_DATA).getValue();
		SQLSession sqlSession = getSQLSession(application);
		try {
			Synchronism newObject = (Synchronism) sqlSession.find(sqlSession.getIdentifier(synchronism));
			item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(newObject);
			return newObject;
		} catch (Exception e) {
			try {
				sqlSession.getTransaction().rollback();
			} catch (Exception e1) {
			}
			new UserMessages(application.getMainWindow()).error(e.getMessage());
		}
		return null;
	}

	public static void readPreferences(MobileServerApplication application) {
		getMobileServerContext(application).readPreferences();
	}

	public static boolean isConfigured(MobileServerApplication application) {
		return getMobileServerContext(application).isConfigured();
	}

	public static boolean isConnected(MobileServerApplication application) {
		return getMobileServerContext(application).isConnected();
	}

}
