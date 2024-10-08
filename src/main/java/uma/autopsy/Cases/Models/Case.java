package uma.autopsy.Cases.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uma.autopsy.DataSource.DataSource;
import uma.autopsy.Devices.Device;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@Getter@Setter
@NoArgsConstructor
@Table(name="case_entity")
@EntityListeners(AuditingEntityListener.class)
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @NotBlank(message = "Device ID is mandatory")
    @Column(name="device_hardware_id")
    private String deviceId;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "hardware_id")
    @JsonBackReference
    private Device device;

    @Column(name="case_path")
    private String casePath;

    @CreationTimestamp
    @Column(name="creation_date")
    private Timestamp creationDate;

    @NotBlank(message = "Name is mandatory")
    @Column(name="name")
    private String name;

    @Column(name="number")
    private int number;

    @Column(name="type")
    private int type;

    @Column(name="examiner_name")
    private String examinerName;

    @Column(name="examiner_phone")
    private String examinerPhone;

    @Column(name="examiner_email")
    private String examinerEmail;

    @Column(name="examiner_notes")
    private String examinerNotes;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DataSource> dataSourceList;

    public void setDevice(Device device) {
        this.device = device;
        if (device != null) {
            this.deviceId = device.getHardwareId();
        }
    }

}
