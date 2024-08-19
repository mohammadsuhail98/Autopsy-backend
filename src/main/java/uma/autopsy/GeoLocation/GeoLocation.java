package uma.autopsy.GeoLocation;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation {
    private long id;
    private String fileName;
    private String latitude;
    private String longitude;
    private String altitude;
    private String timestamp;
    private String deviceModel;
    private String deviceName;
    private String file;
}
