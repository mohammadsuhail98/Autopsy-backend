package uma.autopsy.DataSourceContent.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class VolumeInfo {
    private long startingSector;
    private long lengthInSectors;
    private String description;
    private String flags;
}
