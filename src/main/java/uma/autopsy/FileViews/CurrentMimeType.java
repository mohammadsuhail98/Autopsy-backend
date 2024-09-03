package uma.autopsy.FileViews;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class CurrentMimeType {

    private static final AtomicInteger counter = new AtomicInteger();
    private int id;
    private String name;
    private List<CurrentMimeSubtype> mimeSubtypes;

    public CurrentMimeType() {
        this.id = counter.incrementAndGet();
    }

    @Getter @Setter
    public static class CurrentMimeSubtype {
        private int id;
        private String name;
        private String fullName;
        private long count;

        public CurrentMimeSubtype() {
            this.id = counter.incrementAndGet();
        }
    }

}
