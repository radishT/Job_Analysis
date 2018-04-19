package com.edmund.vo;

/**
 * 职位信息封装类
 * 
 * @author Edmund
 *
 */
public class Job {
	private Integer jid;
	private String city;
	private String key;
	private String title;
	private String salary;
	private String company;
	private String job;
	private String education;
	private String experience;

	public Job() {
		super();
	}

	public Job(Integer jid, String city, String key, String title, String salary, String company, String job,
			String education, String experience) {
		super();
		this.jid = jid;
		this.city = city;
		this.key = key;
		this.title = title;
		this.salary = salary;
		this.company = company;
		this.job = job;
		this.education = education;
		this.experience = experience;
	}

	public Integer getJid() {
		return jid;
	}

	public void setJid(Integer jid) {
		this.jid = jid;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	@Override
	public String toString() {
		return "Job [jid=" + jid + ", city=" + city + ", key=" + key + ", title=" + title + ", salary=" + salary
				+ ", company=" + company + ", job=" + job + ", education=" + education + ", experience=" + experience
				+ "]";
	}

}
