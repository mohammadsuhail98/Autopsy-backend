package uma.autopsy.DataSourceContent.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class VolumeInfo {
    private static final AtomicInteger counter = new AtomicInteger();
    private int id;
    private long startingSector;
    private long lengthInSectors;
    private String description;
    private String flags;

    public VolumeInfo() {
        this.id = counter.incrementAndGet();
    }

    public VolumeInfo(long startingSector, long lengthInSectors, String description, String flags) {
        this.id = counter.incrementAndGet();
        this.startingSector = startingSector;
        this.lengthInSectors = lengthInSectors;
        this.description = description;
        this.flags = flags;
    }
}
