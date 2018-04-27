package com.edmund.vo;

import java.util.Map;

public class KeyMap {
	private int id;
	private String keyword;
	private Map<String, Integer> keywords;

	public KeyMap() {
		super();
	}

	public KeyMap(int id, String keyword, Map<String, Integer> keywords) {
		super();
		this.id = id;
		this.keyword = keyword;
		this.keywords = keywords;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Map<String, Integer> getKeywords() {
		return keywords;
	}

	public void setKeywords(Map<String, Integer> keywords) {
		this.keywords = keywords;
	}

	@Override
	public String toString() {
		return "KeyMap [id=" + id + ", keyword=" + keyword + ", keywords="
				+ keywords + "]";
	}

}
