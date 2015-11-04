package br.com.anteros.mobileserver.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import br.com.anteros.mobile.core.synchronism.model.ActionSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.mobile.core.synchronism.model.FieldSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ProcedureSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobile.core.synchronism.model.TableSynchronism;
import br.com.anteros.mobileserver.app.form.ActionForm;
import br.com.anteros.mobileserver.app.form.ApplicationForm;
import br.com.anteros.mobileserver.app.form.ConfigurationWindow;
import br.com.anteros.mobileserver.app.form.ExecuteForm;
import br.com.anteros.mobileserver.app.form.FieldForm;
import br.com.anteros.mobileserver.app.form.LogForm;
import br.com.anteros.mobileserver.app.form.LoginWindow;
import br.com.anteros.mobileserver.app.form.ParameterForm;
import br.com.anteros.mobileserver.app.form.ProcedureForm;
import br.com.anteros.mobileserver.app.form.TableForm;
import br.com.anteros.mobileserver.util.UserMessages;
import br.com.anteros.persistence.transaction.Transaction;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class MobileServerApplication extends Application implements ValueChangeListener {

	private static final String CAPTION_FIELDS = "Campos";
	private static final String CAPTION_PARAMETERS = "Parâmetros";
	private Tree tree = new Tree();
	private Button btnSeparatorApplication;

	private Button btnReload;
	private Button btnLog;

	private Button btnAddApplication;
	private Button btnRemoveApplication;
	private Button btnEditApplication;

	private Button btnAddAction;
	private Button btnRemoveAction;
	private Button btnEditAction;

	private Button btnSeparatorAction;

	private Button btnAddTable;
	private Button btnRemoveTable;
	private Button btnEditTable;

	private Button btnSeparatorTable;

	private Button btnAddProcedure;
	private Button btnRemoveProcedure;
	private Button btnEditProcedure;

	private Button btnSeparatorProcedure;

	private Button btnAddFieldTable;
	private Button btnRemoveFieldTable;
	private Button btnEditFieldTable;

	private Button btnSeparatorFieldTable;

	private Button btnAddParameter;
	private Button btnRemoveParameter;
	private Button btnEditParameter;

	private Button btnSeparatorParameter;

	private Button btnRun;

	private HorizontalSplitPanel horizontalDataLayout;

	private Button logo;

	private MenuBar menubar;

	private VerticalLayout verticalMainLayout;

	private HorizontalLayout toolBarButtonsLayout;
	private TabSheet pageControl;
	private VerticalLayout verticalHomeLayout;
	private MenuItem mnuFile;
	private Object mnuAddApplication;
	private Object mnuRemoveApplication;
	private Object mnuEditApplication;
	private MenuItem mnuAddAction;
	private MenuItem mnuRemoveAction;
	private MenuItem mnuEditAction;
	private MenuItem mnuAddTable;
	private MenuItem mnuRemoveTable;
	private MenuItem mnuEditTable;
	private MenuItem mnuAddProcedure;
	private MenuItem mnuRemoveProcedure;
	private MenuItem mnuEditProcedure;
	private MenuItem mnuAddParameter;
	private MenuItem mnuRemoveParameter;
	private MenuItem mnuEditParameter;
	private MenuItem mnuAddField;
	private MenuItem mnuRemoveField;
	private MenuItem mnuEditField;
	private MenuItem mnuRun;
	private Button btnSeparatorReload;

	private VerticalLayout verticalStatusBar;
	private HorizontalLayout horizontalToolbarLayout;
	private VerticalLayout menuBarLayout;
	private MenuItem mnuTools;
	private MenuItem mnuPreferences;
	private MenuItem mnuHelp;
	private MenuItem mnuEditConfiguration;
	private Embedded imgArchiteture;

	private Button btnDuplicateAction;
	private VerticalLayout verticalVersaoLayout;
	private Label lblVersao;

	public void init() {
		buildMainLayout();
	}

	private void buildMainLayout() {
		setMainWindow(new Window("Anteros Mobile Server"));
		setTheme("anteros");
		verticalMainLayout = new VerticalLayout();
		verticalMainLayout.setHeight("100%");
		verticalMainLayout.setWidth("100%");

		if (MobileServerData.isConfigured(app) && MobileServerData.isConnected(app))
			createMainLayout();
		else
			createConfiguration();
	}

	private void createConfiguration() {
		ConfigurationWindow configurationWindow = new ConfigurationWindow(this);
		configurationWindow.getConfigurationForm().getBtnCancel().setEnabled(false);
		getMainWindow().addWindow(configurationWindow);
	}

	public void createMainLayout() {
		this.getMainWindow().removeAllComponents();
		this.getMainWindow().removeAllActionHandlers();
		createToolbarLayout();
		createDataLayout();
		createStatusBarLayout();
		getMainWindow().setContent(verticalMainLayout);
		LoginWindow loginWindow = new LoginWindow(this);
		getMainWindow().addWindow(loginWindow);
	}

	private void createStatusBarLayout() {
		verticalStatusBar = new VerticalLayout();
		verticalStatusBar.setImmediate(true);
		verticalStatusBar.setWidth("100%");
		verticalStatusBar.setHeight("25px");
		verticalStatusBar.setMargin(false);
		verticalStatusBar.setStyleName("statusbar");
		
		lblVersao = new Label();
		lblVersao.setImmediate(false);
		lblVersao.setWidth("-1px");
		lblVersao.setHeight("-1px");
		lblVersao.setValue(" Anteros Mobile Server v1.0.3");

		verticalStatusBar.addComponent(lblVersao);
		verticalStatusBar.setComponentAlignment(lblVersao, Alignment.MIDDLE_LEFT);
		verticalStatusBar.setMargin(false, false, false, true);
		
		verticalMainLayout.addComponent(verticalStatusBar);
	}

	private void createDataLayout() {
		horizontalDataLayout = new HorizontalSplitPanel();
		horizontalDataLayout.setHeight("100%");
		horizontalDataLayout.setWidth("100%");
		verticalMainLayout.addComponent(horizontalDataLayout);
		verticalMainLayout.setExpandRatio(horizontalDataLayout, 1);
		createTreeObjects();
		pageControl = new TabSheet();
		pageControl.setSizeFull();
		horizontalDataLayout.addComponent(pageControl);
		verticalHomeLayout = new VerticalLayout();
		verticalHomeLayout.setSizeFull();

		imgArchiteture = new Embedded();
		imgArchiteture.setImmediate(false);
		imgArchiteture.setSource(new ThemeResource("images/architeture.png"));
		imgArchiteture.setType(1);
		imgArchiteture.setMimeType("image/png");
		verticalHomeLayout.addComponent(imgArchiteture);
		verticalHomeLayout.setComponentAlignment(imgArchiteture, Alignment.MIDDLE_CENTER);

		verticalVersaoLayout = new VerticalLayout();
		verticalVersaoLayout.setSizeFull();

		verticalHomeLayout.addComponent(verticalVersaoLayout);

		pageControl.addTab(verticalHomeLayout, "Home");
	}

	private void createTreeObjects() {
		tree = new Tree("Objetos", MobileServerData.getApplications(this));
		tree.setItemCaptionPropertyId(MobileServerData.PROPERTY_NAME);
		tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		tree.setItemIconPropertyId(MobileServerData.PROPERTY_ICON);
		tree.setImmediate(true);
		tree.addListener(expandListener);
		tree.addListener(valueChangeListener);
		tree.addStyleName(Reindeer.TREE_CONNECTORS);
		horizontalDataLayout.setSplitPosition(350, Sizeable.UNITS_PIXELS);
		horizontalDataLayout.setFirstComponent(tree);
		enableActions(null);
	}

	private void createToolbarLayout() {
		horizontalToolbarLayout = new HorizontalLayout();
		horizontalToolbarLayout.setMargin(false);
		horizontalToolbarLayout.setSpacing(false);
		horizontalToolbarLayout.setStyleName("toolbar");
		horizontalToolbarLayout.setWidth("100%");

		VerticalLayout vo = new VerticalLayout();
		vo.setImmediate(true);
		vo.setWidth("100%");
		vo.setHeight("67px");
		vo.setMargin(false);

		menuBarLayout = new VerticalLayout();

		toolBarButtonsLayout = new HorizontalLayout();
		toolBarButtonsLayout.setMargin(false);
		toolBarButtonsLayout.setHeight("-1px");
		toolBarButtonsLayout.setWidth("100%");
		toolBarButtonsLayout.setMargin(false, true, false, false);

		createMenuBar();

		menuBarLayout.addComponent(menubar);
		vo.addComponent(toolBarButtonsLayout);
		vo.setExpandRatio(toolBarButtonsLayout, 1);

		horizontalToolbarLayout.addComponent(vo);

		createButtons();

		verticalMainLayout.addComponent(menuBarLayout);
		verticalMainLayout.addComponent(horizontalToolbarLayout);
	}

	private void createButtons() {
		btnReload = new Button();
		btnReload.setStyleName("link");
		btnReload.setWidth("40px");
		btnReload.setDescription("<h4>Recarregar servidor</h4>");

		btnLog = new Button();
		btnLog.setStyleName("link");
		btnLog.setWidth("40px");
		btnLog.setDescription("<h4>Ver log do servidor</h4>");

		btnSeparatorReload = new Button();
		btnSeparatorReload.setStyleName("link");
		btnSeparatorReload.setWidth("15px");
		btnSeparatorReload.setData("SEPARATOR");

		btnAddApplication = new Button();
		btnAddApplication.setStyleName("link");
		btnAddApplication.setWidth("40px");
		btnAddApplication.setDescription("<h4>Adicionar Aplicação</h4>");

		btnRemoveApplication = new Button();
		btnRemoveApplication.setStyleName("link");
		btnRemoveApplication.setWidth("40px");
		btnRemoveApplication.setDescription("<h4>Remover Aplicação</h4>");

		btnEditApplication = new Button();
		btnEditApplication.setStyleName("link");
		btnEditApplication.setWidth("40px");
		btnEditApplication.setDescription("<h4>Editar Aplicação</h4>");

		btnSeparatorApplication = new Button();
		btnSeparatorApplication.setStyleName("link");
		btnSeparatorApplication.setWidth("15px");
		btnSeparatorApplication.setData("SEPARATOR");

		btnAddAction = new Button();
		btnAddAction.setStyleName("link");
		btnAddAction.setWidth("40px");
		btnAddAction.setDescription("<h4>Adicionar Ação</h4>");

		btnRemoveAction = new Button();
		btnRemoveAction.setStyleName("link");
		btnRemoveAction.setWidth("40px");
		btnRemoveAction.setDescription("<h4>Remover Ação</h4>");

		btnEditAction = new Button();
		btnEditAction.setStyleName("link");
		btnEditAction.setWidth("40px");
		btnEditAction.setDescription("<h4>Editar Ação</h4>");

		btnDuplicateAction = new Button();
		btnDuplicateAction.setStyleName("link");
		btnDuplicateAction.setWidth("40px");
		btnDuplicateAction.setDescription("<h4>Duplicar Ação</h4>");

		btnSeparatorAction = new Button();
		btnSeparatorAction.setStyleName("link");
		btnSeparatorAction.setWidth("15px");
		btnSeparatorAction.setData("SEPARATOR");

		btnAddTable = new Button();
		btnAddTable.setStyleName("link");
		btnAddTable.setWidth("40px");
		btnAddTable.setDescription("<h4>Adicionar Tabela</h4>");

		btnRemoveTable = new Button();
		btnRemoveTable.setStyleName("link");
		btnRemoveTable.setWidth("40px");
		btnRemoveTable.setDescription("<h4>Remover Tabela</h4>");

		btnEditTable = new Button();
		btnEditTable.setStyleName("link");
		btnEditTable.setWidth("40px");
		btnEditTable.setDescription("<h4>Editar Tabela</h4>");

		btnSeparatorTable = new Button();
		btnSeparatorTable.setStyleName("link");
		btnSeparatorTable.setWidth("15px");
		btnSeparatorTable.setData("SEPARATOR");

		btnAddFieldTable = new Button();
		btnAddFieldTable.setStyleName("link");
		btnAddFieldTable.setWidth("40px");
		btnAddFieldTable.setDescription("<h4>Adicionar Campo</h4>");

		btnRemoveFieldTable = new Button();
		btnRemoveFieldTable.setStyleName("link");
		btnRemoveFieldTable.setWidth("40px");
		btnRemoveFieldTable.setDescription("<h4>Remover Campo</h4>");

		btnEditFieldTable = new Button();
		btnEditFieldTable.setStyleName("link");
		btnEditFieldTable.setWidth("40px");
		btnEditFieldTable.setDescription("<h4>Editar Campo</h4>");

		btnSeparatorFieldTable = new Button();
		btnSeparatorFieldTable.setStyleName("link");
		btnSeparatorFieldTable.setWidth("15px");
		btnSeparatorFieldTable.setData("SEPARATOR");

		btnAddProcedure = new Button();
		btnAddProcedure.setStyleName("link");
		btnAddProcedure.setWidth("40px");
		btnAddProcedure.setDescription("<h4>Adicionar Procedimento</h4>");

		btnRemoveProcedure = new Button();
		btnRemoveProcedure.setStyleName("link");
		btnRemoveProcedure.setWidth("40px");
		btnRemoveProcedure.setDescription("<h4>Remover Procedimento</h4>");

		btnEditProcedure = new Button();
		btnEditProcedure.setStyleName("link");
		btnEditProcedure.setWidth("40px");
		btnEditProcedure.setDescription("<h4>Editar Procedimento</h4>");

		btnSeparatorProcedure = new Button();
		btnSeparatorProcedure.setStyleName("link");
		btnSeparatorProcedure.setWidth("15px");
		btnSeparatorProcedure.setData("SEPARATOR");

		btnAddParameter = new Button();
		btnAddParameter.setStyleName("link");
		btnAddParameter.setWidth("40px");
		btnAddParameter.setDescription("<h4>Adicionar Parâmetro</h4>");

		btnRemoveParameter = new Button();
		btnRemoveParameter.setStyleName("link");
		btnRemoveParameter.setWidth("40px");
		btnRemoveParameter.setDescription("<h4>Remover Parâmetro</h4>");

		btnEditParameter = new Button();
		btnEditParameter.setStyleName("link");
		btnEditParameter.setWidth("40px");
		btnEditParameter.setDescription("<h4>Editar Parâmetro</h4>");

		btnSeparatorParameter = new Button();
		btnSeparatorParameter.setStyleName("link");
		btnSeparatorParameter.setWidth("15px");
		btnSeparatorParameter.setData("SEPARATOR");

		btnRun = new Button();
		btnRun.setStyleName("link");
		btnRun.setWidth("40px");
		btnRun.setDescription("<h4>Executar</h4>");
		btnRun.setClickShortcut(KeyCode.F9, null);

		logo = new Button();
		logo.setStyleName("link");
		logo.setIcon(new ThemeResource("images/anteros_mobile_server45.png"));

		toolBarButtonsLayout.addComponent(btnReload);
		toolBarButtonsLayout.addComponent(btnLog);
		toolBarButtonsLayout.addComponent(btnSeparatorReload);
		toolBarButtonsLayout.addComponent(btnAddApplication);
		toolBarButtonsLayout.addComponent(btnRemoveApplication);
		toolBarButtonsLayout.addComponent(btnEditApplication);

		toolBarButtonsLayout.addComponent(btnSeparatorApplication);

		toolBarButtonsLayout.addComponent(btnAddAction);
		toolBarButtonsLayout.addComponent(btnRemoveAction);
		toolBarButtonsLayout.addComponent(btnEditAction);
		toolBarButtonsLayout.addComponent(btnDuplicateAction);

		toolBarButtonsLayout.addComponent(btnSeparatorAction);

		toolBarButtonsLayout.addComponent(btnAddTable);
		toolBarButtonsLayout.addComponent(btnRemoveTable);
		toolBarButtonsLayout.addComponent(btnEditTable);

		toolBarButtonsLayout.addComponent(btnSeparatorTable);

		toolBarButtonsLayout.addComponent(btnAddFieldTable);
		toolBarButtonsLayout.addComponent(btnRemoveFieldTable);
		toolBarButtonsLayout.addComponent(btnEditFieldTable);

		toolBarButtonsLayout.addComponent(btnSeparatorFieldTable);

		toolBarButtonsLayout.addComponent(btnAddProcedure);
		toolBarButtonsLayout.addComponent(btnRemoveProcedure);
		toolBarButtonsLayout.addComponent(btnEditProcedure);

		toolBarButtonsLayout.addComponent(btnSeparatorProcedure);

		toolBarButtonsLayout.addComponent(btnAddParameter);
		toolBarButtonsLayout.addComponent(btnRemoveParameter);
		toolBarButtonsLayout.addComponent(btnEditParameter);

		toolBarButtonsLayout.addComponent(btnSeparatorParameter);

		toolBarButtonsLayout.addComponent(btnRun);
		toolBarButtonsLayout.addComponent(logo);

		toolBarButtonsLayout.setExpandRatio(logo, 1);

		btnReload.setIcon(new ThemeResource("icons/32/arrow_refresh_small.png"));
		btnLog.setIcon(new ThemeResource("icons/32/log.png"));
		btnAddApplication.setIcon(new ThemeResource("icons/32/applicationAdd.png"));
		btnRemoveApplication.setIcon(new ThemeResource("icons/32/applicationRemove.png"));
		btnEditApplication.setIcon(new ThemeResource("icons/32/applicationEdit.png"));

		btnSeparatorReload.setIcon(new ThemeResource("icons/32/separator.png"));
		btnSeparatorApplication.setIcon(new ThemeResource("icons/32/separator.png"));
		btnSeparatorAction.setIcon(new ThemeResource("icons/32/separator.png"));
		btnSeparatorTable.setIcon(new ThemeResource("icons/32/separator.png"));
		btnSeparatorFieldTable.setIcon(new ThemeResource("icons/32/separator.png"));
		btnSeparatorProcedure.setIcon(new ThemeResource("icons/32/separator.png"));
		btnSeparatorParameter.setIcon(new ThemeResource("icons/32/separator.png"));

		btnAddAction.setIcon(new ThemeResource("icons/32/actionAdd32_.png"));
		btnRemoveAction.setIcon(new ThemeResource("icons/32/actionRemove.png"));
		btnEditAction.setIcon(new ThemeResource("icons/32/actionEdit.png"));
		btnDuplicateAction.setIcon(new ThemeResource("icons/32/actionDuplicate.png"));

		btnAddTable.setIcon(new ThemeResource("icons/32/tableAdd.png"));
		btnRemoveTable.setIcon(new ThemeResource("icons/32/tableRemove.png"));
		btnEditTable.setIcon(new ThemeResource("icons/32/tableEdit.png"));

		btnAddFieldTable.setIcon(new ThemeResource("icons/32/tableFieldAdd.png"));
		btnRemoveFieldTable.setIcon(new ThemeResource("icons/32/tableFieldRemove.png"));
		btnEditFieldTable.setIcon(new ThemeResource("icons/32/tableFieldEdit.png"));

		btnAddProcedure.setIcon(new ThemeResource("icons/32/procedureAdd.png"));
		btnRemoveProcedure.setIcon(new ThemeResource("icons/32/procedureRemove.png"));
		btnEditProcedure.setIcon(new ThemeResource("icons/32/procedureEdit.png"));

		btnAddParameter.setIcon(new ThemeResource("icons/32/parameterAdd.png"));
		btnRemoveParameter.setIcon(new ThemeResource("icons/32/parameterRemove.png"));
		btnEditParameter.setIcon(new ThemeResource("icons/32/parameterEdit.png"));
		btnRun.setIcon(new ThemeResource("icons/32/run.png"));

		btnReload.addListener(clickListener);
		btnLog.addListener(clickListener);
		btnAddApplication.addListener(clickListener);
		btnRemoveApplication.addListener(clickListener);
		btnEditApplication.addListener(clickListener);

		btnAddAction.addListener(clickListener);
		btnRemoveAction.addListener(clickListener);
		btnEditAction.addListener(clickListener);
		btnDuplicateAction.addListener(clickListener);

		btnAddTable.addListener(clickListener);
		btnRemoveTable.addListener(clickListener);
		btnEditTable.addListener(clickListener);

		btnAddFieldTable.addListener(clickListener);
		btnRemoveFieldTable.addListener(clickListener);
		btnEditFieldTable.addListener(clickListener);

		btnAddProcedure.addListener(clickListener);
		btnRemoveProcedure.addListener(clickListener);
		btnEditProcedure.addListener(clickListener);

		btnAddParameter.addListener(clickListener);
		btnRemoveParameter.addListener(clickListener);
		btnEditParameter.addListener(clickListener);

		btnRun.addListener(clickListener);

		toolBarButtonsLayout.setComponentAlignment(btnReload, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnLog, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnAddApplication, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnRemoveApplication, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnEditApplication, Alignment.MIDDLE_LEFT);

		toolBarButtonsLayout.setComponentAlignment(btnSeparatorReload, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnSeparatorApplication, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnSeparatorAction, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnSeparatorTable, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnSeparatorFieldTable, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnSeparatorProcedure, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnSeparatorParameter, Alignment.MIDDLE_LEFT);

		toolBarButtonsLayout.setComponentAlignment(btnAddAction, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnRemoveAction, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnEditAction, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnDuplicateAction, Alignment.MIDDLE_LEFT);

		toolBarButtonsLayout.setComponentAlignment(btnAddTable, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnRemoveTable, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnEditTable, Alignment.MIDDLE_LEFT);

		toolBarButtonsLayout.setComponentAlignment(btnAddFieldTable, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnRemoveFieldTable, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnEditFieldTable, Alignment.MIDDLE_LEFT);

		toolBarButtonsLayout.setComponentAlignment(btnAddProcedure, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnRemoveProcedure, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnEditProcedure, Alignment.MIDDLE_LEFT);

		toolBarButtonsLayout.setComponentAlignment(btnAddParameter, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnRemoveParameter, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(btnEditParameter, Alignment.MIDDLE_LEFT);

		toolBarButtonsLayout.setComponentAlignment(btnRun, Alignment.MIDDLE_LEFT);
		toolBarButtonsLayout.setComponentAlignment(logo, Alignment.MIDDLE_RIGHT);
	}

	private void createMenuBar() {
		menubar = new MenuBar();
		mnuFile = menubar.addItem("Arquivo", null);
		mnuAddApplication = mnuFile.addItem("Adicionar aplicação", new ThemeResource("icons/16/applicationAdd16.png"),
				menuCommand);
		mnuRemoveApplication = mnuFile.addItem("Remover aplicação",
				new ThemeResource("icons/16/applicationRemove.png"), menuCommand);
		mnuEditApplication = mnuFile.addItem("Editar aplicação", new ThemeResource("icons/16/applicationEdit.png"),
				menuCommand);
		mnuFile.addSeparator();
		mnuAddAction = mnuFile.addItem("Nova ação", new ThemeResource("icons/16/actionAdd.png"), menuCommand);
		mnuRemoveAction = mnuFile.addItem("Remover ação", new ThemeResource("icons/16/actionRemove.png"), menuCommand);
		mnuEditAction = mnuFile.addItem("Editar ação", new ThemeResource("icons/16/actionEdit.png"), menuCommand);
		mnuRun = mnuFile.addItem("Executar Ação", new ThemeResource("icons/16/run.png"), menuCommand);
		mnuFile.addSeparator();
		mnuAddTable = mnuFile.addItem("Nova tabela", new ThemeResource("icons/16/tableAdd.png"), menuCommand);
		mnuRemoveTable = mnuFile.addItem("Remover tabela", new ThemeResource("icons/16/tableRemove.png"), menuCommand);
		mnuEditTable = mnuFile.addItem("Editar tabela", new ThemeResource("icons/16/tableEdit.png"), menuCommand);
		mnuFile.addSeparator();
		mnuAddProcedure = mnuFile.addItem("Novo procedimento", new ThemeResource("icons/16/procedureAdd.png"),
				menuCommand);
		mnuRemoveProcedure = mnuFile.addItem("Remover procedimento", new ThemeResource("icons/16/procedureRemove.png"),
				menuCommand);
		mnuEditProcedure = mnuFile.addItem("Editar procedimento", new ThemeResource("icons/16/procedureEdit.png"),
				menuCommand);
		mnuFile.addSeparator();
		mnuAddParameter = mnuFile
				.addItem("Novo parâmetro", new ThemeResource("icons/16/parameterAdd.png"), menuCommand);
		mnuRemoveParameter = mnuFile.addItem("Remover parâmetro", new ThemeResource("icons/16/parameterRemove.png"),
				menuCommand);
		mnuEditParameter = mnuFile.addItem("Editar parâmetro", new ThemeResource("icons/16/parameterEdit.png"),
				menuCommand);
		mnuFile.addSeparator();
		mnuAddField = mnuFile.addItem("Novo campo", new ThemeResource("icons/16/fieldAdd.png"), menuCommand);
		mnuRemoveField = mnuFile.addItem("Remover campo", new ThemeResource("icons/16/fieldRemove.png"), menuCommand);
		mnuEditField = mnuFile.addItem("Editar campo", new ThemeResource("icons/16/fieldEdit.png"), menuCommand);
		mnuFile.addSeparator();
		mnuEditApplication = mnuFile.addItem("Sair", new ThemeResource("icons/16/doorOut.png"), menuCommand);

		mnuTools = menubar.addItem("Ferramentas", null);

		mnuPreferences = menubar.addItem("Preferências", null);
		mnuEditConfiguration = mnuPreferences.addItem("Configuração do servidor", new ThemeResource(
				"icons/16/configuration16.png"), menuCommand);

		mnuHelp = menubar.addItem("Ajuda", null);
		menubar.setWidth("100%");
	}

	public String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	private void setMainComponent(Component c) {
		horizontalDataLayout.setSecondComponent(c);
	}

	public void valueChange(ValueChangeEvent event) {

	}

	final MobileServerApplication app = this;

	private ExpandListener expandListener = new ExpandListener() {

		public void nodeExpand(ExpandEvent event) {
			Item itemToLoad = app.getTree().getItem(event.getItemId());
			refreshTreeItem(itemToLoad, itemToLoad.getItemProperty(MobileServerData.PROPERTY_DATA).getValue());
			tree.select(event.getItemId());
		}
	};

	public void refreshTreeItem(Item itemToLoad, Object object) {
		if (object instanceof ApplicationSynchronism)
			MobileServerData.loadActions(app, itemToLoad);
		else if (object instanceof ActionSynchronism)
			MobileServerData.loadTablesAndProcedures(app, itemToLoad);
		else if (object instanceof TableSynchronism) {
			MobileServerData.loadTableFields(app, itemToLoad);
			MobileServerData.loadTableParameters(app, itemToLoad);
		} else if (object instanceof ProcedureSynchronism) {
			MobileServerData.loadProcedureParameters(app, itemToLoad);
		}
	}

	private ValueChangeListener valueChangeListener = new ValueChangeListener() {

		public void valueChange(ValueChangeEvent event) {
			Item item = tree.getItem(event.getProperty().getValue());
			if (item != null) {
				Object obj = item.getItemProperty(MobileServerData.PROPERTY_DATA).getValue();
				if (obj == null)
					enableActions(item);
				else
					enableActions(obj);
			}
		}
	};

	private void enableActions(Object selectedObject) {
		boolean isFields = isFields();
		boolean isParameters = isParameters();

		btnAddApplication.setVisible(true);

		btnRemoveApplication.setEnabled(selectedObject instanceof ApplicationSynchronism
				&& ((ApplicationSynchronism) selectedObject).getItems() != null
				&& ((ApplicationSynchronism) selectedObject).getItems().size() == 0);

		btnEditApplication.setEnabled((selectedObject instanceof ApplicationSynchronism));

		btnAddAction.setVisible((selectedObject instanceof ApplicationSynchronism)
				|| (selectedObject instanceof ActionSynchronism));
		btnRemoveAction.setVisible((selectedObject instanceof ApplicationSynchronism)
				|| (selectedObject instanceof ActionSynchronism));
		btnEditAction.setVisible((selectedObject instanceof ApplicationSynchronism)
				|| (selectedObject instanceof ActionSynchronism));
		btnDuplicateAction.setVisible((selectedObject instanceof ApplicationSynchronism)
				|| (selectedObject instanceof ActionSynchronism));

		btnAddAction.setEnabled(selectedObject instanceof ApplicationSynchronism
				&& (((ApplicationSynchronism) selectedObject).getItems() != null)
				&& (((ApplicationSynchronism) selectedObject).getItems().size() > 0));

		btnRemoveAction.setEnabled(selectedObject instanceof ActionSynchronism
				&& ((ActionSynchronism) selectedObject).getItems() != null
				&& ((ActionSynchronism) selectedObject).getItems().size() == 0);

		btnEditAction.setEnabled(selectedObject instanceof ActionSynchronism);
		btnDuplicateAction.setEnabled(selectedObject instanceof ActionSynchronism);

		btnAddTable.setVisible((selectedObject instanceof ActionSynchronism)
				|| (selectedObject instanceof TableSynchronism));
		btnRemoveTable.setVisible((selectedObject instanceof ActionSynchronism)
				|| (selectedObject instanceof TableSynchronism));
		btnEditTable.setVisible((selectedObject instanceof ActionSynchronism)
				|| (selectedObject instanceof TableSynchronism));

		if (btnAddTable.isVisible()) {
			btnAddTable.setEnabled(selectedObject instanceof ActionSynchronism
					&& ((((ActionSynchronism) selectedObject).getItems() == null)
					|| (((ActionSynchronism) selectedObject).getItems().size() == 0)));
		}

		btnRemoveTable.setEnabled(selectedObject instanceof TableSynchronism
				&& ((TableSynchronism) selectedObject).getItems() != null
				&& ((TableSynchronism) selectedObject).getItems().size() == 0);

		btnEditTable.setEnabled(selectedObject instanceof TableSynchronism && !isFields && !isParameters);

		btnAddProcedure.setVisible((selectedObject instanceof ActionSynchronism)
				|| (selectedObject instanceof ProcedureSynchronism));
		btnRemoveProcedure.setVisible((selectedObject instanceof ActionSynchronism)
				|| (selectedObject instanceof ProcedureSynchronism));
		btnEditProcedure.setVisible((selectedObject instanceof ActionSynchronism)
				|| (selectedObject instanceof ProcedureSynchronism));

		if (btnAddProcedure.isVisible()) {
			btnAddProcedure.setEnabled(selectedObject instanceof ActionSynchronism
					&& ((((ActionSynchronism) selectedObject).getItems() == null)
					|| (((ActionSynchronism) selectedObject).getItems().size() == 0)));
		}

		btnRemoveProcedure.setEnabled(selectedObject instanceof ProcedureSynchronism
				&& ((ProcedureSynchronism) selectedObject).getItems() != null
				&& ((ProcedureSynchronism) selectedObject).getItems().size() == 0);

		btnEditProcedure.setEnabled(selectedObject instanceof ProcedureSynchronism);

		btnAddFieldTable
				.setVisible((((selectedObject instanceof TableSynchronism) || (selectedObject instanceof FieldSynchronism))
						|| isFields) && !isParameters);
		btnRemoveFieldTable
				.setVisible((((selectedObject instanceof TableSynchronism) || (selectedObject instanceof FieldSynchronism))
						|| isFields) && !isParameters);
		btnEditFieldTable
				.setVisible((((selectedObject instanceof TableSynchronism) || (selectedObject instanceof FieldSynchronism))
						|| isFields) && !isParameters);
		
		if (btnAddFieldTable.isVisible())
			btnAddFieldTable.setEnabled(selectedObject instanceof TableSynchronism || isFields);

		btnRemoveFieldTable.setEnabled(selectedObject instanceof FieldSynchronism
				&& ((FieldSynchronism) selectedObject).getItems() != null
				&& ((FieldSynchronism) selectedObject).getItems().size() == 0);

		btnEditFieldTable.setEnabled(selectedObject instanceof FieldSynchronism);

		btnAddParameter
				.setVisible((((selectedObject instanceof TableSynchronism) || (selectedObject instanceof ProcedureSynchronism)
						|| (selectedObject instanceof ParameterSynchronism))
						|| isParameters) && !isFields);
		btnRemoveParameter
				.setVisible((((selectedObject instanceof TableSynchronism) || (selectedObject instanceof ProcedureSynchronism)
						|| (selectedObject instanceof ParameterSynchronism))
						|| isParameters) && !isFields);
		btnEditParameter
				.setVisible((((selectedObject instanceof TableSynchronism) || (selectedObject instanceof ProcedureSynchronism)
						|| (selectedObject instanceof ParameterSynchronism))
						|| isParameters) && !isFields);
		
		if (btnAddParameter.isVisible())
			btnAddParameter.setEnabled(selectedObject instanceof TableSynchronism || selectedObject instanceof ProcedureSynchronism || isParameters);

		btnRemoveParameter.setEnabled(selectedObject instanceof ParameterSynchronism
				&& ((ParameterSynchronism) selectedObject).getItems() != null
				&& ((ParameterSynchronism) selectedObject).getItems().size() == 0);

		btnEditParameter.setEnabled(selectedObject instanceof ParameterSynchronism);

		btnRun.setVisible(selectedObject instanceof ActionSynchronism);
		if (btnRun.isVisible()) {
			if (selectedObject instanceof ActionSynchronism) {
				if ((((ActionSynchronism) selectedObject).getItems() != null)
						&& (((ActionSynchronism) selectedObject).getItems().size() == 0)) {
					btnRun.setVisible(false);
				}
			}
		}

		btnSeparatorParameter.setVisible(checkSeparatorIsNeedVisible(btnSeparatorParameter));

		btnSeparatorFieldTable.setVisible(checkSeparatorIsNeedVisible(btnSeparatorFieldTable));
		btnSeparatorProcedure.setVisible(checkSeparatorIsNeedVisible(btnSeparatorProcedure));
		btnSeparatorTable.setVisible(checkSeparatorIsNeedVisible(btnSeparatorTable));
		btnSeparatorAction.setVisible(checkSeparatorIsNeedVisible(btnSeparatorAction));

		btnSeparatorApplication.setVisible(checkSeparatorIsNeedVisible(btnSeparatorApplication));
	}

	private boolean checkSeparatorIsNeedVisible(Button btn) {
		boolean checkNext = false;
		for (int i = 0; i < toolBarButtonsLayout.getComponentCount(); i++) {
			if (!checkNext) {
				if (toolBarButtonsLayout.getComponent(i) == btn)
					checkNext = true;
			} else {
				if ("SEPARATOR".equals(((Button) toolBarButtonsLayout.getComponent(i)).getData())) {
					return false;
				} else {
					if (((Button) toolBarButtonsLayout.getComponent(i)).isVisible())
						return true;
				}
			}
		}
		return false;
	}

	private Command menuCommand = new Command() {
		public void menuSelected(MenuItem selectedItem) {
			if (selectedItem == mnuAddApplication)
				addApplication();
			else if (selectedItem == mnuRemoveApplication)
				removeApplication();
			else if (selectedItem == mnuEditApplication)
				editApplication();
			else if (selectedItem == mnuAddAction)
				addAction();
			else if (selectedItem == mnuRemoveAction)
				removeAction();
			else if (selectedItem == mnuEditAction)
				editAction();
			else if (selectedItem == mnuAddTable)
				addTable();
			else if (selectedItem == mnuRemoveTable)
				removeTable();
			else if (selectedItem == mnuEditTable)
				editTable();
			else if (selectedItem == mnuAddField)
				addFieldTable();
			else if (selectedItem == mnuRemoveField)
				removeFieldTable();
			else if (selectedItem == mnuEditField)
				editFieldTable();
			else if (selectedItem == mnuAddProcedure)
				addProcedure();
			else if (selectedItem == mnuRemoveProcedure)
				removeProcedure();
			else if (selectedItem == mnuEditProcedure)
				editProcedure();
			else if (selectedItem == mnuAddParameter)
				addParameter();
			else if (selectedItem == mnuRemoveParameter)
				removeParameter();
			else if (selectedItem == mnuEditParameter)
				editParameter();
			else if (selectedItem == mnuRun) {
				Object selectObject = app.getSelectedObject();
				if (selectObject != null) {
					if (selectObject instanceof ActionSynchronism) {
						ExecuteForm form = new ExecuteForm(app, (ActionSynchronism) selectObject);
						pageControl.addTab(form, "Ação " + ((ActionSynchronism) selectObject).getId())
								.setClosable(true);
						pageControl.setSelectedTab(form);
					}
				}
			} else if (selectedItem == mnuEditConfiguration) {
				ConfigurationWindow configurationWindow = new ConfigurationWindow(app);
				configurationWindow.getConfigurationForm().getBtnCancel().setEnabled(true);
				getMainWindow().addWindow(configurationWindow);
			}
		}
	};

	private ClickListener clickListener = new ClickListener() {

		public void buttonClick(ClickEvent event) {
			if (event.getComponent() == btnAddApplication)
				addApplication();
			else if (event.getComponent() == btnRemoveApplication)
				removeApplication();
			else if (event.getComponent() == btnReload)
				reloadServer();
			else if (event.getComponent() == btnLog)
				logServer();
			else if (event.getComponent() == btnEditApplication)
				editApplication();
			else if (event.getComponent() == btnAddAction)
				addAction();
			else if (event.getComponent() == btnRemoveAction)
				removeAction();
			else if (event.getComponent() == btnEditAction)
				editAction();
			else if (event.getComponent() == btnDuplicateAction)
				duplicateAction();
			else if (event.getComponent() == btnAddTable)
				addTable();
			else if (event.getComponent() == btnRemoveTable)
				removeTable();
			else if (event.getComponent() == btnEditTable)
				editTable();
			else if (event.getComponent() == btnAddFieldTable)
				addFieldTable();
			else if (event.getComponent() == btnRemoveFieldTable)
				removeFieldTable();
			else if (event.getComponent() == btnEditFieldTable)
				editFieldTable();
			else if (event.getComponent() == btnAddProcedure)
				addProcedure();
			else if (event.getComponent() == btnRemoveProcedure)
				removeProcedure();
			else if (event.getComponent() == btnEditProcedure)
				editProcedure();
			else if (event.getComponent() == btnAddParameter)
				addParameter();
			else if (event.getComponent() == btnRemoveParameter)
				removeParameter();
			else if (event.getComponent() == btnEditParameter)
				editParameter();
			else if (event.getComponent() == btnRun) {
				Object selectObject = app.getSelectedObject();
				if (selectObject != null) {
					if (selectObject instanceof ActionSynchronism) {
						ExecuteForm form = new ExecuteForm(app, (ActionSynchronism) selectObject);
						pageControl.addTab(form, "Ação " + ((ActionSynchronism) selectObject).getId())
								.setClosable(true);
						pageControl.setSelectedTab(form);
					}
				}
			}
		}

	};

	private void reloadServer() {
		final UserMessages userMessages = new UserMessages(app.getMainWindow());
		userMessages.confirm("Recarregar o dicionário do Servidor ?", new ClickListener() {

			public void buttonClick(ClickEvent event) {
				userMessages.removeConfirm();
				if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
					MobileServerData.reloadServer(app);
					MobileServerData.reconfigureSession(app);
					getTree().removeAllItems();
					getTree().setContainerDataSource(MobileServerData.getApplications(app));
				}
			}
		});
	}

	protected void logServer() {
		LogForm form = new LogForm(app);
		pageControl.addTab(form, "Log do servidor ").setClosable(true);
		pageControl.setSelectedTab(form);
	}

	private void addProcedure() {
		ProcedureForm form = new ProcedureForm(app, new ProcedureSynchronism(), (Synchronism) app.getSelectedObject());
		pageControl.addTab(form, "Procedimento").setClosable(true);
		pageControl.setSelectedTab(form);
	}

	private void removeProcedure() {
		final Synchronism selectObject = (Synchronism) app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ProcedureSynchronism) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover o procedimento " + selectObject.getName() + " ?", new ClickListener() {

					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK))
							removeTreeItem(event);
					}
				});
			}
		}
	}

	private void editProcedure() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ProcedureSynchronism) {
				ProcedureForm form = new ProcedureForm(app, (ProcedureSynchronism) selectObject,
						((ProcedureSynchronism) selectObject).getObjectOwner());
				pageControl.addTab(form, "Procedimento").setClosable(true);
				pageControl.setSelectedTab(form);
			}
		}
	}

	private void addParameter() {
		ParameterForm form = new ParameterForm(app, new ParameterSynchronism(), null,
				(Synchronism) app.getSelectedObject(), isParameters());
		pageControl.addTab(form, "Parâmetro").setClosable(true);
		pageControl.setSelectedTab(form);
	}

	private void removeParameter() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ParameterSynchronism) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover o parâmetro " + ((Synchronism) selectObject).getName() + " ?",
						new ClickListener() {

							public void buttonClick(ClickEvent event) {
								userMessages.removeConfirm();
								if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK))
									removeTreeItem(event);
							}
						});
			}
		}
	}

	private void editParameter() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ParameterSynchronism) {
				ParameterForm form = new ParameterForm(app, (ParameterSynchronism) selectObject, null,
						((Synchronism) selectObject).getObjectOwner(), isParameters());
				pageControl.addTab(form, "Parâmetro").setClosable(true);
				pageControl.setSelectedTab(form);
			}
		}
	}

	private void editFieldTable() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof FieldSynchronism) {
				FieldForm form = new FieldForm(app, (FieldSynchronism) selectObject, null,
						((Synchronism) selectObject).getObjectOwner(), isFields());
				pageControl.addTab(form, "Campo").setClosable(true);
				pageControl.setSelectedTab(form);
			}
		}
	}

	private void removeFieldTable() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof FieldSynchronism) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover o campo " + ((Synchronism) selectObject).getName() + " ?",
						new ClickListener() {

							public void buttonClick(ClickEvent event) {
								userMessages.removeConfirm();
								if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK))
									removeTreeItem(event);
							}
						});
			}
		}
	}

	private void addFieldTable() {
		FieldForm form = new FieldForm(app, new FieldSynchronism(), null, (Synchronism) app.getSelectedObject(),
				isFields());
		pageControl.addTab(form, "Campo da Tabela").setClosable(true);
		pageControl.setSelectedTab(form);

	}

	private void editTable() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof TableSynchronism) {
				TableForm form = new TableForm(app, (TableSynchronism) selectObject,
						((Synchronism) selectObject).getObjectOwner());
				pageControl.addTab(form, "Tabela").setClosable(true);
				pageControl.setSelectedTab(form);
			}
		}
	}

	private void removeTable() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof TableSynchronism) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover a tabela " + ((Synchronism) selectObject).getName() + " ?",
						new ClickListener() {

							public void buttonClick(ClickEvent event) {
								userMessages.removeConfirm();
								if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK))
									removeTreeItem(event);
							}
						});
			}
		}
	}

	private void addTable() {
		TableForm form = new TableForm(app, new TableSynchronism(), (Synchronism) app.getSelectedObject());
		pageControl.addTab(form, "Tabela").setClosable(true);
		pageControl.setSelectedTab(form);

	}

	private void editAction() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ActionSynchronism) {
				ActionForm form = new ActionForm(app, (ActionSynchronism) selectObject,
						((Synchronism) selectObject).getObjectOwner());
				pageControl.addTab(form, "Ação").setClosable(true);
				pageControl.setSelectedTab(form);
			}
		}
	}

	private void duplicateAction() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ActionSynchronism) {
				ActionSynchronism action;
				try {
					action = (ActionSynchronism) ((ActionSynchronism) selectObject).clone();
					ActionForm form = new ActionForm(app, action, action.getObjectOwner());
					pageControl.addTab(form, "Ação").setClosable(true);
					pageControl.setSelectedTab(form);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void removeAction() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ActionSynchronism) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover a ação " + ((Synchronism) selectObject).getName() + " ?",
						new ClickListener() {

							public void buttonClick(ClickEvent event) {
								userMessages.removeConfirm();
								if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK))
									removeTreeItem(event);
							}
						});
			}
		}
	}

	private void addAction() {
		Synchronism objectOwner = (Synchronism) app.getSelectedObject();
		if (objectOwner instanceof ActionSynchronism)
			objectOwner = objectOwner.getObjectOwner();

		ActionForm form = new ActionForm(app, new ActionSynchronism(), objectOwner);
		pageControl.addTab(form, "Ação").setClosable(true);
		pageControl.setSelectedTab(form);

	}

	private void editApplication() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ApplicationSynchronism) {
				ApplicationForm form = new ApplicationForm(app, (ApplicationSynchronism) selectObject);
				pageControl.addTab(form, "Aplicação").setClosable(true);
				pageControl.setSelectedTab(form);
			}
		}
	}

	private void removeApplication() {
		Object selectObject = app.getSelectedObject();
		if (selectObject != null) {
			if (selectObject instanceof ApplicationSynchronism) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover a Aplicação " + ((Synchronism) selectObject).getName() + " ?",
						new ClickListener() {

							public void buttonClick(ClickEvent event) {
								userMessages.removeConfirm();
								if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK))
									removeTreeItem(event);
							}
						});
			}
		}

	}

	private void addApplication() {
		ApplicationForm form = new ApplicationForm(app, new ApplicationSynchronism());
		pageControl.addTab(form, "Aplicação").setClosable(true);
		pageControl.setSelectedTab(form);
	}

	private void removeTreeItem(ClickEvent event) {
		if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
			Synchronism synchronism = (Synchronism) app.getSelectedObject();
			Object visualOwner = null;
			Item itemOwner = null;
			if (synchronism.getObjectOwner() != null) {
				visualOwner = tree.getParent(tree.getValue());
				itemOwner = tree.getItem(synchronism.getObjectOwner().getId());
			}
			if (MobileServerData.remove(app, synchronism))
				((HierarchicalContainer) app.getTree().getContainerDataSource()).removeItemRecursively(synchronism
						.getId());
			if (itemOwner != null) {
				Synchronism newObject = null;
				try {
					newObject = MobileServerData.refreshItemData(app, itemOwner);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (newObject != null) {
					enableActions(visualOwner);
					tree.select(visualOwner);
				}
			}
		}
	}

	public void removeTab(Component component) {
		pageControl.removeTab(pageControl.getTab(component));
		enableActions(getSelectedObject());
	}

	public Object getSelectedObject() {
		if (tree.getValue() != null) {
			Item item = tree.getItem(tree.getValue());
			if (item != null)
				return item.getItemProperty(MobileServerData.PROPERTY_DATA).getValue();
		}
		return null;
	}

	public boolean isFields() {
		if (tree.getValue() != null) {
			return (CAPTION_FIELDS.equals(tree.getItem(tree.getValue()).getItemProperty(MobileServerData.PROPERTY_NAME)
					.getValue()));
		}
		return false;
	}

	public boolean isParameters() {
		if (tree.getValue() != null) {
			return (CAPTION_PARAMETERS.equals(tree.getItem(tree.getValue())
					.getItemProperty(MobileServerData.PROPERTY_NAME).getValue()));
		}
		return false;
	}

	public Tree getTree() {
		return tree;
	}

}
