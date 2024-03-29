package com.predu.evertask.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.predu.evertask.event.listener.AuditRevisionListener;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "revision_info")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@RevisionEntity(AuditRevisionListener.class)
@AttributeOverride(name = "timestamp", column = @Column(name = "rev_timestamp"))
@AttributeOverride(name = "id", column = @Column(name = "revision_id"))
public class AuditRevisionEntity extends DefaultRevisionEntity implements Serializable {

    @Column(name = "username")
    private String username;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditRevisionEntity that)) return false;
        if (!super.equals(o)) return false;

        return getUsername() != null ? getUsername().equals(that.getUsername()) : that.getUsername() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        return result;
    }
}
