package uma.autopsy.FileViews;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CurrentMimeType {

    private String name;
    private List<CurrentMimeSubtype> mimeSubtypes;

    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor
    public static class CurrentMimeSubtype {
        private String name;
        private String fullName;
        private long count;
    }

}
