package org.tdl.vireo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity implements Comparable<BaseEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        // if we're the same entity type
        if (obj != null && obj.getClass().equals(this.getClass())) {
            // and we have the same Id
            Long objId = ((BaseEntity) obj).getId();
            if (objId != null) {
                return objId.equals(this.getId());
            } else {
                return objId == this.getId();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (getId() == null ? 0 : getId().hashCode());
        return hashCode;
    }

    @Override
    public int compareTo(BaseEntity o) {
        if (this.getId() < o.getId()) {
            return -1;
        } else if (this.getId() > o.getId()) {
            return 1;
        }
        return 0;
    }

}
