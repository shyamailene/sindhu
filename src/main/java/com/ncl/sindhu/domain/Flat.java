package com.ncl.sindhu.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Flat.
 */
@Entity
@Table(name = "flat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "flat")
public class Flat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "flat_id", nullable = false)
    private String flatId;

    @Column(name = "flat_desc")
    private String flatDesc;

    @Column(name = "block_id")
    private Integer blockId;

    @ManyToOne(optional = false)
    @NotNull
    private Block blockflat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlatId() {
        return flatId;
    }

    public Flat flatId(String flatId) {
        this.flatId = flatId;
        return this;
    }

    public void setFlatId(String flatId) {
        this.flatId = flatId;
    }

    public String getFlatDesc() {
        return flatDesc;
    }

    public Flat flatDesc(String flatDesc) {
        this.flatDesc = flatDesc;
        return this;
    }

    public void setFlatDesc(String flatDesc) {
        this.flatDesc = flatDesc;
    }

    public Integer getBlockId() {
        return blockId;
    }

    public Flat blockId(Integer blockId) {
        this.blockId = blockId;
        return this;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public Block getBlockflat() {
        return blockflat;
    }

    public Flat blockflat(Block block) {
        this.blockflat = block;
        return this;
    }

    public void setBlockflat(Block block) {
        this.blockflat = block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Flat flat = (Flat) o;
        if (flat.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), flat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Flat{" +
            "id=" + getId() +
            ", flatId='" + getFlatId() + "'" +
            ", flatDesc='" + getFlatDesc() + "'" +
            ", blockId='" + getBlockId() + "'" +
            "}";
    }
}
