package com.brindysoft.mygist.app;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mygist.api.LanguageFormatter;
import com.brindysoft.mygist.api.model.GistFile;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Component(factory = "mygist.FileComposite")
public class FileComposite extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private TextField fileNameField;
	private ComboBox languageCombo;
	private TextArea pasteArea;
	private Button closeButton;

	private Set<LanguageFormatter> formatters = new TreeSet<LanguageFormatter>(new LanguageFormatterNameComparator());

	private Label languageLabel;

	private GistFileListener listener;

	private LanguageFormatter plainTextFormatter;

	public FileComposite() {
		createUI();
		updateLanguage();
	}

	@Reference(dynamic = true, multiple = true, optional = true)
	public void addFormatter(LanguageFormatter formatter) {

		if ("Plain Text".equals(formatter.getName())) {
			plainTextFormatter = formatter;
		}

		formatters.add(formatter);
		if (null != languageCombo) {
			updateLanguage();
		}
	}

	public void removeFormatter(LanguageFormatter formatter) {
		formatters.remove(formatter);
		if (null != languageCombo) {
			updateLanguage();
		}
	}

	public void setCloseable(boolean closeable) {
		closeButton.setVisible(false);
	}

	public boolean isValid() {
		String content = (String) pasteArea.getValue();
		return content.trim().length() > 0;
	}

	public GistFile createGistFile() {
		GistFile file = new GistFile();
		file.setFileName((String) fileNameField.getValue());
		file.setContent((String) pasteArea.getValue());
		LanguageFormatter formatter = (LanguageFormatter) languageCombo.getValue();
		file.setLanguageType(formatter.getName());
		return file;
	}

	public void setGistFileChangedListener(GistFileListener listener) {
		this.listener = listener;
	}

	private void createUI() {
		setMargin(true);

		createNameField();
		createPasteArea();
		createCloseButton();

		HorizontalLayout languageHolder = createLanguageHolder();
		HorizontalLayout nameLanguageRow = createGistNameLanguageHolder(languageHolder);

		VerticalLayout pasteRow = createPasteRow();

		addComponent(nameLanguageRow);
		addComponent(pasteRow);
		addComponent(closeButton);

		setComponentAlignment(closeButton, Alignment.BOTTOM_RIGHT);
	}

	private VerticalLayout createPasteRow() {
		VerticalLayout pasteRow = new VerticalLayout();
		pasteRow.setWidth("100%");
		pasteRow.addStyleName("otherrow");
		pasteRow.addComponent(pasteArea);
		pasteRow.setComponentAlignment(pasteArea, Alignment.TOP_CENTER);
		return pasteRow;
	}

	private HorizontalLayout createGistNameLanguageHolder(HorizontalLayout languageHolder) {
		HorizontalLayout inputRow = new HorizontalLayout();
		inputRow.setWidth("100%");
		inputRow.addComponent(fileNameField);
		inputRow.addComponent(languageHolder);
		inputRow.setComponentAlignment(languageHolder, Alignment.TOP_RIGHT);
		return inputRow;
	}

	private void createCloseButton() {
		closeButton = new Button("Remove this file");
		closeButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				((AbstractLayout) getParent()).removeComponent(FileComposite.this);
				listener.gistFileRemoved(FileComposite.this);
			}
		});
	}

	private void createPasteArea() {
		pasteArea = new TextArea();
		pasteArea.setWidth("100%");
		pasteArea.setHeight("300px");
		pasteArea.setImmediate(true);
		pasteArea.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				fireChangedEvent();
			}
		});
	}

	private HorizontalLayout createLanguageHolder() {
		languageLabel = new Label("Language:");
		languageCombo = new ComboBox();
		languageCombo.setInputPrompt("Language");
		languageCombo.setImmediate(true);

		HorizontalLayout languageHolder = new HorizontalLayout();
		languageHolder.addComponent(languageLabel);
		languageHolder.addComponent(languageCombo);
		languageHolder.setComponentAlignment(languageLabel, Alignment.MIDDLE_RIGHT);
		return languageHolder;
	}

	private void createNameField() {
		fileNameField = new TextField();
		fileNameField.setInputPrompt("name this file...");
		fileNameField.setWidth("200px");
	}

	private void updateLanguage() {

		languageCombo.removeAllItems();
		for (LanguageFormatter formatter : formatters) {
			languageCombo.addItem(formatter);
			languageCombo.setItemCaption(formatter, formatter.getName());
		}

		languageCombo.setValue(plainTextFormatter);
	}

	private void fireChangedEvent() {
		listener.gistFileChanged(this);
	}

	class LanguageFormatterNameComparator implements Comparator<LanguageFormatter> {

		@Override
		public int compare(LanguageFormatter formatter1, LanguageFormatter formatter2) {
			return formatter1.getName().compareTo(formatter2.getName());
		}

	}

	static interface GistFileListener {

		void gistFileChanged(FileComposite source);

		void gistFileRemoved(FileComposite source);

	}

}
