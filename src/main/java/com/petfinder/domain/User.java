package com.petfinder.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="users")
public class User extends AbstractPersistable<Long> implements Serializable {
   
	@Column(name = "login", unique = true, nullable = false)
    private String login;
	
	@Column(name = "email", unique = true, nullable = false)
    private String email;
	
	@Column(name = "password", nullable = false)
    private String password;
	
	@Column(name = "isActivated", nullable = false)
    private boolean isActivated;
	
	@Column(name = "isAdmin", nullable = false)
    private boolean isAdmin = false;

	@Column(name = "isBanned", nullable = false)
    private boolean isBanned;
	
	@Column(name = "emailNotification", nullable = false)
    private boolean emailNotification;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registrationDate", nullable = false)
    private Date registrationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastLoginDate")
    private Date lastLoginDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Advertisement> advertisements;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private List<Pet> pets;

	public User() {
		super();
	}

    public User(String login, String email, String password){
        super();
    	this.login = login;
    	this.email=email;
    	this.password=password;
    	this.isBanned = false;
    	this.isActivated = false;
    	this.emailNotification = false;
    	this.registrationDate = new Date();
        this.advertisements = new ArrayList<>();
        this.pets = new ArrayList<>();
        this.isAdmin = false;
    }
    
	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	public boolean isEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(boolean emailNotification) {
		this.emailNotification = emailNotification;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

    public List<Advertisement> getAdvertisements() {
        return advertisements;
    }

    public void setAdvertisements(List<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
    
    public String getRole()
    {
    	return "ROLE_USER";
    }

    @Override
    public String toString() {
        return String.format(
                "User<#%d, login=%s, email=%s, isActivated=%b, isBanned=%b>",
                getId(),
                getLogin(),
                getEmail(),
                isActivated(),
                isBanned()
        );
    }
}
