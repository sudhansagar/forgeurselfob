package com.forgeurself.ob.entities;

import javax.persistence.*;

@MappedSuperclass
public class AbstractEntity {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
