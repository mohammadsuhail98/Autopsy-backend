package uma.autopsy.DataSourceContent;
import org.sleuthkit.datamodel.*;

import java.util.ArrayList;
import java.util.List;

public class DirectoryTreeBuilder {

    public FileNode buildTree(Content content) throws TskCoreException {

        boolean isDir = false, isFile = false, isRoot = false;
        String name = "", path = "", type = "", flagsDir = "", flagsMeta = "", known = "";
        String md5Hash = "", sha1Hash = "", sha256Hash = "", mimeType = "", extension = "";
        String mTime = "", cTime = "", aTime = "", crTime = "", fileSystemType = "";
        short fileType = -1;
        long id = -1, size = -1;
        int uid = -1, gid = -1;
        List<String> metaDataText = new ArrayList<>();
        id = content.getId();
        name = content.getName();
        path = content.getUniquePath();

        /**
         * Gets a text-based description of the file's metadata. This is the same
         * content as the TSK istat tool produces and is different information for
         * each type of file system.
         */
        if (content instanceof FsContent) {
            FsContent fsContent = (FsContent) content;
            metaDataText = fsContent.getMetaDataText();
        }

        if (content instanceof AbstractFile) {
            AbstractFile file = (AbstractFile) content;
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
            mimeType = file.getMIMEType();
            extension = file.getNameExtension();
            fileType = file.getType().getFileType();
            mTime = file.getMtimeAsDate();
            cTime = file.getCtimeAsDate();
            aTime = file.getAtimeAsDate();
            crTime = file.getCrtimeAsDate();
            fileSystemType = file.getFileSystem().getFsType().getDisplayName();
        }

        FileNode node = new FileNode(name, path, type, id, uid, gid, isDir, isFile,
                isRoot, size, flagsDir, flagsMeta, known, md5Hash, sha1Hash,
                sha256Hash, mimeType, extension, fileType, mTime, cTime, aTime,
                crTime, fileSystemType, metaDataText);

        for (Content child : content.getChildren()) {
            node.addChild(buildTree(child));
        }

        return node;
    }

}