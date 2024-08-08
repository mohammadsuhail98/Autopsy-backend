package uma.autopsy.Cases;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.validation.annotation.Validated;
import uma.autopsy.Devices.Device;

import java.sql.Date;
import java.sql.Timestamp;

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

    public void setDevice(Device device) {
        this.device = device;
        if (device != null) {
            this.deviceId = device.getHardwareId();
        }
    }

}
