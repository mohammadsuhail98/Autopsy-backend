package uma.autopsy.Utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.autopsy.modules.pictureanalyzer.PictureAnalyzerIngestModuleFactory;
import org.sleuthkit.datamodel.*;
import org.sleuthkit.datamodel.BlackboardArtifact.Type;
import org.sleuthkit.datamodel.BlackboardAttribute.ATTRIBUTE_TYPE;

public class ExifProcessor {

    private static final Logger logger = Logger.getLogger(ExifProcessor.class.getName());
    private static final String MODULE_NAME = PictureAnalyzerIngestModuleFactory.getModuleName();

    private FileTypeDetector fileTypeDetector;
    private SleuthkitCase skcase;

    public ExifProcessor(SleuthkitCase skcase){
        this.skcase = skcase;

        try {
            this.fileTypeDetector = new FileTypeDetector();
        } catch (FileTypeDetector.FileTypeDetectorInitException e) {
            logger.log(Level.SEVERE, "Failed to instantiate FileTypeDetector", e);
        }
    }

    public void processAllDirectories(Content content) throws TskCoreException {

        if (content instanceof AbstractFile) {
            AbstractFile file = (AbstractFile) content;
            if (file != null) {
                String fileMimeType = fileTypeDetector.getMIMEType(file);
                if (mimeTypes().contains(fileMimeType.toLowerCase())) {
                    process(file);
                }
            }
        }

        for (Content child : content.getChildren()) {
            processAllDirectories(child);
        }
    }

    public void process(AbstractFile file) {
        try (BufferedInputStream bin = new BufferedInputStream(new ReadContentInputStream(file))) {
            List<BlackboardAttribute> attributes = new ArrayList<>();
            Metadata metadata = ImageMetadataReader.readMetadata(bin);

            processExifData(file, metadata, attributes);
            processGpsData(file, metadata, attributes);
            processDeviceData(file, metadata, attributes);

            if (!attributes.isEmpty()) {
                Blackboard blackboard = skcase.getBlackboard();
                if (!blackboard.artifactExists(file, Type.TSK_METADATA_EXIF, attributes)) {
                    createArtifacts(file, attributes, blackboard);
                }
            }
        } catch (TskCoreException e) {
            logger.log(Level.SEVERE, String.format("Error creating TSK_METADATA_EXIF and TSK_USER_CONTENT_SUSPECTED artifacts for %s (object ID = %d)", file.getName(), file.getId()), e);
        } catch (ImageProcessingException | IOException e) {
            logger.log(Level.WARNING, String.format("Error parsing %s (object ID = %d), presumed corrupt", file.getName(), file.getId()), e);
        }
    }

    private void processExifData(AbstractFile file, Metadata metadata, List<BlackboardAttribute> attributes) {
        ExifSubIFDDirectory exifDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exifDir != null) {
            TimeZone timeZone = getTimeZone(file);
            Date date = exifDir.getDate(36867, timeZone);
            if (date != null) {
                attributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_DATETIME_CREATED, MODULE_NAME, date.getTime() / 1000L));
            }
        }
    }

    private void processGpsData(AbstractFile file, Metadata metadata, List<BlackboardAttribute> attributes) throws TskCoreException {
        GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        if (gpsDir != null) {
            GeoLocation loc = gpsDir.getGeoLocation();

            if (loc != null) {
                attributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_GEO_LATITUDE, MODULE_NAME, loc.getLatitude()));
                attributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_GEO_LONGITUDE, MODULE_NAME, loc.getLongitude()));
                storeFile(file);
            }

            Rational altitude = gpsDir.getRational(6);
            if (altitude != null) {
                double alt = altitude.doubleValue();
                if (Double.isInfinite(alt)) {
                    alt = 0.0;
                }
                attributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_GEO_ALTITUDE, MODULE_NAME, alt));
            }
        }
    }

    private void processDeviceData(AbstractFile file, Metadata metadata, List<BlackboardAttribute> attributes) {
        ExifIFD0Directory devDir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (devDir != null) {
            String model = devDir.getString(272);
            if (StringUtils.isNotBlank(model)) {
                attributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_DEVICE_MODEL, MODULE_NAME, model));
            }

            String make = devDir.getString(271);
            if (StringUtils.isNotBlank(make)) {
                attributes.add(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_DEVICE_MAKE, MODULE_NAME, make));
            }
        }
    }

    private void createArtifacts(AbstractFile file, List<BlackboardAttribute> attributes, Blackboard blackboard) throws TskCoreException {
        List<BlackboardArtifact> artifacts = new ArrayList<>();
        BlackboardArtifact exifArtifact = file.newAnalysisResult(Type.TSK_METADATA_EXIF, Score.SCORE_NONE, null, null, null, attributes).getAnalysisResult();
        artifacts.add(exifArtifact);
        BlackboardArtifact userSuspectedArtifact = file.newAnalysisResult(Type.TSK_USER_CONTENT_SUSPECTED, Score.SCORE_UNKNOWN, null, null, null,
                List.of(new BlackboardAttribute(ATTRIBUTE_TYPE.TSK_COMMENT, MODULE_NAME, "User content suspected"))).getAnalysisResult();
        artifacts.add(userSuspectedArtifact);

        try {
            blackboard.postArtifacts(artifacts, MODULE_NAME, (long) -1); // Passing -1 or a different value to avoid context.getJobId()
        } catch (Blackboard.BlackboardException e) {
            logger.log(Level.SEVERE, "Error posting artifacts for file " + file.getName(), e);
        }
    }

    private TimeZone getTimeZone(AbstractFile file) {
        try {
            Content dataSource = file.getDataSource();
            if (dataSource != null && dataSource instanceof Image) {
                Image image = (Image) dataSource;
                return TimeZone.getTimeZone(image.getTimeZone());
            }
        } catch (TskCoreException e) {
            logger.log(Level.INFO, "Error getting time zone", e);
        }
        return null;
    }

    private void storeFile(AbstractFile file) throws TskCoreException {
        String baseDir = skcase.getDbDirPath();

        if (baseDir == null) {
            throw new IllegalArgumentException("Base directory path is null");
        }

        File directory = new File(baseDir);
        File exifDirectory = new File(directory, String.format("exif_files/%d-%s", file.getDataSource().getId(), file.getDataSource().getName()));

        if (!exifDirectory.exists()) {
            if (!exifDirectory.mkdirs()) {
                System.out.println(STR."Failed to create directory: \{baseDir}");
            }
        }

        File localFile = new File(exifDirectory, file.getName());

        if (localFile.exists()) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            byte[] imageBytes = new byte[(int) file.getSize()];
            file.read(imageBytes, 0, imageBytes.length);
            fos.write(imageBytes);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

    public Set<String> mimeTypes() {
        return new HashSet<>(Set.of("audio/x-wav", "image/jpeg", "image/tiff", "image/heic"));
    }
}

