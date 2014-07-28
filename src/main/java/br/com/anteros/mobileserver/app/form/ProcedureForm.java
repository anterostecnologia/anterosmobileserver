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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.mobile.core.synchronism.model.ParameterSynchronism;
import br.com.anteros.mobile.core.synchronism.model.ProcedureSynchronism;
import br.com.anteros.mobile.core.synchronism.model.Synchronism;
import br.com.anteros.mobileserver.app.MobileServerApplication;
import br.com.anteros.mobileserver.app.MobileServerData;
import br.com.anteros.mobileserver.util.FieldTypes;
import br.com.anteros.mobileserver.util.UserMessages;
import br.com.anteros.persistence.schema.definition.StoredParameterSchema;
import br.com.anteros.persistence.schema.definition.StoredProcedureSchema;
import br.com.anteros.persistence.schema.definition.type.StoredParameterType;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ProcedureForm extends VerticalLayout implements ValueChangeListener, Button.ClickListener {

	private TextField fldId;
	private ComboBox fldName;
	private TextField fldDescription;
	private ComboBox fldParameterOut;
	private TabSheet pageControl;
	private Table gridParameters;
	private MobileServerApplication app;
	private ProcedureSynchronism procedureSynchronism;
	private HorizontalLayout buttons;
	private Form procedureForm;
	private Button btnOk;
	private Button btnCancel;
	private Button btnAddParameter;
	private Button btnRemoveParameter;
	private Button btnEditParameter;
	private Button btnImport;
	private Button btnMoveUp;
	private Button btnMoveDown;
	private Synchronism objectOwner;
	private Random randomGenerator = new Random(99);

	public ProcedureForm(MobileServerApplication app, ProcedureSynchronism procedureSynchronism, Synchronism objectOwner) {
		this.setMargin(true);
		this.procedureSynchronism = procedureSynchronism;
		this.app = app;
		this.objectOwner = objectOwner;

		createForm();
		createFields();
		createButtons();
		createGrid();
		createPageControl();

		Label lblTitle = new Label("Procedimento");
		lblTitle.setStyleName("h2 color");

		addComponent(lblTitle);
		addComponent(procedureForm);
		addComponent(pageControl);
		addComponent(buttons);
		fldName.focus();
		enableActions();
		loadData();
	}

	private void createForm() {
		procedureForm = new Form();
		procedureForm.setWriteThrough(false);
		procedureForm.setInvalidCommitted(false);
	}

	private void createPageControl() {
		pageControl = new TabSheet();
		pageControl.setWidth("100%");
		pageControl.setHeight("250px");
		pageControl.addTab(gridParameters, "Parâmetros");
	}

	private void createFields() {
		fldId = new TextField();
		fldId.setCaption("Id");
		fldId.setWidth("100px");
		fldId.setStyleName("small");

		fldName = new ComboBox();
		fldName.setCaption("Nome do procedimento");
		fldName.setWidth("400px");
		fldName.setRequired(true);
		fldName.setRequiredError("Informe o nome do procedimento.");
		fldName.setContainerDataSource(MobileServerData.loadAllProcedures(app));
		fldName.setItemCaptionPropertyId(MobileServerData.PROPERTY_NAME);
		fldName.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		fldName.setItemIconPropertyId(MobileServerData.PROPERTY_ICON);
		fldName.setImmediate(true);
		fldName.addListener(this);
		fldName.setStyleName("small");

		fldDescription = new TextField();
		fldDescription.setCaption("Descrição do procedimento");
		fldDescription.setWidth("500px");
		fldDescription.setRequired(true);
		fldDescription.setRequiredError("Informe a descrição do procedimento.");
		fldDescription.setStyleName("small");

		fldParameterOut = new ComboBox();
		fldParameterOut.setCaption("Nome do parâmetro retorno");
		fldParameterOut.setWidth("250px");
		fldParameterOut.setImmediate(true);
		fldParameterOut.setStyleName("small");
		fldParameterOut.setRequired(true);
		fldParameterOut.setRequiredError("Informe o nome do parâmetro de retorno.");

		procedureForm.addField("fldId", fldId);
		procedureForm.addField("fldName", fldName);
		procedureForm.addField("fldDescription", fldDescription);
		procedureForm.addField("fldParameterOut", fldParameterOut);
	}

	private void createButtons() {
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setWidth("100%");

		btnAddParameter = new Button("Adicionar", this);
		btnAddParameter.setIcon(new ThemeResource("icons/16/parameterAdd.png"));
		buttons.addComponent(btnAddParameter);
		buttons.setComponentAlignment(btnAddParameter, Alignment.MIDDLE_LEFT);

		btnRemoveParameter = new Button("Remover", this);
		btnRemoveParameter.setIcon(new ThemeResource("icons/16/parameterRemove.png"));
		buttons.addComponent(btnRemoveParameter);
		buttons.setComponentAlignment(btnRemoveParameter, Alignment.MIDDLE_LEFT);

		btnEditParameter = new Button("Editar", this);
		btnEditParameter.setIcon(new ThemeResource("icons/16/parameterEdit.png"));
		buttons.addComponent(btnEditParameter);
		buttons.setComponentAlignment(btnEditParameter, Alignment.MIDDLE_LEFT);

		btnImport = new Button("Importar parâmetros", this);
		btnImport.setIcon(new ThemeResource("icons/16/import.png"));
		buttons.addComponent(btnImport);
		buttons.setComponentAlignment(btnImport, Alignment.MIDDLE_LEFT);

		btnMoveUp = new Button("Mover p/cima", this);
		btnMoveUp.setIcon(new ThemeResource("icons/16/moveUp.png"));
		buttons.addComponent(btnMoveUp);
		buttons.setComponentAlignment(btnMoveUp, Alignment.MIDDLE_LEFT);

		btnMoveDown = new Button("Mover p/baixo", this);
		btnMoveDown.setIcon(new ThemeResource("icons/16/moveDown.png"));
		buttons.addComponent(btnMoveDown);
		buttons.setComponentAlignment(btnMoveDown, Alignment.MIDDLE_LEFT);

		btnOk = new Button("Ok", this);
		btnOk.addStyleName("default");
		btnOk.setIcon(new ThemeResource("icons/16/ok.png"));
		buttons.addComponent(btnOk);
		buttons.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);
		buttons.setExpandRatio(btnOk, 1);

		btnCancel = new Button("Cancela", this);
		btnCancel.setIcon(new ThemeResource("icons/16/cancel.png"));
		buttons.addComponent(btnCancel);
		buttons.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		buttons.setMargin(true, false, true, false);
	}

	private void createGrid() {
		gridParameters = new Table("Parâmetros da Tabela");
		gridParameters.setImmediate(true);
		gridParameters.setContainerDataSource(getParametersDataSource(procedureSynchronism));
		gridParameters.setColumnHeaders(new String[] { "Id", "Seq", "Nome Parâmetro", "Descrição",
				"Tipo Dado Parâmetro", "Tipo de Parâmetro", "" });
		gridParameters.setSizeFull();
		gridParameters.addListener(new Property.ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				enableActions();
			}
		});

		gridParameters.setColumnExpandRatio("NAME", 1);
		gridParameters.setColumnWidth("ID", 70);
		gridParameters.setColumnWidth("SEQ", 30);
		gridParameters.setColumnWidth("NAME", 300);
		gridParameters.setColumnWidth("DESCRIPTION", 400);
		gridParameters.setColumnWidth("PARAMETER_DATA_TYPE", 200);
		gridParameters.setColumnWidth("PARAMETER_TYPE", 200);
		gridParameters.setColumnWidth(MobileServerData.PROPERTY_DATA, 1);
		gridParameters.setSelectable(true);
		gridParameters.setImmediate(true);

	}

	private Container getParametersDataSource(ProcedureSynchronism procedureSynchronism) {
		IndexedContainer result = new IndexedContainer();
		result.addContainerProperty("ID", Long.class, null);
		result.addContainerProperty("SEQ", Long.class, null);
		result.addContainerProperty("NAME", String.class, null);
		result.addContainerProperty("DESCRIPTION", String.class, null);
		result.addContainerProperty("PARAMETER_DATA_TYPE", String.class, null);
		result.addContainerProperty("PARAMETER_TYPE", String.class, null);
		result.addContainerProperty(MobileServerData.PROPERTY_DATA, Object.class, null);
		ParameterSynchronism[] parameters = procedureSynchronism.getParameters();
		if (parameters != null) {
			for (ParameterSynchronism param : parameters) {
				Item item = result.addItem(param.getId());
				item.getItemProperty("ID").setValue(param.getId());
				item.getItemProperty("SEQ").setValue(param.getSequence());
				item.getItemProperty("NAME").setValue(param.getName());
				item.getItemProperty("DESCRIPTION").setValue(param.getDescription());

				if (param.getParameterDataType() != null) {
					item.getItemProperty("PARAMETER_DATA_TYPE").setValue(
							FieldTypes.getFieldNameByValue(param.getParameterDataType().intValue() + ""));
				}
				item.getItemProperty("PARAMETER_TYPE").setValue(
						(param.getParameterType().intValue() == 0 ? "INPUT" : "OUTPUT"));
				item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(param);
			}
		}
		result.sort(new Object[] { "SEQ" }, new boolean[] { true });
		return result;
	}

	
	public void valueChange(ValueChangeEvent event) {
		Property prop = fldName.getContainerProperty(event.getProperty().toString(), MobileServerData.PROPERTY_DATA);
		loadParametersOut(prop);
	}

	private void loadParametersOut(Property prop) {
		StoredProcedureSchema procedure = (StoredProcedureSchema) prop.getValue();
		MobileServerData.loadParametersByProcedure(app, procedure);
		IndexedContainer container = new IndexedContainer();

		container.addContainerProperty(MobileServerData.PROPERTY_NAME, String.class, null);
		container.addContainerProperty(MobileServerData.PROPERTY_ICON, ThemeResource.class, null);
		container.addContainerProperty(MobileServerData.PROPERTY_DATA, StoredParameterSchema.class, null);

		try {
			for (StoredParameterSchema param : procedure.getParameters()) {
				if ((param.getParameterType() == StoredParameterType.OUT)) {
					Item item = container.addItem(param.getName());
					item.getItemProperty(MobileServerData.PROPERTY_NAME).setValue(param.getName());
					item.getItemProperty(MobileServerData.PROPERTY_ICON).setValue(MobileServerData.PARAMETER_IMG);
					item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(param);
				}
			}
			container.sort(new Object[] { MobileServerData.PROPERTY_NAME }, new boolean[] { true });
			fldParameterOut.setContainerDataSource(container);
			fldParameterOut.setItemCaptionPropertyId(MobileServerData.PROPERTY_NAME);
			fldParameterOut.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
			fldParameterOut.setItemIconPropertyId(MobileServerData.PROPERTY_ICON);
		} catch (Exception e) {
			app.getMainWindow()
					.showNotification(
							"Ocorreu um erro lendo os parâmetros do Procedimento " + procedure.getName() + " "
									+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void enableActions() {
		btnEditParameter.setEnabled(gridParameters.getValue() != null);
		btnRemoveParameter.setEnabled(gridParameters.getValue() != null);
		btnMoveUp.setEnabled(gridParameters.getValue() != null);
		btnMoveDown.setEnabled(gridParameters.getValue() != null);
	}

	
	public void buttonClick(ClickEvent event) {
		if (event.getSource() == btnImport)
			importParameters();
		else if (event.getSource() == btnAddParameter)
			addParameter();
		else if (event.getSource() == btnRemoveParameter)
			removeParameter();
		else if (event.getSource() == btnEditParameter)
			editParameter();
		else if (event.getButton() == btnCancel)
			cancelEditProcedure();
		else if (event.getButton() == btnOk)
			saveProcedure();
		else if (event.getButton() == btnMoveUp)
			moveParameterUp();
		else if (event.getButton() == btnMoveDown)
			moveParameterDown();

	}

	private void importParameters() {
		if (!StringUtils.isEmpty(fldName.getValue() + "")) {
			Property prop = fldName.getContainerProperty(fldName.getValue(), MobileServerData.PROPERTY_DATA);
			if (prop != null) {
				final StoredProcedureSchema procedure = (StoredProcedureSchema) prop.getValue();
				MobileServerData.loadParametersByProcedure(app, procedure);
				if (procedure != null) {
					final UserMessages userMessages = new UserMessages(app.getMainWindow());
					userMessages.confirm("Importar o(s) parâmetro(s) do procedimento " + procedure.getName() + " ?",
							new ClickListener() {
								
								public void buttonClick(ClickEvent event) {
									userMessages.removeConfirm();
									if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
										gridParameters.getContainerDataSource().removeAllItems();
										if (procedureSynchronism.getItems() == null)
											procedureSynchronism.setItems(new LinkedHashSet<Synchronism>());
										procedureSynchronism.getItems().clear();
										StoredParameterSchema currentParam = null;
										try {
											int sequence = 1;
											for (StoredParameterSchema param : procedure.getParameters()) {
												currentParam = param;
												ParameterSynchronism parameterSynchronism = new ParameterSynchronism();
												parameterSynchronism.setId(Math.abs(randomGenerator.nextLong()) * -1);
												parameterSynchronism.setName(param.getName());
												parameterSynchronism.setObjectOwner(procedureSynchronism);
												parameterSynchronism.setDescription(param.getName());
												if (StoredParameterType.IN.equals(param.getType()))
													parameterSynchronism.setParameterType(ParameterSynchronism.INPUT);
												else
													parameterSynchronism.setParameterType(ParameterSynchronism.OUTPUT);
												parameterSynchronism.setParameterDataType(new Long(FieldTypes
														.getFieldValueByName(FieldTypes.convertJdbcType(param
																.getDataTypeSql()))));
												parameterSynchronism.setSequence(new Long(sequence));
												addParameterDataSource(
														(IndexedContainer) gridParameters.getContainerDataSource(),
														parameterSynchronism);
												sequence++;
											}
										} catch (Exception e) {
											getWindow().showNotification(
													"Atenção",
													"Ocorreu um erro importando " + currentParam.toString() + " "
															+ e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
										}
										enableActions();
									}
								}
							});
				}
			}
		}
	}

	private void moveParameterDown() {
		final ParameterSynchronism selectedParameter = (ParameterSynchronism) this.getSelectedParameter();
		if (selectedParameter != null) {
			procedureSynchronism.moveDown(selectedParameter);
			gridParameters.setContainerDataSource(getParametersDataSource(procedureSynchronism));
			gridParameters.setValue(selectedParameter.getId());
			gridParameters.focus();
		}
	}

	private void moveParameterUp() {
		final ParameterSynchronism selectedParameter = (ParameterSynchronism) this.getSelectedParameter();
		if (selectedParameter != null) {
			procedureSynchronism.moveUp(selectedParameter);
			gridParameters.setContainerDataSource(getParametersDataSource(procedureSynchronism));
			gridParameters.setValue(selectedParameter.getId());
			gridParameters.focus();
		}
	}

	private void saveProcedure() {
		final ProcedureForm comp = this;
		if (this.getParent() instanceof TabSheet) {
			final UserMessages userMessages = new UserMessages(app.getMainWindow());
			final Form f = procedureForm;
			userMessages.confirm("Gravar os dados?", new ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					userMessages.removeConfirm();
					if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
						try {
							f.setValidationVisible(true);
							f.validate();
							saveData();
							if (procedureSynchronism.getItems() != null) {
								for (Synchronism child : procedureSynchronism.getItems()) {
									if (child.getId().longValue() < 0)
										child.setId(null);
								}
							}
							if (MobileServerData.save(app, procedureSynchronism)) {
								refreshAndClose(comp);
							}
						} catch (Exception e) {
							getWindow().showNotification("Atenção", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);

						}
					}
				}
			});
		}
	}

	private void cancelEditProcedure() {
		final ProcedureForm comp = this;
		if (this.getParent() instanceof TabSheet) {
			final UserMessages userMessages = new UserMessages(app.getMainWindow());
			userMessages.confirm("Cancelar a Edição do Procedimento?", new ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					userMessages.removeConfirm();
					refreshAndClose(comp);
				}
			});
		}
	}

	private void editParameter() {
		final ParameterSynchronism selectedParameter = (ParameterSynchronism) this.getSelectedParameter();
		if (selectedParameter != null) {
			final ParameterWindow parameterWindow = new ParameterWindow(app, selectedParameter, procedureSynchronism);
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
							enableActions();
						}
					}
				}
			});
		}
	}

	private void removeParameter() {
		final Synchronism selectedParameter = (Synchronism) this.getSelectedParameter();
		if (selectedParameter != null) {
			final UserMessages userMessages = new UserMessages(app.getMainWindow());
			userMessages.confirm("Remover o parâmetro " + selectedParameter.getName() + " ?", new ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					userMessages.removeConfirm();
					if (event.getButton().getData().equals(UserMessages.USER_CONFIRM_OK)) {
						gridParameters.getContainerDataSource().removeItem(selectedParameter.getId());
						if (procedureSynchronism.getItems() == null)
							procedureSynchronism.setItems(new LinkedHashSet<Synchronism>());
						procedureSynchronism.getItems().remove(selectedParameter);
						procedureSynchronism.renumberSequence();
						gridParameters.setContainerDataSource(getParametersDataSource(procedureSynchronism));
						enableActions();
					}
				}
			});
		}
	}

	private void addParameter() {
		final ParameterSynchronism parameter = new ParameterSynchronism();
		parameter.setId(new Date().getTime() * -1);
		parameter.setObjectOwner(procedureSynchronism);
		parameter.setSequence(procedureSynchronism.getLastSequence() + 1);
		final ParameterWindow parameterWindow = new ParameterWindow(app, parameter, procedureSynchronism);
		getWindow().addWindow(parameterWindow);
		parameterWindow.addListener(new CloseListener() {
			
			public void windowClose(CloseEvent e) {
				if (parameterWindow.getFormParameter().getLastAction() == UserMessages.USER_CONFIRM_OK) {
					addParameterDataSource((IndexedContainer) gridParameters.getContainerDataSource(), parameter);
					enableActions();
				}
			}
		});
	}

	public void refreshAndClose(final ProcedureForm comp) {
		changeDataTreeItem();
		app.removeTab(comp);
		if (procedureSynchronism.getId() != null)
			((HierarchicalContainer) app.getTree().getContainerDataSource()).removeItemRecursively(procedureSynchronism
					.getId());

		Item owner = app.getTree().getItem(objectOwner.getId());
		app.refreshTreeItem(owner, owner.getItemProperty(MobileServerData.PROPERTY_DATA).getValue());
		if (procedureSynchronism.getId() != null) {
			app.getTree().collapseItem(procedureSynchronism.getId());
			app.getTree().expandItemsRecursively(procedureSynchronism.getId());
			app.getTree().select(procedureSynchronism.getId());
		}
	}

	private void addParameterDataSource(IndexedContainer container, ParameterSynchronism param) {

		Item item = container.addItem(param.getId());
		item.getItemProperty("ID").setValue(param.getId());
		item.getItemProperty("SEQ").setValue(param.getSequence());
		item.getItemProperty("NAME").setValue(param.getName());
		item.getItemProperty("DESCRIPTION").setValue(param.getDescription());
		if (param.getParameterDataType() != null) {
			item.getItemProperty("PARAMETER_DATA_TYPE").setValue(
					FieldTypes.getFieldNameByValue(param.getParameterDataType().intValue() + ""));
		}
		item.getItemProperty("PARAMETER_TYPE")
				.setValue((param.getParameterType().intValue() == 0 ? "INPUT" : "OUTPUT"));
		item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(param);

		if (procedureSynchronism.getItems() == null)
			procedureSynchronism.setItems(new LinkedHashSet<Synchronism>());

		procedureSynchronism.getItems().add(param);
	}

	protected void changeDataTreeItem() {
		if (procedureSynchronism.getId() != null) {
			Item item = app.getTree().getItem(procedureSynchronism.getId());
			if (item == null) {
				item = app.getTree().addItem(procedureSynchronism.getId());
				((HierarchicalContainer) app.getTree().getContainerDataSource()).setParent(
						procedureSynchronism.getId(), procedureSynchronism.getObjectOwner().getId());
			}
			item.getItemProperty(MobileServerData.PROPERTY_NAME).setValue(
					procedureSynchronism.getName() + " " + procedureSynchronism.getId());
			item.getItemProperty(MobileServerData.PROPERTY_ICON).setValue(MobileServerData.TABLE_IMG);
			item.getItemProperty(MobileServerData.PROPERTY_DATA).setValue(procedureSynchronism);
			((HierarchicalContainer) app.getTree().getContainerDataSource()).setChildrenAllowed(
					procedureSynchronism.getId(), true);
		}
	}

	protected void saveData() {
		if (!StringUtils.isEmpty(fldId.getValue() + "")) {
			procedureSynchronism.setId(new Long(fldId.getValue() + ""));
		}
		procedureSynchronism.setName(fldName.getValue() + "");
		procedureSynchronism.setDescription(fldDescription.getValue() + "");
		procedureSynchronism.setProcedureParamOut(fldParameterOut.getValue() + "");
		procedureSynchronism.setObjectOwner(objectOwner);
		if (objectOwner.getItems() == null) {
			objectOwner.setItems(new HashSet<Synchronism>());
		}
		objectOwner.getItems().add(procedureSynchronism);
	}

	private void loadData() {
		if (procedureSynchronism.getId() != null)
			fldId.setValue(procedureSynchronism.getId());

		if (procedureSynchronism.getDescription() != null)
			fldDescription.setValue(procedureSynchronism.getDescription());

		if (procedureSynchronism.getName() != null)
			fldName.setValue(procedureSynchronism.getName());

		Container containerDataSource = fldName.getContainerDataSource();
		Iterator<?> it = containerDataSource.getItemIds().iterator();
		while (it.hasNext()) {
			Object id = it.next();
			Property property = containerDataSource.getContainerProperty(id, MobileServerData.PROPERTY_DATA);
			StoredProcedureSchema procedure = (StoredProcedureSchema) property.getValue();
			if (procedure.getName().equalsIgnoreCase(fldName.getValue() + ""))
				loadParametersOut(property);
		}

		if (procedureSynchronism.getProcedureParamOut() != null)
			fldParameterOut.setValue(procedureSynchronism.getProcedureParamOut());
	}

	public Object getSelectedParameter() {
		if (gridParameters.getValue() != null) {
			Item item = gridParameters.getItem(gridParameters.getValue());
			return item.getItemProperty(MobileServerData.PROPERTY_DATA).getValue();
		}
		return null;
	}

}
