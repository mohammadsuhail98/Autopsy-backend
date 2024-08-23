package uma.autopsy.FileViews;

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
            //                return STR."\{createBaseWhereExpr()} AND mime_type = '\{FileTypeExtensions.getImageExtensions()}'";

            case DELETED_FILES_FILE_SYSTEM:
                return ""; // Query for deleted files in file system

            case DELETED_FILES_ALL:
                return ""; // Query for all deleted files

            case FILE_SIZE_MB_50_TO_200:
                return ""; // Query for file size 50MB to 200MB

            case FILE_SIZE_MB_200_TO_1GB:
                return ""; // Query for file size 200MB to 1GB

            case FILE_SIZE_MB_1GB_PLUS:
                return ""; // Query for file size 1GB and above

            default:
                throw new IllegalArgumentException("Unknown FileViewType: " + fileViewType);
        }

    }

    public static String getFilesByMimeTypeQuery(String mimeType){
        System.out.println(mimeType);
        System.out.println(STR."\{getBaseWhereExprQuery()} AND mime_type = '\{mimeType}'");
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
