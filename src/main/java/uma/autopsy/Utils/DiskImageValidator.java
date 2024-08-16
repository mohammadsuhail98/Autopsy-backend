package uma.autopsy.Utils;

import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;
import org.springframework.web.multipart.MultipartFile;
import uma.autopsy.Exceptions.BadRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;



/*
    Adding a Disk Image

    Autopsy supports disk images in the following formats:

    Raw Single (For example: *.img, *.dd, *.raw, *.bin)
    Raw Split (For example: *.001, *.002, *.aa, *.ab, etc)
    EnCase (For example: *.e01, *.e02, etc)
    Virtual Machines (For example: *.vmdk, *.vhd)
 */
public class DiskImageValidator {

    // Patterns to match different file types
    private static final Pattern RAW_SPLIT_PATTERN = Pattern.compile(".*\\.(\\d{3}|[a-z]{2})$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EWF_PATTERN = Pattern.compile(".*\\.e\\d{2}$", Pattern.CASE_INSENSITIVE);

    public static TskData.TSK_IMG_TYPE_ENUM validateDiskImage(MultipartFile file) throws BadRequestException, IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        fileName = fileName.toLowerCase();

        // Validate based on file extension
        if (fileName.endsWith(".img") || fileName.endsWith(".dd") || fileName.endsWith(".raw") || fileName.endsWith(".bin")) {
            return TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_RAW_SING;
        } else if (RAW_SPLIT_PATTERN.matcher(fileName).matches()) {
            return TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_RAW_SPLIT;
        } else if (EWF_PATTERN.matcher(fileName).matches()) {
            return TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_EWF_EWF;
        } else if (fileName.endsWith(".vmdk")) {
            return TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_VMDK_VMDK;
        } else if (fileName.endsWith(".vhd") || fileName.endsWith(".vhdx")) {
            return TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_VHD_VHD;
        }

        // Validate by reading the first few bytes (magic number)
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[8];
            if (inputStream.read(header, 0, header.length) != header.length) {
                throw new IllegalArgumentException("Unable to read file header");
            }

            // Check for EWF magic number (EVF)
            byte[] ewfSignature = {0x45, 0x56, 0x46}; // "EVF"
            if (Arrays.equals(Arrays.copyOfRange(header, 0, 3), ewfSignature)) {
                return TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_EWF_EWF;
            }

            // Check for VMDK magic number (KDMV)
            byte[] vmdkSignature = {0x4B, 0x44, 0x4D, 0x56}; // "KDMV"
            if (Arrays.equals(Arrays.copyOfRange(header, 0, 4), vmdkSignature)) {
                return TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_VMDK_VMDK;
            }
        }

        throw new BadRequestException("Unsupported or unrecognized disk image type");
    }

    public static boolean isValidDiskImage(MultipartFile file) throws IOException {
        return validateDiskImage(file) != null;
    }

}