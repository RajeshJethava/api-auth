package com.auth.api.model;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseIdEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}