package uma.autopsy.Utils;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.datamodel.*;
import uma.autopsy.DataSourceContent.Models.FileNode;
import uma.autopsy.DataSourceContent.Models.MimeType;
import uma.autopsy.DataSourceContent.Models.VolumeInfo;

public class DirectoryTreeBuilder {

    public FileNode buildTree(Content content) throws TskCoreException {

        boolean isDir = false, isFile = false, isRoot = false, hasAnalysisResults = false;
        boolean isVirtual = false, isDeleted = false, isVolume = false;
        String name = "", path = "", type = "", flagsDir = "", flagsMeta = "", known = "";
        String md5Hash = "", sha1Hash = "", sha256Hash = "", extension = "";
        String mTime = "", cTime = "", aTime = "", crTime = "", fileSystemType = "";
        short fileType = -1;
        long id = -1, size = -1;
        int uid = -1, gid = -1;
        MimeType mimeType = new MimeType();
        VolumeInfo volumeInfo = null;
        id = content.getId();
        name = content.getName();
        path = content.getUniquePath();

        if (content instanceof Volume volume) {
            isVolume = true;
            volumeInfo = new VolumeInfo(
                    volume.getStart(),
                    volume.getLength(),
                    volume.getDescription(),
                    volume.getFlagsAsString());
        }

        if (content instanceof AbstractFile file) {

            try {
                var detector = new FileTypeDetector();
                mimeType = SupportedMimeTypesUtil.getMimeTypeModel(detector.getMIMEType(file));
            } catch (FileTypeDetector.FileTypeDetectorInitException e) {
                mimeType = new MimeType(SupportedMimeTypesUtil.MimeTypeList.NONE.getValue(), file.getMIMEType(), false);
                System.out.println(e.getLocalizedMessage());
            }

            uid = file.getUid();
            gid = file.getGid();
            isDir = file.isDir();
            isFile = file.isFile();
            isRoot = file.isRoot();
            size = file.getSize();
            type = file.getType().getName();
            flagsDir = file.getDirFlagAsString();
            flagsMeta = file.getMetaFlagsAsString();
            known = file.getKnown().getName();
            md5Hash = file.getMd5Hash();
            sha1Hash = file.getSha1Hash();
            sha256Hash = file.getSha256Hash();
            extension = file.getNameExtension();
            fileType = file.getType().getFileType();
            mTime = file.getMtimeAsDate();
            cTime = file.getCtimeAsDate();
            aTime = file.getAtimeAsDate();
            crTime = file.getCrtimeAsDate();
            isVirtual = file.isVirtual();
            isDeleted = file.isDirNameFlagSet(TskData.TSK_FS_NAME_FLAG_ENUM.UNALLOC);
            fileSystemType = file.hasFileSystem() ? file.getFileSystem().getFsType().getDisplayName() : "";
            hasAnalysisResults = !content.getAllAnalysisResults().isEmpty();
        }

        FileNode node = new FileNode(name, path, type, id, uid, gid, isDir, isFile,
                isRoot, size, flagsDir, flagsMeta, known, md5Hash, sha1Hash,
                sha256Hash, mimeType, extension, fileType, mTime, cTime, aTime,
                crTime, fileSystemType, hasAnalysisResults, isVirtual, isDeleted, isVolume, volumeInfo);

        for (Content child : content.getChildren()) {
            node.addChild(buildTree(child));
        }

        return node;
    }

}