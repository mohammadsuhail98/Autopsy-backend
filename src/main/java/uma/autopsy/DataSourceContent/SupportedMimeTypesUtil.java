package uma.autopsy.DataSourceContent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SupportedMimeTypesUtil {

    public enum MimeTypeList {
        IMAGE(0),
        VIDEO(1),
        PDF(2),
        NONE(3);

        private final int value;

        MimeTypeList(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList(
            "application/vnd.adobe.photoshop", "application/x-123", "application/x-photoshop", "image/bmp", "image/cursor",
            "image/dcx", "image/gif", "image/ico", "image/iff", "image/jp2",
            "image/jpeg", "image/jpeg2000", "image/pcx", "image/pict", "image/png",
            "image/sgi", "image/targa", "image/tga", "image/tiff", "image/vnd.adobe.photoshop",
            "image/vnd.microsoft.bitmap", "image/vnd.microsoft.cursor", "image/vnd.microsoft.icon", "image/vnd.wap.wbmp", "image/webp",
            "image/x-apple-icons", "image/x-bmp", "image/x-cursor", "image/x-dcx", "image/x-icon",
            "image/x-iff", "image/x-ms-bmp", "image/x-pcx", "image/x-photoshop", "image/x-pict",
            "image/x-png", "image/x-pntg", "image/x-portable-anymap", "image/x-portable-arbitrarymap", "image/x-portable-bitmap",
            "image/x-portable-graymap", "image/x-portable-pixmap", "image/x-psd", "image/x-rgb", "image/x-sgi",
            "image/x-targa", "image/x-tga", "image/x-thumbs-db", "image/x-tiff", "image/x-windows-bmp", "image/heic"
    );

    private static final List<String> VIDEO_MIME_TYPES = Arrays.asList(
            "video/3gpp", "video/3gpp2", "audio/aiff", "audio/amr-wb", "audio/basic",
            "audio/mp4", "video/mp4", "audio/mpeg", "video/mpeg", "audio/mpeg3",
            "application/mxf", "application/ogg", "video/quicktime", "audio/vorbis", "audio/vnd.wave",
            "video/webm", "video/x-3ivx", "audio/x-aac", "audio/x-adpcm", "audio/x-alaw",
            "audio/x-cinepak", "video/x-divx", "audio/x-dv", "video/x-dv", "video/x-ffv",
            "audio/x-flac", "video/x-flv", "audio/x-gsm", "video/x-h263", "video/x-h264",
            "video/x-huffyuv", "video/x-indeo", "video/x-intel-h263", "audio/x-ircam", "video/x-jpeg",
            "audio/x-m4a", "video/x-m4v", "audio/x-mace", "audio/x-matroska", "video/x-matroska",
            "audio/x-mpeg", "video/x-mpeg", "audio/x-mpeg-3", "video/x-ms-asf", "audio/x-ms-wma",
            "video/x-ms-wmv", "video/x-msmpeg", "video/x-msvideo", "video/x-msvideocodec", "audio/x-mulaw",
            "audio/x-nist", "audio/x-oggflac", "audio/x-paris", "audio/x-qdm2", "audio/x-raw",
            "video/x-raw", "video/x-rle", "audio/x-speex", "video/x-svq", "audio/x-svx",
            "video/x-tarkin", "video/x-theora", "audio/x-voc", "audio/x-vorbis", "video/x-vp3",
            "audio/x-w64", "audio/x-wav", "audio/x-wma", "video/x-wmv", "video/x-xvid"
    );

    private static final List<String> PDF_MIME_TYPES = Arrays.asList("application/pdf");

    public static List<String> getAllMimeTypes() {
        return IMAGE_MIME_TYPES;
    }

    public static List<String> getAllVideoMimeTypes() {
        return VIDEO_MIME_TYPES;
    }

    public static List<String> getAllPdfMimeTypes() {
        return PDF_MIME_TYPES;
    }

    public static boolean isMimeTypeSupported(String mimeType, MimeTypeList listType) {
        Set<String> mimeTypeSet;

        switch (listType) {
            case IMAGE:
                mimeTypeSet = new HashSet<>(IMAGE_MIME_TYPES);
                break;
            case VIDEO:
                mimeTypeSet = new HashSet<>(VIDEO_MIME_TYPES);
                break;
            case PDF:
                mimeTypeSet = new HashSet<>(PDF_MIME_TYPES);
                break;
            default:
                return false;
        }

        return mimeTypeSet.contains(mimeType);
    }

    public static MimeType getMimeTypeModel(String mimeType) {
        if (isMimeTypeSupported(mimeType, MimeTypeList.IMAGE)) {
            return new MimeType(MimeTypeList.IMAGE.getValue(), mimeType, true);
        } else if (isMimeTypeSupported(mimeType, MimeTypeList.VIDEO)) {
            return new MimeType(MimeTypeList.VIDEO.getValue(), mimeType, true);
        } else if (isMimeTypeSupported(mimeType, MimeTypeList.PDF)) {
            return new MimeType(MimeTypeList.PDF.getValue(), mimeType, true);
        } else {
            return new MimeType(MimeTypeList.NONE.getValue(), mimeType, false);
        }
    }

}
