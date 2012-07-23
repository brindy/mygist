package com.brindysoft.mygist.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mygist.api.LanguageFormatter;
import com.brindysoft.mygist.api.model.Gist;
import com.brindysoft.mygist.app.FileComposite.GistFileListener;
import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

@Component(factory = "com.vaadin.Application/mygist")
public class MyGistApp extends Application implements GistFileListener {

	private static final long serialVersionUID = 1L;

	private Window mainWindow = new Window("My Gist");

	private TextField descriptionField;

	private ComponentFactory fileCompositeFactory;

	private ComponentFactory gistRendererCompositeFactory;

	private VerticalLayout files;

	private Button previewButton;

	private Set<LanguageFormatter> formatters = new HashSet<LanguageFormatter>();

	private Map<Object, ComponentInstance> instances = new HashMap<Object, ComponentInstance>();

	@Override
	public void gistFileChanged(FileComposite source) {
		validate();
	}

	@Override
	public void gistFileRemoved(FileComposite source) {
		dispose(source);
	}

	@Reference(dynamic = true, multiple = true, optional = true)
	public void addFormatter(LanguageFormatter formatter) {
		formatters.add(formatter);
	}

	public void removeFormatter(LanguageFormatter formatter) {
		formatters.remove(formatter);
	}

	@Reference(target = "(component.factory=mygist.FileComposite)")
	public void setFileCompositeFactory(ComponentFactory factory) {
		this.fileCompositeFactory = factory;
	}

	@Reference(target = "(component.factory=mygist.GistRendererComposite)")
	public void setGistRendererCompositeFactory(ComponentFactory factory) {
		this.gistRendererCompositeFactory = factory;
	}

	@Override
	public void init() {
		createUI();
		addFileInstance();
	}

	@Deactivate
	public void dispose() {
		for (ComponentInstance instance : instances.values()) {
			instance.dispose();
		}
	}

	private void createUI() {
		setTheme("mygist");
		setMainWindow(mainWindow);

		mainWindow.addComponent(createHeader());
		mainWindow.addComponent(createMainLayout());
	}

	private com.vaadin.ui.Component createMainLayout() {
		VerticalLayout leftColumn = createLeftColumn();
		VerticalLayout rightColumn = createRightColumn();

		GridLayout twoColumns = new GridLayout(2, 1);
		twoColumns.setWidth("100%");
		twoColumns.addComponent(leftColumn);
		twoColumns.addComponent(rightColumn);

		twoColumns.setColumnExpandRatio(0, 2.0f);
		twoColumns.setColumnExpandRatio(1, 1.0f);

		return twoColumns;
	}

	private VerticalLayout createRightColumn() {
		Panel infoPanel = createInfoPanel();

		VerticalLayout rightColumn = new VerticalLayout();
		rightColumn.setWidth("100%");
		rightColumn.setMargin(true);
		rightColumn.addComponent(infoPanel);
		return rightColumn;
	}

	private Panel createInfoPanel() {
		Panel infoPanel = new Panel();
		Label info = new Label();
		info.setValue("<strong>MyGist</strong> is a simple way to <strong>share snippets and pastes</strong> with others.  "
				+ "It is inspired by <strong>Gist</strong> on <strong>GitHub.com</strong> but is intended "
				+ "for personal/internal use.<p/>  "
				+ "It uses <strong>OSGi</strong> to allow for pluggable language formatters, "
				+ "and perhaps other features in the future.<p/>"
				+ "It uses <strong>Vaadin</strong> to allow for a rich user experience.<p/>");
		info.setContentMode(Label.CONTENT_XHTML);
		info.setWidth("100%");
		info.addStyleName("info");
		infoPanel.addComponent(info);
		return infoPanel;
	}

	private VerticalLayout createLeftColumn() {
		Button addAnotherButton = createAddButton();
		Button createGistButton = createPreviewGistButton();

		HorizontalLayout bottomButtons = new HorizontalLayout();
		bottomButtons.setWidth("100%");
		bottomButtons.addComponent(addAnotherButton);
		bottomButtons.addComponent(createGistButton);
		bottomButtons.setComponentAlignment(createGistButton, Alignment.BOTTOM_RIGHT);

		createDescriptionField();
		createFilesHolder();

		VerticalLayout leftColumn = new VerticalLayout();
		leftColumn.setWidth("100%");
		leftColumn.setMargin(true);
		leftColumn.addComponent(descriptionField);
		leftColumn.addComponent(files);
		leftColumn.addComponent(bottomButtons);

		return leftColumn;
	}

	private Button createPreviewGistButton() {
		previewButton = new Button("Preview Gist");
		previewButton.setEnabled(false);

		previewButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				showPreview();
			}
		});

		return previewButton;
	}

	private Button createAddButton() {
		Button addAnotherButton = new Button("Add another file...");
		addAnotherButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				addFileInstance();
			}
		});
		return addAnotherButton;
	}

	private void createFilesHolder() {
		files = new VerticalLayout();
		files.setWidth("100%");
	}

	private void createDescriptionField() {
		descriptionField = new TextField();
		descriptionField.setWidth("100%");
		descriptionField.setInputPrompt("Gist description...");
		descriptionField.setImmediate(true);
		descriptionField.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				validate();
			}
		});
	}

	private void addFileInstance() {
		FileComposite composite = createFileComposite();
		composite.setGistFileChangedListener(this);
		files.addComponent(composite);
		if (files.getComponentCount() == 1) {
			composite.setCloseable(false);
		}
	}

	private FileComposite createFileComposite() {
		ComponentContext ctx = (ComponentContext) fileCompositeFactory.newInstance(null);
		ComponentInstance componentInstance = ctx.getComponentInstance();
		FileComposite composite = (FileComposite) componentInstance.getInstance();
		instances.put(composite, componentInstance);
		return composite;
	}

	private Label createHeader() {
		Label header = new Label();
		header.addStyleName("h1");
		header.setValue("MyGist");
		return header;
	}

	private void showPreview() {

		Window w = new Window("Preview");
		w.setWidth("800px");
		w.setHeight("600px");
		w.setModal(true);
		w.setResizable(false);
		mainWindow.addWindow(w);
		w.center();

		Gist gist = createGist();
		final GistRendererComposite renderer = createRendererComposite();
		renderer.setGist(gist);
		w.getContent().addComponent(renderer);

		w.addListener(new CloseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {
				dispose(renderer);
			}
		});

	}

	private GistRendererComposite createRendererComposite() {
		ComponentContext ctx = (ComponentContext) gistRendererCompositeFactory.newInstance(null);
		ComponentInstance componentInstance = ctx.getComponentInstance();
		GistRendererComposite composite = (GistRendererComposite) componentInstance.getInstance();
		instances.put(composite, componentInstance);
		return composite;
	}

	private Gist createGist() {
		Gist gist = new Gist();
		gist.setDescription((String) descriptionField.getValue());
		int compositeCount = files.getComponentCount();
		for (int i = 0; i < compositeCount; i++) {
			FileComposite composite = (FileComposite) files.getComponent(i);
			gist.addFile(composite.createGistFile());
		}
		return gist;
	}

	private void validate() {

		int compositeCount = files.getComponentCount();
		int validCount = 0;
		for (int i = 0; i < compositeCount; i++) {
			FileComposite composite = (FileComposite) files.getComponent(i);
			if (composite.isValid()) {
				validCount++;
			}
		}

		boolean filesValid = compositeCount == validCount;

		String desc = (String) descriptionField.getValue();
		boolean descValid = desc.trim().length() > 0;

		previewButton.setEnabled(filesValid && descValid);
	}

	private void dispose(Object component) {
		ComponentInstance instance = instances.get(component);
		if (null != instance) {
			instance.dispose();
		}
	}

}