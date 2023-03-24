package org.tdl.vireo.model.simple;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "named_search_filter_group")
public class SimpleNamedSearchFilterGroup implements Serializable {

    @Transient
    private static final long serialVersionUID = 483874224486014344L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Long userId;

    @Column(insertable = false, updatable = false, nullable = true)
    private String name;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean publicFlag;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean columnsFlag;

    @Column(insertable = false, updatable = false, nullable = false)
    private Boolean umiRelease;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublicFlag() {
        return publicFlag;
    }

    public void setPublicFlag(Boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    public Boolean getColumnsFlag() {
        return columnsFlag;
    }

    public void setColumnsFlag(Boolean columnsFlag) {
        this.columnsFlag = columnsFlag;
    }

    public Boolean getUmiRelease() {
        return umiRelease;
    }

    public void setUmiRelease(Boolean umiRelease) {
        this.umiRelease = umiRelease;
    }

}
