package uma.autopsy.DataSourceContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileNode {
    private String name;
    private String path;
    private String type;
    private long id;
    private int uid;
    private int gid;
    private boolean isDir;
    private boolean isFile;
    private boolean isRoot;
    private long size;
    private String flagsDir;
    private String flagsMeta;
    private String known;
    private String md5Hash;
    private String sha1Hash;
    private String sha256Hash;
    private String mimeType;
    private String extension;
    private short fileType;
    private List<FileNode> children;
    private String mTime = "";
    private String cTime = "";
    private String aTime = "";
    private String crTime = "";
    private String fileSystemType = "";
    private List<String> metaDataText;

    public FileNode(String name, String path, String type, long id,
                    int uid, int gid, boolean isDir, boolean isFile, boolean isRoot,
                    long size, String flagsDir, String flagsMeta,
                    String known, String md5Hash, String sha1Hash,
                    String sha256Hash, String mimeType, String extension,
                    short fileType, String mTime, String cTime, String aTime,
                    String crTime, String fileSystemType, List<String> metaDataText) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.id = id;
        this.uid = uid;
        this.gid = gid;
        this.isDir = isDir;
        this.isFile = isFile;
        this.isRoot = isRoot;
        this.size = size;
        this.flagsDir = flagsDir;
        this.flagsMeta = flagsMeta;
        this.known = known;
        this.md5Hash = md5Hash;
        this.sha1Hash = sha1Hash;
        this.sha256Hash = sha256Hash;
        this.mimeType = mimeType;
        this.extension = extension;
        this.fileType = fileType;
        this.mTime = mTime;
        this.cTime = cTime;
        this.aTime = aTime;
        this.crTime = crTime;
        this.fileSystemType = fileSystemType;
        this.metaDataText = metaDataText;
        this.children = new ArrayList<>();
    }

    public void addChild(FileNode child) {
        this.children.add(child);
    }

}
