package com.ncl.sindhu.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Mails.
 */
@Entity
@Table(name = "mails")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "mails")
public class Mails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jhi_from", nullable = false)
    private String from;

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotNull
    @Column(name = "mail", nullable = false)
    private String mail;

    @NotNull
    @Column(name = "recdate", nullable = false)
    private LocalDate recdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public Mails from(String from) {
        this.from = from;
        return this;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public Mails subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMail() {
        return mail;
    }

    public Mails mail(String mail) {
        this.mail = mail;
        return this;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public LocalDate getRecdate() {
        return recdate;
    }

    public Mails recdate(LocalDate recdate) {
        this.recdate = recdate;
        return this;
    }

    public void setRecdate(LocalDate recdate) {
        this.recdate = recdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mails mails = (Mails) o;
        if (mails.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), mails.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Mails{" +
            "id=" + getId() +
            ", from='" + getFrom() + "'" +
            ", subject='" + getSubject() + "'" +
            ", mail='" + getMail() + "'" +
            ", recdate='" + getRecdate() + "'" +
            "}";
    }
}
