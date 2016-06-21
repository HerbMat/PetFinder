package com.petfinder.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "advertisements")
public class Advertisement extends AbstractPersistable<Long>
        implements Serializable {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, length = 1048576)
    private String content;

    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", nullable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastEditedDate")
    private Date lastEditedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToMany
    @JoinTable(name = "advertisement_tags",
            joinColumns = @JoinColumn(name = "advertisement_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id",
                    referencedColumnName = "id"))
    private List<Tag> tags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "advertisement")
    private List<Attachment> attachments;

    public Advertisement() {
        super();
    }

    public Advertisement(String title, String content, User user, Pet pet,
                         Location location, List<Tag> tags,
                         List<Attachment> attachments) {
        super();
        this.title = title;
        this.content = content;
        this.isDeleted = false;
        this.createdDate = new Date();
        this.lastEditedDate = null;
        this.user = user;
        this.pet = pet;
        this.location = location;
        this.tags = tags;
        this.attachments = attachments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return String.format(
                "Advertisement<#%d, title=%s, createdDate=%s, user=%s>",
                getId(),
                getTitle(),
                getCreatedDate().toString(),
                getUser().toString()
        );
    }
}
