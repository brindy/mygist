package com.brindysoft.mygist.app;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mygist.api.LanguageFormatter;
import com.brindysoft.mygist.api.model.Gist;
import com.brindysoft.mygist.api.model.GistFile;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@Component(factory = "mygist.GistRendererComposite")
public class GistRendererComposite extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private Panel infoPanel;
	private Gist gist;
	private Map<String, LanguageFormatter> formatters = new HashMap<String, LanguageFormatter>();

	@Reference(dynamic = true, multiple = true, optional = true)
	public void addFormatter(LanguageFormatter formatter) {
		formatters.put(formatter.getName(), formatter);
	}

	public GistRendererComposite() {
	}

	public void setGist(Gist gist) {
		this.gist = gist;

		removeAllComponents();

		addInfoPanel();
		addFiles();
	}

	private void addFiles() {

		for (GistFile file : gist) {
			addFile(file);
		}

	}

	private void addFile(GistFile file) {

		Label titleLabel = new Label(file.getFileName());
		titleLabel.addStyleName("h2");
		titleLabel.addStyleName("gist-file-title");

		// TODO show type

		LanguageFormatter formatter = getFormatter(file);
		String[] htmlLines = formatter.toHTMLLines(file.getContent());

		int lineNumber = 0;

		StringBuffer lineNumbers = new StringBuffer();
		StringBuffer code = new StringBuffer();
		
		for (String line : htmlLines) {
			lineNumbers.append("<span>").append(String.valueOf(++lineNumber)).append("</span><br/>");
			code.append(line).append("<br/>");
		}

		Label lineNumbersLabel = new Label(lineNumbers.toString(), Label.CONTENT_XHTML);
		lineNumbersLabel.setWidth("100%");
		lineNumbersLabel.addStyleName("line-numbers");

		Label codeLine = new Label(code.toString(), Label.CONTENT_XHTML);
		codeLine.setWidth("100%");
		codeLine.addStyleName("code");

		GridLayout codeLayout = new GridLayout(20, 1);
		codeLayout.addStyleName("code-layout");
		codeLayout.setWidth("100%");
		codeLayout.addComponent(lineNumbersLabel, 0, 0);
		codeLayout.addComponent(codeLine, 1, 0, 19, 0);

		VerticalLayout vbox = new VerticalLayout();
		vbox.setMargin(true, false, false, false);
		vbox.addComponent(titleLabel);
		vbox.addComponent(codeLayout);
		addComponent(vbox);
	}

	private LanguageFormatter getFormatter(GistFile file) {
		LanguageFormatter formatter = formatters.get(file.getLanguageType());
		return formatter;
	}

	private void addInfoPanel() {

		infoPanel = new Panel();
		infoPanel.setContent(new VerticalLayout());

		addGistIdToPanel();
		addDescriptionToPanel();

		addComponent(infoPanel);

	}

	private void addDescriptionToPanel() {

		Label label = new Label(gist.getDescription());
		infoPanel.getContent().addComponent(label);

	}

	private void addGistIdToPanel() {
		Label label = new Label("gist: " + gist.getId());
		label.addStyleName("h1");
		infoPanel.getContent().addComponent(label);
	}

}
