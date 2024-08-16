package uma.autopsy.DataSourceContent.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MimeType {
    private int id;
    private String name;
    private boolean isSupported;
}