package com.edmund.vo;

import java.util.Map;

public class LGJob {
	private Integer id;
	private String keyword;
	private String job;
	private String salary;
	private String city;
	private String experience;
	private String education;
	private String company;
	private Map<String, Integer> keywords;

	public LGJob() {
		super();
	}

	public LGJob(Integer id, String keyword, String job, String salary,
			String city, String experience, String education, String company,
			Map<String, Integer> keywords) {
		super();
		this.id = id;
		this.keyword = keyword;
		this.job = job;
		this.salary = salary;
		this.city = city;
		this.experience = experience;
		this.education = education;
		this.company = company;
		this.keywords = keywords;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Map<String, Integer> getKeywords() {
		return keywords;
	}

	public void setKeywords(Map<String, Integer> keywords) {
		this.keywords = keywords;
	}

	@Override
	public String toString() {
		return "LGJob [id=" + id + ", keyword=" + keyword + ", job=" + job
				+ ", salary=" + salary + ", city=" + city + ", experience="
				+ experience + ", education=" + education + ", company="
				+ company + ", keywords=" + keywords + "]";
	}

}
