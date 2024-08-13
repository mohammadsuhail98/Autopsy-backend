package uma.autopsy.DataSource;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uma.autopsy.Cases.Case;
import uma.autopsy.Devices.Device;

import java.sql.Timestamp;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name="data_source")
@EntityListeners(AuditingEntityListener.class)
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="data_source_device_id")
    private String dataSourceDeviceId;

    @Column(name="data_source_id")
    private long dataSourceId;

    @ManyToOne
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    @JsonBackReference
    private Case caseEntity;

    @Column(name="name")
    private String name;

    @CreationTimestamp
    @Column(name="creation_date")
    private Timestamp creationDate;

    @Column(name="file_type")
    private String fileType;

    @Column(name="ignore_orphan_files")
    private boolean ignoreOrphanFiles;

    @Column(name="add_unalloc_space")
    private boolean addUnAllocSpace;

    @Column(name="time_zone")
    private String timeZone;

    @Column(name="sector_size")
    private int sectorSize;

    @Column(name="md5_hash")
    private String md5Hash;

    @Column(name="sha1_hash")
    private String sha1Hash;

    @Column(name="sha256_hash")
    private String sha256Hash;

    @Column(name="size")
    private long size;

    @Column(name="non_severe_errors")
    private String errors;
}
