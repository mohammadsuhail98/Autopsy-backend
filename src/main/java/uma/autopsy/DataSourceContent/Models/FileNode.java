package uma.autopsy.DataSourceContent.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.datamodel.*;
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
    private boolean hasAnalysisResults;
    private boolean isVirtual;
    private boolean isDeleted;
    private boolean isVolume;
    private VolumeInfo volumeInfo;

    public FileNode(String name, String path, String type, long id,
                    int uid, int gid, boolean isDir, boolean isFile, boolean isRoot,
                    long size, String flagsDir, String flagsMeta,
                    String known, String md5Hash, String sha1Hash,
                    String sha256Hash, MimeType mimeType, String extension,
                    short fileType, String mTime, String cTime, String aTime,
                    String crTime, String fileSystemType, boolean hasAnalysisResults,
                    boolean isVirtual, boolean isDeleted, boolean isVolume, VolumeInfo volumeInfo) {
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
        this.hasAnalysisResults = hasAnalysisResults;
        this.isVirtual = isVirtual;
        this.isDeleted = isDeleted;
        this.isVolume = isVolume;
        this.volumeInfo = volumeInfo;
        this.children = new ArrayList<>();
    }

    public FileNode(String name, String path, String type, long id,
                    int uid, int gid, boolean isDir, boolean isFile, boolean isRoot,
                    long size, String flagsDir, String flagsMeta,
                    String known, String md5Hash, String sha1Hash,
                    String sha256Hash, MimeType mimeType, String extension,
                    short fileType, String mTime, String cTime, String aTime,
                    String crTime, String fileSystemType, boolean hasAnalysisResults,
                    boolean isVirtual, boolean isDeleted, boolean isVolume) {
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
        this.hasAnalysisResults = hasAnalysisResults;
        this.isVirtual = isVirtual;
        this.isDeleted = isDeleted;
        this.isVolume = isVolume;
        this.children = new ArrayList<>();
    }

    public FileNode(String name, String path, String type, long id,
                    int uid, int gid, boolean isDir, boolean isFile, boolean isRoot,
                    long size, String flagsDir, String flagsMeta,
                    String known, String md5Hash, String sha1Hash,
                    String sha256Hash, MimeType mimeType, String extension,
                    short fileType, String mTime, String cTime, String aTime,
                    String crTime, String fileSystemType, List<String> metaDataText, boolean hasAnalysisResults,
                    boolean isVirtual, boolean isDeleted, boolean isVolume) {
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
        this.hasAnalysisResults = hasAnalysisResults;
        this.isVirtual = isVirtual;
        this.isDeleted = isDeleted;
        this.isVolume = isVolume;
        this.children = new ArrayList<>();
    }

    public void addChild(FileNode child) {
        this.children.add(child);
    }

    public static FileNode getNode(AbstractFile content) {
        if (content == null) { return new FileNode(); }
        List<String> metaDataText = new ArrayList<>();
        MimeType mimeType = new MimeType(SupportedMimeTypesUtil.MimeTypeList.NONE.getValue(), "", false);
        String uniquePath = "", fileSystemName = "";;
        boolean hasAnalysisResults = false, isDeleted = false, isVolume = false;

        try {
            hasAnalysisResults = !content.getAllAnalysisResults().isEmpty();
            uniquePath = content.getUniquePath();
            fileSystemName = content.hasFileSystem() ? content.getFileSystem().getFsType().getDisplayName() : "";
            isDeleted = content.isDirNameFlagSet(TskData.TSK_FS_NAME_FLAG_ENUM.UNALLOC);
            if (content instanceof FsContent fsContent) {
                metaDataText = fsContent.getMetaDataText();
            }
        } catch (TskCoreException e) {
            System.out.println(e.getLocalizedMessage());
        }

        /*
         * Gets a text-based description of the file's metadata. This is the same
         * content as the TSK istat tool produces and is different information for
         * each type of file system.
         */
        if (content instanceof FsContent fsContent) {
            try {
                metaDataText = fsContent.getMetaDataText();
            } catch (TskCoreException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        try {
            var detector = new FileTypeDetector();
            mimeType = SupportedMimeTypesUtil.getMimeTypeModel(detector.getMIMEType(content));
        } catch (FileTypeDetector.FileTypeDetectorInitException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return new FileNode(content.getName(), uniquePath, content.getType().getName(), content.getId(), content.getUid(),
                content.getGid(), content.isDir(), content.isFile(), content.isRoot(), content.getSize(), content.getDirFlagAsString(),
                content.getMetaFlagsAsString(), content.getKnown().getName(), content.getMd5Hash(), content.getSha1Hash(), content.getSha256Hash(),
                mimeType, content.getNameExtension(), content.getType().getFileType(), content.getMtimeAsDate(), content.getCtimeAsDate(),
                content.getAtimeAsDate(), content.getCrtimeAsDate(),
                fileSystemName, metaDataText, hasAnalysisResults, content.isVirtual(), isDeleted, isVolume);
    }

    public static FileNode getNode(Content content) throws TskCoreException {
        if (content == null) { return new FileNode(); }
        FileNode node = new FileNode();

        List<String> metaDataText = new ArrayList<>();
        boolean hasAnalysisResults = !content.getAllAnalysisResults().isEmpty();

        if (content instanceof FsContent fsContent) {
            metaDataText = fsContent.getMetaDataText();
        }

        node.setName(content.getName());
        node.setPath(content.getUniquePath());
        node.setId(content.getId());
        node.setSize(content.getSize());
        node.setMetaDataText(metaDataText);
        node.setHasAnalysisResults(hasAnalysisResults);
        node.setMimeType(new MimeType(SupportedMimeTypesUtil.MimeTypeList.NONE.getValue(), "", false));

        return node;
    }

    public static FileNode getNode(org.sleuthkit.datamodel.Volume volume) throws TskCoreException {
        if (volume == null) { return new FileNode(); }
        FileNode node = new FileNode();
        VolumeInfo volumeInfo;

        boolean hasAnalysisResults = !volume.getAllAnalysisResults().isEmpty();

        node.setName(volume.getName());
        node.setPath(volume.getUniquePath());
        node.setId(volume.getId());
        node.setSize(volume.getSize());
        node.setHasAnalysisResults(hasAnalysisResults);
        volumeInfo = new VolumeInfo(volume.getStart(), volume.getLength(), volume.getDescription(), volume.getFlagsAsString());
        node.setVolumeInfo(volumeInfo);

        return node;
    }

}
