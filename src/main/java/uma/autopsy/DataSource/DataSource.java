package uma.autopsy.DataSource;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uma.autopsy.Cases.Case;
import uma.autopsy.Devices.Device;

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

    @ManyToOne
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    @JsonBackReference
    private Case caseEntity;

    @Column(name="file_type")
    private int fileType;

    @NotBlank(message = "File is mandatory")
    @Column(name="file_path")
    private String filePath;

    @Column(name="ignore_orphan_files")
    private boolean ignoreOrphanFiles;

    @NotBlank(message = "Time Zone is mandatory")
    @Column(name="time_zone")
    private String timeZone;

    @NotBlank(message = "Sector Size is mandatory")
    @Column(name="sector_size")
    private int sectorSize;

    @Column(name="md5_hash")
    private String md5Hash;

    @Column(name="sha1_hash")
    private String sha1Hash;

    @Column(name="sha256_hash")
    private String sha256Hash;

}
