package uma.autopsy.DataSourceContent.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.FsContent;
import org.sleuthkit.datamodel.TskCoreException;
import uma.autopsy.Utils.SupportedMimeTypesUtil;

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
    private String extension;
    private short fileType;
    private List<FileNode> children;
    private String mTime = "";
    private String cTime = "";
    private String aTime = "";
    private String crTime = "";
    private String fileSystemType = "";
    private List<String> metaDataText;
    private MimeType mimeType;

    public FileNode(String name, String path, String type, long id,
                    int uid, int gid, boolean isDir, boolean isFile, boolean isRoot,
                    long size, String flagsDir, String flagsMeta,
                    String known, String md5Hash, String sha1Hash,
                    String sha256Hash, MimeType mimeType, String extension,
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

    public static FileNode getNode(AbstractFile content) throws TskCoreException {
        if (content == null) { return new FileNode(); }
        List<String> metaDataText = new ArrayList<>();
        MimeType mimeType = new MimeType(SupportedMimeTypesUtil.MimeTypeList.NONE.getValue(), "", false);

        if (content instanceof FsContent fsContent) {
            metaDataText = fsContent.getMetaDataText();
        }

        try {
            var detector = new FileTypeDetector();
            mimeType = SupportedMimeTypesUtil.getMimeTypeModel(detector.getMIMEType(content));
        } catch (FileTypeDetector.FileTypeDetectorInitException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return new FileNode(content.getName(), content.getUniquePath(), content.getType().getName(), content.getId(), content.getUid(),
                content.getGid(), content.isDir(), content.isFile(), content.isRoot(), content.getSize(), content.getDirFlagAsString(),
                content.getMetaFlagsAsString(), content.getKnown().getName(), content.getMd5Hash(), content.getSha1Hash(), content.getSha256Hash(),
                mimeType, content.getNameExtension(), content.getType().getFileType(), content.getMtimeAsDate(), content.getCtimeAsDate(),
                content.getAtimeAsDate(), content.getCrtimeAsDate(),
                content.getFileSystem().getFsType().getDisplayName(), metaDataText);
    }

    public static FileNode getNode(Content content) throws TskCoreException {
        if (content == null) { return new FileNode(); }
        FileNode node = new FileNode();

        List<String> metaDataText = new ArrayList<>();

        if (content instanceof FsContent fsContent) {
            metaDataText = fsContent.getMetaDataText();
        }

        node.setName(content.getName());
        node.setPath(content.getUniquePath());
        node.setId(content.getId());
        node.setSize(content.getSize());
        node.setMetaDataText(metaDataText);
        node.setMimeType(new MimeType(SupportedMimeTypesUtil.MimeTypeList.NONE.getValue(), "", false));

        return node;
    }

}
