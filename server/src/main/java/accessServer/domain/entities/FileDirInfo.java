package accessServer.domain.entities;


import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
//@ToString(exclude = "parent")
@Entity(name="file_dir_info")
@NamedQueries({
        @NamedQuery(name = "FileDirInfo.findById",
                query = "SELECT f FROM file_dir_info f where f.id = :id"),
        @NamedQuery(name="FileDirInfo.fileExists",
            query="select f from file_dir_info f where f.name=:name and f.parent.name=:parent"),
        @NamedQuery(name="FileDirInfo.getChildren",
                query="select f from file_dir_info f where f.parent.id=:parent"),
        @NamedQuery(name="FileDirInfo.getRoot",
                query="select f from file_dir_info f where f.id=1"),
        @NamedQuery(name="FileDirInfo.getRootDirs",
                query="select f from file_dir_info f where f.parent.id=1"),
        @NamedQuery(name="FileDirInfo.findByName",
                query="select f from file_dir_info f where f.name=:name"),

})
public class FileDirInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id",nullable =false)
    private Integer id;

    @Column(name="name",nullable =false)
    private String name;

    @Column(name="created_date",updatable = false)
    @CreationTimestamp
    private Date createdDate;

    @Column(name="last_modified_date")
    @UpdateTimestamp
    private Date lastModifiedDate;

    @Column(name="is_dir",nullable =false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isDir = false;

    @Column(name="status", nullable = false)
    private String status = "Ready";

    @Column(name="status_by_user")
    private String statusByUser;

    @ManyToOne(cascade={CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name="parent")
    private FileDirInfo parent;

}
