package com.brindysoft.mygist.api.model;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Gist implements Iterable<GistFile> {

	private String description;

	private List<GistFile> files = new LinkedList<GistFile>();

	private Date created = new Date();

	private String id = UUID.randomUUID().toString();

	@Override
	public Iterator<GistFile> iterator() {
		return files.iterator();
	}

	public void addFile(GistFile file) {
		files.add(file);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreated() {
		return created;
	}

	public String getId() {
		return id;
	}

}
