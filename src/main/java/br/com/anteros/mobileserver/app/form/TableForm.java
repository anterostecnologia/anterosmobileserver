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
package br.com.anteros.mobileserver.app.form;

import java.io.UnsupportedEncodingException;
import java.security.acl.Owner;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.mobile.core.synchronism.model.ActionSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ApplicationSynchronism;
import br.com.anteros.mobile.core.synchronism.model.FieldSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobile.core.synchronism.model.TableSynchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.mobileserver.util.FieldTypes;
import br.com.anteros.mobileserver.util.UserMessages;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

public class TableForm extends VerticalLayout implements ClickListener, SelectedTabChangeListener {

	private TextField fldId;
	private TextField fldName;
	private TextField fldDescription;
	private TextField fldTableNameMobile;
	private TextArea fldTableSQL;
	private Table gridFields;
	private TabSheet pageControl;
	private Table gridParameters;
	private Form tableForm;
	private Button btnCancel;
	private HorizontalLayout buttons;
	private HorizontalLayout buttonsFields;
	private HorizontalLayout buttonsParameters;
	private Button btnOk;
	private VerticalLayout layoutFields;
	private VerticalLayout layoutParameters;
	private Button btnAddField;
	private Button btnEditField;
	private Button btnRemoveField;
	private Button btnAddParameter;
	private Button btnRemoveParameter;
	private Button btnEditParameter;
	private MobileServerApplication app;
	private TableSynchronism tableSynchronism;
	private Synchronism objectOwner;

	public TableForm(MobileServerApplication app, TableSynchronism tableSynchronism, Synchronism objectOwner) {
		this.setMargin(true);
		this.app = app;
		this.tableSynchronism = tableSynchronism;
		this.objectOwner = objectOwner;
		createForm();
		createFields();
		createGrids();
		createButtons();

		createPageControl();

		Label lblTitle = new Label("Tabela");
		lblTitle.setStyleName("h2 color");
		addComponent(lblTitle);
		addComponent(tableForm);
		addComponent(pageControl);
		addComponent(buttons);
		fldName.focus();

		loadData();
		enableActions();
	}

	private void createGrids() {
		layoutFields = new VerticalLayout();
		layoutFields.setImmediate(false);
		layoutFields.setWidth("100.0%");
		layoutFields.setHeight("100.0%");
		layoutFields.setMargin(false);

		gridFields = new Table();
		gridFields.setWidth("100.0%");
		gridFields.setHeight("100.0%");
		gridFields.setContainerDataSource(getFieldsDataSource());
		gridFields.setColumnHeaders(new String[] { "Id", "Nome campo", "Descrição", "Nome campo SQL", "Tipo de Campo",
				"" });
		gridFields.setSizeFull();
		gridFields.addListener(new Property.ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				enableActions();
			}
		});

		gridFields.setColumnExpandRatio("NAME", 1);
		gridFields.setColumnWidth("ID", 70);
		gridFields.setColumnWidth("NAME", 300);
		gridFields.setColumnWidth("DESCRIPTION", 400);
		gridFields.setColumnWidth("SQL_FIELD_NAME", 200);
		gridFields.setColumnWidth("FIELD_TYPE", 200);
		gridFields.setColumnWidth(MobileServerData.PROPERTY_DATA, 1);
		gridFields.setSelectable(true);
		gridFields.setImmediate(true);

		layoutFields.addComponent(gridFields);
		layoutFields.setExpandRatio(gridFields, 1.0f);

		buttonsFields = new HorizontalLayout();
		buttonsFields.setImmediate(false);
		buttonsFields.setWidth("100.0%");
		buttonsFields.setHeight("30px");
		buttonsFields.setMargin(false);
		buttonsFields.setSpacing(true);

		btnAddField = new Button();
		btnAddField.setCaption("Adicionar");
		btnAddField.setImmediate(false);
		btnAddField.setWidth("-1px");
		btnAddField.setHeight("-1px");
		btnAddField.setIcon(new ThemeResource("icons/16/fieldAdd.png"));
		btnAddField.addListener(this);
		buttonsFields.addComponent(btnAddField);
		buttonsFields.setComponentAlignment(btnAddField, new Alignment(33));

		btnRemoveField = new Button();
		btnRemoveField.setCaption("Remover");
		btnRemoveField.setImmediate(false);
		btnRemoveField.setWidth("-1px");
		btnRemoveField.setHeight("-1px");
		btnRemoveField.setIcon(new ThemeResource("icons/16/fieldRemove.png"));
		btnRemoveField.addListener(this);
		buttonsFields.addComponent(btnRemoveField);
		buttonsFields.setComponentAlignment(btnRemoveField, new Alignment(33));

		btnEditField = new Button();
		btnEditField.setCaption("Editar");
		btnEditField.setImmediate(false);
		btnEditField.setWidth("-1px");
		btnEditField.setHeight("-1px");
		btnEditField.setIcon(new ThemeResource("icons/16/fieldEdit.png"));
		btnEditField.addListener(this);
		buttonsFields.addComponent(btnEditField);
		buttonsFields.setComponentAlignment(btnEditField, new Alignment(33));
		buttonsFields.setExpandRatio(btnEditField, 1);

		layoutParameters = new VerticalLayout();
		layoutParameters.setImmediate(false);
		layoutParameters.setWidth("100.0%");
		layoutParameters.setHeight("100.0%");
		layoutParameters.setMargin(false);

		gridParameters = new Table();
		gridParameters.setWidth("100.0%");
		gridParameters.setHeight("100.0%");
		gridParameters.setContainerDataSource(getParametersDataSource());
		gridParameters.setColumnHeaders(new String[] { "Id", "Nome Parâmetro", "Descrição", "Tipo Dado Parâmetro",
				"Tipo de Parâmetro", "" });
		gridParameters.setSizeFull();
		gridParameters.setSelectable(true);
		gridParameters.setImmediate(true);

		gridParameters.setColumnExpandRatio("NAME", 1);
		gridParameters.setColumnWidth("ID", 70);
		gridParameters.setColumnWidth("NAME", 300);
		gridParameters.setColumnWidth("DESCRIPTION", 400);
		gridParameters.setColumnWidth("PARAMETER_DATA_TYPE", 200);
		gridParameters.setColumnWidth("PARAMETER_TYPE", 200);
		gridParameters.setColumnWidth(MobileServerData.PROPERTY_DATA, 0);
		gridParameters.addListener(new Property.ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				enableActions();
			}
		});
		layoutParameters.addComponent(gridParameters);
		layoutParameters.setExpandRatio(gridParameters, 1.0f);

		buttonsParameters = new HorizontalLayout();
		buttonsParameters.setImmediate(false);
		buttonsParameters.setWidth("100.0%");
		buttonsParameters.setHeight("30px");
		buttonsParameters.setMargin(false);
		buttonsParameters.setSpacing(true);

		btnAddParameter = new Button();
		btnAddParameter.setCaption("Adicionar");
		btnAddParameter.setImmediate(false);
		btnAddParameter.setWidth("-1px");
		btnAddParameter.setHeight("-1px");
		btnAddParameter.setIcon(new ThemeResource("icons/16/parameterAdd.png"));
		btnAddParameter.addListener(this);
		buttonsParameters.addComponent(btnAddParameter);
		buttonsParameters.setComponentAlignment(btnAddParameter, new Alignment(33));

		btnRemoveParameter = new Button();
		btnRemoveParameter.setCaption("Remover");
		btnRemoveParameter.setImmediate(false);
		btnRemoveParameter.setWidth("-1px");
		btnRemoveParameter.setHeight("-1px");
		btnRemoveParameter.setIcon(new ThemeResource("icons/16/parameterRemove.png"));
		btnRemoveParameter.addListener(this);
		buttonsParameters.addComponent(btnRemoveParameter);
		buttonsParameters.setComponentAlignment(btnRemoveParameter, new Alignment(33));

		btnEditParameter = new Button();
		btnEditParameter.setCaption("Editar");
		btnEditParameter.setImmediate(false);
		btnEditParameter.setWidth("-1px");
		btnEditParameter.setHeight("-1px");
		btnEditParameter.setIcon(new ThemeResource("icons/16/parameterEdit.png"));
		btnEditParameter.addListener(this);
		buttonsParameters.addComponent(btnEditParameter);
		buttonsParameters.setComponentAlignment(btnEditParameter, new Alignment(33));
		buttonsParameters.setExpandRatio(btnEditParameter, 1);

	}

	private void createPageControl() {
		pageControl = new TabSheet();
		pageControl.setWidth("100%");
		pageControl.setHeight("180px");
		pageControl.addTab(layoutFields, "Campos");
		pageControl.addTab(layoutParameters, "Parâmetros");
		pageControl.addListener(this);
	}

	private void createButtons() {
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setWidth("100%");

		buttons.addComponent(buttonsFields);
		buttons.setExpandRatio(buttonsFields, 1);

		btnOk = new Button("Ok", this);
		btnOk.addStyleName("default");
		btnOk.setIcon(new ThemeResource("icons/16/ok.png"));
		btnOk.addListener(this);
		buttons.addComponent(btnOk);
		buttons.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);

		btnCancel = new Button("Cancela", this);
		btnCancel.setIcon(new ThemeResource("icons/16/cancel.png"));
		btnCancel.addListener(this);
		buttons.addComponent(btnCancel);
		buttons.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		buttons.setMargin(true, false, true, false);
	}

	private void createFields() {
		fldId = new TextField();
		fldId.setCaption("Id");
		fldId.setWidth("100px");
		fldId.setStyleName("small");

		fldName = new TextField();
		fldName.setCaption("Nome da tabela");
		fldName.setWidth("200px");
		fldName.setRequired(true);
		fldName.setRequiredError("Informe o nome da tabela.");
		fldName.setStyleName("small");

		fldDescription = new TextField();
		fldDescription.setCaption("Descrição da tabela");
		fldDescription.setWidth("500px");
		fldDescription.setRequired(true);
		fldDescription.setRequiredError("Informe a descrição da tabela.");
		fldDescription.setStyleName("small");

		fldTableNameMobile = new TextField();
		fldTableNameMobile.setCaption("Tabela na aplicação móvel");
		fldTableNameMobile.setWidth("100px");
		fldTableNameMobile.setStyleName("small");
		fldTableNameMobile.setRequired(true);
		fldTableNameMobile.setRequiredError("Informe o nome da tabela na aplicação móvel.");

		fldTableSQL = new TextArea();
		fldTableSQL.setCaption("SQL");
		fldTableSQL.setWidth("100%");
		fldTableSQL.setHeight("150px");
		fldTableSQL.setStyleName("small");
		fldTableSQL.setRequired(true);
		fldTableSQL.setRequiredError("Informe o SQL para seleção dos dados.");

		tableForm.addField("fldId", fldId);
		tableForm.addField("fldName", fldName);
		tableForm.addField("fldDescription", fldDescription);
		tableForm.addField("fldTableNameMobile", fldTableNameMobile);
		tableForm.addField("fldTableSQL", fldTableSQL);
	}

	private void createForm() {
		tableForm = new Form();
		tableForm.setWriteThrough(false);
		tableForm.setInvalidCommitted(false);
	}

	
	public void buttonClick(ClickEvent event) {
		final TableForm comp = this;
		if (event.getSource() == btnAddParameter) {
			final ParameterSynchronism parameter = new ParameterSynchronism();
			parameter.setId(new Date().getTime() * -1);
			parameter.setObjectOwner(tableSynchronism);
			final ParameterWindow parameterWindow = new ParameterWindow(app, parameter, tableSynchronism);
			getWindow().addWindow(parameterWindow);
			parameterWindow.addListener(new CloseListener() {
				
				public void windowClose(CloseEvent e) {
					if (parameterWindow.getFormParameter().getLastAction() == UserMessages.USER_CONFIRM_OK) {
						addParameterDataSource((IndexedContainer) gridParameters.getContainerDataSource(), parameter);
						enableActions();
					}
				}
			});
		} else if (event.getSource() == btnRemoveParameter) {
			final Synchronism selectedParameter = (Synchronism) this.getSelectedParameter();
			if (selectedParameter != null) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover o parâmetro " + selectedParameter.getName() + " ?", new ClickListener() {
					
					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							gridParameters.getContainerDataSource().removeItem(selectedParameter.getId());
							if (tableSynchronism.getItems() == null) {
								tableSynchronism.setItems(new LinkedHashSet<Synchronism>());
							}
							tableSynchronism.getItems().remove(selectedParameter);
							enableActions();
						}
					}
				});
			}
		} else if (event.getSource() == btnEditParameter) {
			final ParameterSynchronism selectedParameter = (ParameterSynchronism) this.getSelectedParameter();
			if (selectedParameter != null) {
				final ParameterWindow parameterWindow = new ParameterWindow(app, selectedParameter, tableSynchronism);
				getWindow().addWindow(parameterWindow);
				parameterWindow.addListener(new CloseListener() {
					
					public void windowClose(CloseEvent e) {
						if (parameterWindow.getFormParameter().getLastAction() == UserMessages.USER_CONFIRM_OK) {
							Item item = gridParameters.getItem(selectedParameter.getId());
							if (item != null) {
								item.getItemProperty("ID").setValue(selectedParameter.getId());
								item.getItemProperty("NAME").setValue(selectedParameter.getName());
								item.getItemProperty("DESCRIPTION").setValue(selectedParameter.getDescription());
								if (selectedParameter.getParameterDataType() != null) {
									item.getItemProperty("PARAMETER_DATA_TYPE").setValue(
											FieldTypes.getFieldNameByValue(selectedParameter.getParameterDataType()
													.intValue() + ""));
								}
								item.getItemProperty("PARAMETER_TYPE").setValue(
										(selectedParameter.getParameterType().intValue() == 0 ? "INPUT" : "OUTPUT"));
								item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(selectedParameter);
								item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(selectedParameter);
								enableActions();
							}
						}
					}
				});
			}
		} else if (event.getSource() == btnAddField) {
			final FieldSynchronism field = new FieldSynchronism();
			field.setId(new Date().getTime() * -1);
			field.setObjectOwner(tableSynchronism);
			final FieldWindow fieldWindow = new FieldWindow(app, field, tableSynchronism);
			getWindow().addWindow(fieldWindow);
			fieldWindow.addListener(new CloseListener() {
				
				public void windowClose(CloseEvent e) {
					if (fieldWindow.getFieldForm().getLastAction() == UserMessages.USER_CONFIRM_OK) {
						addFieldDataSource((IndexedContainer) gridFields.getContainerDataSource(), field);
						enableActions();
					}
				}
			});
		} else if (event.getSource() == btnRemoveField) {
			final Synchronism selectedField = (Synchronism) this.getSelectedField();
			if (selectedField != null) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Remover o Campo " + selectedField.getName() + " ?", new ClickListener() {
					
					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							gridFields.getContainerDataSource().removeItem(selectedField.getId());
							if (tableSynchronism.getItems() == null) {
								tableSynchronism.setItems(new LinkedHashSet<Synchronism>());
							}
							tableSynchronism.getItems().remove(selectedField);
							enableActions();
						}
					}
				});
			}
		} else if (event.getSource() == btnEditField) {
			final FieldSynchronism selectedField = (FieldSynchronism) this.getSelectedField();
			if (selectedField != null) {
				final FieldWindow fieldWindow = new FieldWindow(app, selectedField, tableSynchronism);
				getWindow().addWindow(fieldWindow);
				fieldWindow.addListener(new CloseListener() {
					
					public void windowClose(CloseEvent e) {
						if (fieldWindow.getFieldForm().getLastAction() == UserMessages.USER_CONFIRM_OK) {
							Item item = gridFields.getItem(selectedField.getId());
							if (item != null) {
								item.getItemProperty("ID").setValue(selectedField.getId());
								item.getItemProperty("NAME").setValue(selectedField.getName());
								item.getItemProperty("DESCRIPTION").setValue(selectedField.getDescription());
								item.getItemProperty("SQL_FIELD_NAME").setValue(selectedField.getSqlFieldName());
								if (selectedField.getFieldType() != null) {
									item.getItemProperty("FIELD_TYPE").setValue(
											FieldTypes
													.getFieldNameByValue(selectedField.getFieldType().intValue() + ""));
								}
								item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(selectedField);
								enableActions();
							}
						}
					}
				});
			}
		} else if (event.getButton() == btnCancel) {
			if (this.getParent() instanceof TabSheet) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				userMessages.confirm("Cancelar a Edição da Aplicação?", new ClickListener() {
					
					public void buttonClick(ClickEvent event) {
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							refreshAndClose(comp);
						}
						userMessages.removeConfirm();
					}
				});

			}
		} else if (event.getButton() == btnOk) {
			if (this.getParent() instanceof TabSheet) {
				final UserMessages userMessages = new UserMessages(app.getMainWindow());
				final Form f = tableForm;
				userMessages.confirm("Gravar os dados?", new ClickListener() {
					
					public void buttonClick(ClickEvent event) {
						userMessages.removeConfirm();
						if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
							try {
								f.setValidationVisible(true);
								f.validate();
								saveData();
								if (tableSynchronism.getItems() != null) {
									for (Synchronism child : tableSynchronism.getItems()) {
										if (child.getId().longValue() < 0)
											child.setId(null);
									}
								}
								if (MobileServerData.save(app, tableSynchronism)) {
									refreshAndClose(comp);
								}
							} catch (Exception e) {
								getWindow()
										.showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);

							}
						}
					}
				});

			}
		}
	}

	private void refreshAndClose(final TableForm comp) {
		changeDataTreeItem();
		app.removeTab(comp);
		if (tableSynchronism.getId() != null)
			((HierarchicalContainer) app.getTree().getContainerDataSource()).removeItemRecursively(tableSynchronism
					.getId());

		Item owner = app.getTree().getItem(objectOwner.getId());
		app.refreshTreeItem(owner, owner.getItemProperty(MobileServerData.PROPERTY_DATA).getValue());
		if (tableSynchronism.getId() != null) {
			app.getTree().collapseItem(tableSynchronism.getId());
			app.getTree().expandItemsRecursively(tableSynchronism.getId());
			app.getTree().select(tableSynchronism.getId());
		}
	}

	protected void changeDataTreeItem() {
		if (tableSynchronism.getId() != null) {
			Item item = app.getTree().getItem(tableSynchronism.getId());
			if (item == null) {
				item = app.getTree().addItem(tableSynchronism.getId());
				((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(tableSynchronism.getId(),
						tableSynchronism.getObjectOwner().getId());
			}
			item.getItemProperty(MobileServerData.PROPERTY_NAME).setValue(
					tableSynchronism.getName() + " " + tableSynchronism.getId());
			item.getItemProperty(MobileServerData.PROPERTY_ICON).setValue(MobileServerData.TABLE_IMG);
			item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(tableSynchronism);
			((HierarchicalContainer) app.getTree().getContainerDataSource()).setChildrenAllowed(
					tableSynchronism.getId(), true);
		}
	}

	protected void saveData() throws UnsupportedEncodingException {
		if (!StringUtils.isEmpty(fldId.getValue() + "")) {
			tableSynchronism.setId(new Long(fldId.getValue() + ""));
		}
		tableSynchronism.setName(fldName.getValue() + "");
		tableSynchronism.setDescription(fldDescription.getValue() + "");
		tableSynchronism.setTableNameMobile(fldTableNameMobile.getValue() + "");
		String s = (fldTableSQL.getValue() + "");
		tableSynchronism.setTableSql(s.getBytes(MobileServerData.getMobileServerContext(app).getCharsetName()));
		tableSynchronism.setObjectOwner(objectOwner);
		if (objectOwner.getItems() == null) {
			objectOwner.setItems(new HashSet<Synchronism>());
		}
		objectOwner.getItems().add(tableSynchronism);
	}

	private Container getParametersDataSource() {
		IndexedContainer result = new IndexedContainer();
		result.addContainerProperty("ID", Long.class, null);
		result.addContainerProperty("NAME", String.class, null);
		result.addContainerProperty("DESCRIPTION", String.class, null);
		result.addContainerProperty("PARAMETER_DATA_TYPE", String.class, null);
		result.addContainerProperty("PARAMETER_TYPE", String.class, null);
		result.addContainerProperty(MobileServerData.PROPERTY_DATA, Object.class, null);
		if (tableSynchronism.getParameters() != null) {
			for (ParameterSynchronism param : tableSynchronism.getParameters()) {
				addParameterDataSource(result, param);
			}
		}
		result.sort(new Object[] { "ID" }, new boolean[] { true });
		return result;
	}

	private void addParameterDataSource(IndexedContainer result, ParameterSynchronism param) {
		Item item = result.addItem(param.getId());
		item.getItemProperty("ID").setValue(param.getId());
		item.getItemProperty("NAME").setValue(param.getName());
		item.getItemProperty("DESCRIPTION").setValue(param.getDescription());
		if (param.getParameterDataType() != null) {
			item.getItemProperty("PARAMETER_DATA_TYPE").setValue(
					FieldTypes.getFieldNameByValue(param.getParameterDataType().intValue() + ""));
		}
		item.getItemProperty("PARAMETER_TYPE")
				.setValue((param.getParameterType().intValue() == 0 ? "INPUT" : "OUTPUT"));
		item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(param);

		if (tableSynchronism.getItems() == null) {
			tableSynchronism.setItems(new LinkedHashSet<Synchronism>());
		}
		tableSynchronism.getItems().add(param);
	}

	private Container getFieldsDataSource() {
		IndexedContainer result = new IndexedContainer();
		result.addContainerProperty("ID", Long.class, null);
		result.addContainerProperty("NAME", String.class, null);
		result.addContainerProperty("DESCRIPTION", String.class, null);
		result.addContainerProperty("SQL_FIELD_NAME", String.class, null);
		result.addContainerProperty("FIELD_TYPE", String.class, null);
		result.addContainerProperty(MobileServerData.PROPERTY_DATA, Object.class, null);
		if (tableSynchronism.getFields() != null) {
			for (FieldSynchronism field : tableSynchronism.getFields()) {
				addFieldDataSource(result, field);
			}
		}
		result.sort(new Object[] { "ID" }, new boolean[] { true });
		return result;
	}

	private void addFieldDataSource(IndexedContainer result, FieldSynchronism field) {
		Item item = result.addItem(field.getId());
		item.getItemProperty("ID").setValue(field.getId());
		item.getItemProperty("NAME").setValue(field.getName());
		item.getItemProperty("DESCRIPTION").setValue(field.getDescription());
		item.getItemProperty("SQL_FIELD_NAME").setValue(field.getSqlFieldName());

		if (field.getFieldType() != null) {
			item.getItemProperty("FIELD_TYPE").setValue(
					FieldTypes.getFieldNameByValue(field.getFieldType().intValue() + ""));
		}
		item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(field);
		if (tableSynchronism.getItems() == null) {
			tableSynchronism.setItems(new LinkedHashSet<Synchronism>());
		}
		tableSynchronism.getItems().add(field);
	}

	
	public void selectedTabChange(SelectedTabChangeEvent event) {
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab(tabsheet.getSelectedTab());
		if (tab != null) {
			buttons.removeComponent(buttonsFields);
			buttons.removeComponent(buttonsParameters);
			buttons.removeComponent(btnOk);
			buttons.removeComponent(btnCancel);
			if (tab.getCaption().equals("Campos")) {
				buttons.addComponent(buttonsFields);
				buttons.setExpandRatio(buttonsFields, 1);
			} else {
				buttons.addComponent(buttonsParameters);
				buttons.setExpandRatio(buttonsParameters, 1);
			}
			buttons.addComponent(btnOk);
			buttons.addComponent(btnCancel);
			buttons.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);
			buttons.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		}
	}

	private void loadData() {
		if (tableSynchronism.getId() != null)
			fldId.setValue(tableSynchronism.getId());

		if (tableSynchronism.getDescription() != null)
			fldDescription.setValue(tableSynchronism.getDescription());

		if (tableSynchronism.getName() != null)
			fldName.setValue(tableSynchronism.getName());

		if (tableSynchronism.getTableNameMobile() != null)
			fldTableNameMobile.setValue(tableSynchronism.getTableNameMobile());

		if (tableSynchronism.getTableSql() != null)
			try {
				fldTableSQL.setValue(new String(tableSynchronism.getTableSql(), MobileServerData.getMobileServerContext(app).getCharsetName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public Object getSelectedField() {
		if (gridFields.getValue() != null) {
			Item item = gridFields.getItem(gridFields.getValue());
			if (item != null)
				return item.getItemProperty(MobileServerData.PROPERTY_DATA).getValue();
		}
		return null;
	}

	public Object getSelectedParameter() {
		if (gridParameters.getValue() != null) {
			Item item = gridParameters.getItem(gridParameters.getValue());
			if (item != null)
				return item.getItemProperty(MobileServerData.PROPERTY_DATA).getValue();
		}
		return null;
	}

	private void enableActions() {
		btnEditField.setEnabled(gridFields.getValue() != null);
		btnRemoveField.setEnabled(gridFields.getValue() != null);
		btnEditParameter.setEnabled(gridParameters.getValue() != null);
		btnRemoveParameter.setEnabled(gridParameters.getValue() != null);
	}

}
