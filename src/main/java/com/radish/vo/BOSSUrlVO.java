package com.radish.vo;
/**
 * 对应
 * 北京		北京		https://www.zhipin.com/job_detail/?query=#&scity=101010100
 * @author admin
 *
 */
public class BOSSUrlVO {
	private String province;
	private String city;
	private String url;
	
	
	public BOSSUrlVO() {
		super();
	}
	public BOSSUrlVO(String province, String city, String url, String key) {
		super();
		this.province = province;
		this.city = city;
		this.url = url.replace("#", key);
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((province == null) ? 0 : province.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BOSSUrlVO other = (BOSSUrlVO) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (province == null) {
			if (other.province != null)
				return false;
		} else if (!province.equals(other.province))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BOSSUrlVO [province=" + province + ", city=" + city + ", url=" + url + "]";
	}
	
}
