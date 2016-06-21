package com.petfinder.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "attachments")
public class Attachment extends AbstractPersistable<Long>
        implements Serializable {

    public enum Type {
        IMAGE, VIDEO
    }

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    public Attachment() {
        super();
    }

    public Attachment(String uri, Type type, Advertisement advertisement) {
        super();
        this.uri = uri;
        this.type = type.name();
        this.advertisement = advertisement;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Type getType() {
        return Type.valueOf(type);
    }

    public String getTypeString() {
        return type;
    }

    public void setType(Type type) {
        this.type = type.name();
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    @Override
    public String toString() {
        return String.format("Attachment<#%d, uri=%s, type=%s>",
                getId(),
                getUri(),
                getType().name()
        );
    }
}
