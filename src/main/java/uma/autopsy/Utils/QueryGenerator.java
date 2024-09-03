package uma.autopsy.Utils;

import org.apache.commons.lang3.StringUtils;
import org.sleuthkit.datamodel.TskData;

import java.util.List;
import java.util.stream.Collectors;

public class QueryGenerator {

    public enum FileViewType {
        IMAGES(1),
        VIDEOS(2),
        AUDIO(3),
        ARCHIVES(4),
        DATABASES(5),
        DOCUMENTS_HTML(6),
        DOCUMENTS_OFFICE(7),
        DOCUMENTS_PDF(8),
        DOCUMENTS_PLAIN_TEXT(9),
        DOCUMENTS_RICH_TEXT(10),
        EXECUTABLE_EXE(11),
        EXECUTABLE_DLL(12),
        EXECUTABLE_BAT(13),
        EXECUTABLE_CMD(14),
        EXECUTABLE_COM(15),
        DELETED_FILES_FILE_SYSTEM(16),
        DELETED_FILES_ALL(17),
        FILE_SIZE_MB_50_TO_200(18),
        FILE_SIZE_MB_200_TO_1GB(19),
        FILE_SIZE_MB_1GB_PLUS(20);

        private final int id;

        FileViewType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static FileViewType fromID(int id) {
            FileViewType[] var1 = values();

            for (FileViewType value : var1) {
                if (value.getId() == id) {
                    return value;
                }
            }

            throw new IllegalArgumentException("No ARTIFACT_TYPE matching type: " + id);
        }
    }

    public static String getQueryForFileViewType(FileViewType fileViewType) {
        switch (fileViewType) {
            case IMAGES:
                return getFilesByExtensionQuery(FileTypeExtensions.getImageExtensions());
            case VIDEOS:
                return getFilesByExtensionQuery(FileTypeExtensions.getVideoExtensions());

            case AUDIO:
                return getFilesByExtensionQuery(FileTypeExtensions.getAudioExtensions());

            case ARCHIVES:
                return getFilesByExtensionQuery(FileTypeExtensions.getArchiveExtensions());

            case DATABASES:
                return getFilesByExtensionQuery(FileTypeExtensions.getDatabaseExtensions());

            case DOCUMENTS_HTML:
                return getFilesByExtensionQuery(FileTypeExtensions.getWebExtensions());

            case DOCUMENTS_OFFICE:
                return getFilesByExtensionQuery(FileTypeExtensions.getDocumentExtensions());

            case DOCUMENTS_PDF:
                return getFilesByExtensionQuery(FileTypeExtensions.getPDFExtensions());

            case DOCUMENTS_PLAIN_TEXT, DOCUMENTS_RICH_TEXT :
                return getFilesByExtensionQuery(FileTypeExtensions.getTextExtensions());

            case EXECUTABLE_EXE, EXECUTABLE_DLL, EXECUTABLE_BAT, EXECUTABLE_CMD, EXECUTABLE_COM:
                return getFilesByExtensionQuery(FileTypeExtensions.getExecutableExtensions());

            case DELETED_FILES_FILE_SYSTEM:
                return getFSDeletedFilesQuery();

            case DELETED_FILES_ALL:
                return getAllDeletedFilesQuery();

            case FILE_SIZE_MB_50_TO_200:
                return STR."(size >= 50000000 AND size < 200000000)\{getFilesBySizeQuery()}";

            case FILE_SIZE_MB_200_TO_1GB:
                return STR."(size >= 200000000 AND size < 1000000000)\{getFilesBySizeQuery()}";

            case FILE_SIZE_MB_1GB_PLUS:
                return STR."(size >= 1000000000)\{getFilesBySizeQuery()}";

            default:
                throw new IllegalArgumentException("Unknown FileViewType: " + fileViewType);
        }

    }

    public static String getFilesBySizeQuery(){
        return STR." AND (type != \{TskData.TSK_DB_FILES_TYPE_ENUM.UNALLOC_BLOCKS.getFileType()})";
    }

    public static String getAllDeletedFilesQuery(){
        return STR."( (dir_flags = \{TskData.TSK_FS_NAME_FLAG_ENUM.UNALLOC.getValue()} OR meta_flags = \{TskData.TSK_FS_META_FLAG_ENUM.ORPHAN.getValue()}) AND type = \{TskData.TSK_DB_FILES_TYPE_ENUM.FS.getFileType()} ) OR type = \{TskData.TSK_DB_FILES_TYPE_ENUM.CARVED.getFileType()} OR (dir_flags = \{TskData.TSK_FS_NAME_FLAG_ENUM.UNALLOC.getValue()} AND type = \{TskData.TSK_DB_FILES_TYPE_ENUM.LAYOUT_FILE.getFileType()} )";
    }

    public static String getFSDeletedFilesQuery(){
        return STR."dir_flags = \{TskData.TSK_FS_NAME_FLAG_ENUM.UNALLOC.getValue()} AND meta_flags != \{TskData.TSK_FS_META_FLAG_ENUM.ORPHAN.getValue()} AND type = \{TskData.TSK_DB_FILES_TYPE_ENUM.FS.getFileType()}";
    }

    public static String getFilesByMimeTypeQuery(String mimeType){
        return STR."\{getBaseWhereExprQuery()} AND mime_type = '\{mimeType}'";
    }

    public static String getBaseWhereExprQuery() {
        return "(dir_type = " + TskData.TSK_FS_NAME_TYPE_ENUM.REG.getValue() + ")"
                + " AND (type IN ("
                + TskData.TSK_DB_FILES_TYPE_ENUM.FS.ordinal() + ","
                + TskData.TSK_DB_FILES_TYPE_ENUM.CARVED.ordinal() + ","
                + TskData.TSK_DB_FILES_TYPE_ENUM.DERIVED.ordinal() + ","
                + TskData.TSK_DB_FILES_TYPE_ENUM.LAYOUT_FILE.ordinal() + ","
                + TskData.TSK_DB_FILES_TYPE_ENUM.LOCAL.ordinal()
                + "))";
    }

    private static String getFilesByExtensionQuery(List<String> extensions) {

        return "(dir_type = " + TskData.TSK_FS_NAME_TYPE_ENUM.REG.getValue() + ")"
                + " AND (extension IN (" + extensions.stream()
                .map(String::toLowerCase)
                .map(s -> "'" + StringUtils.substringAfter(s, ".") + "'")
                .collect(Collectors.joining(", ")) + "))";
    }
}
