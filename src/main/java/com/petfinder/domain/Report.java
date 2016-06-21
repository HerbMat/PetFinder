package com.petfinder.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "report")
public class Report extends AbstractPersistable<Long> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", nullable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastEditedDate")
    private Date lastEditedDate;

    public Report() {
        super();
    }

    public Report(Advertisement advertisement, User user) {
        super();
        this.advertisement = advertisement;
        this.user = user;
        this.createdDate = new Date();
        this.lastEditedDate = null;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastEditedDate() {
        return lastEditedDate;
    }

    public void setLastEditedDate(Date lastEditedDate) {
        this.lastEditedDate = lastEditedDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Pet<#%d, advertisement=%s, user=%s, createdDate=%s>",
                getId(),
                getAdvertisement().getTitle(),
                getUser().getLogin(),
                getCreatedDate().toString()
        );
    }
}
