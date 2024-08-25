package uma.autopsy.Devices;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uma.autopsy.Cases.Models.Case;

import java.sql.Timestamp;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name="device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="hardware_id", unique = true)
    private String hardwareId;

    @CreationTimestamp
    @Column(name="creation_date")
    private Timestamp creationDate;

    @OneToMany(mappedBy = "device")
    @JsonManagedReference
    private List<Case> cases;

    public Device(String hardwareId) {
        this.hardwareId = hardwareId;
    }
}
