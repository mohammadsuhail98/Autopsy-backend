package uma.autopsy.GeoLocation;

import org.sleuthkit.datamodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uma.autopsy.Cases.Models.Case;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.Exceptions.CaseNotFoundException;
import uma.autopsy.GlobalProperties.GlobalProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.File;

@Service
public class GeoLocationServiceImpl implements GeoLocationService {

    @Autowired
    CaseRepository caseRepository;

    @Autowired
    private GlobalProperties globalProperties;

    @Override
    public List<GeoLocation> getAllGeoLocations(String deviceId, int caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseNotFoundException(STR."Case not found for this id : \{caseId}"));
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

        SleuthkitCase skcase = null;
        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());

            Collection<BlackboardArtifact.Type> types = new ArrayList<>();
            types.add(BlackboardArtifact.Type.TSK_METADATA_EXIF);

            Collection<Long> dataSourcesIds = new ArrayList<Long>();
            for (var obj: skcase.getRootObjects()) {
                dataSourcesIds.add(obj.getId());
            }

            var artifactList = skcase.getBlackboard().getArtifacts(types, dataSourcesIds);

            return getGeoLocations(skcase, artifactList);
        } catch (TskCoreException e) {
           throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @Override
    public List<GeoLocation> getGeoLocationByDataSources(String deviceId, int caseId, List<Long> dataSourceIds) {

        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseNotFoundException(STR."Case not found for this id : \{caseId}"));
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

        SleuthkitCase skcase = null;
        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());
            Collection<BlackboardArtifact.Type> types = new ArrayList<>();
            types.add(BlackboardArtifact.Type.TSK_METADATA_EXIF);
            if (dataSourceIds != null) {
                var artifactList = skcase.getBlackboard().getArtifacts(types, dataSourceIds);
                return getGeoLocations(skcase, artifactList);
            }
            return getGeoLocations(skcase, new ArrayList<>());
        } catch (TskCoreException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    List<GeoLocation> getGeoLocations(SleuthkitCase skcase, List<BlackboardArtifact> artifactList) throws TskCoreException {
        List<GeoLocation> geoLocationList = new ArrayList<>();

        for (var artifact: artifactList){
            GeoLocation geoLocation = new GeoLocation();
            var latitude = artifact.getAttribute(BlackboardAttribute.Type.TSK_GEO_LATITUDE);
            var longitude = artifact.getAttribute(BlackboardAttribute.Type.TSK_GEO_LONGITUDE);
            var altitude = artifact.getAttribute(BlackboardAttribute.Type.TSK_GEO_ALTITUDE);
            var timestamp = artifact.getAttribute(BlackboardAttribute.Type.TSK_DATETIME_CREATED);
            var deviceModel = artifact.getAttribute(BlackboardAttribute.Type.TSK_DEVICE_MODEL);
            var deviceName = artifact.getAttribute(BlackboardAttribute.Type.TSK_DEVICE_MAKE);

            geoLocation.setId(artifact.getParent().getId());
            geoLocation.setFileName(artifact.getParent().getName());
            geoLocation.setLatitude(latitude == null ? null : latitude.getDisplayString());
            geoLocation.setLongitude(longitude == null ? null : longitude.getDisplayString());
            geoLocation.setAltitude(altitude == null ? null : altitude.getDisplayString());
            geoLocation.setTimestamp(timestamp == null ? null : timestamp.getDisplayString());
            geoLocation.setDeviceModel(deviceModel == null ? null : deviceModel.getDisplayString());
            geoLocation.setDeviceName(deviceName == null ? null : deviceName.getDisplayString());

            if (latitude != null && longitude != null) {

                String baseDir = STR."\{skcase.getDbDirPath()}/exif_files/\{artifact.getDataSource().getId()}-\{artifact.getDataSource().getName()}";
                File directory = new File(baseDir);

                if (baseDir == null || directory == null) {
                    System.out.println("Base directory path is null");
                }

                File localFile = new File(directory, artifact.getParent().getName());
                if (localFile.exists()) {
                    geoLocation.setFile(localFile.getAbsolutePath());
                }
            }
            geoLocationList.add(geoLocation);
        }

        return geoLocationList;
    }

    @Override
    public byte[] getImage(String path) {
        return new byte[0];
    }

    boolean validateDeviceId(String deviceId, Case caseEntity){
        return deviceId.equalsIgnoreCase(caseEntity.getDeviceId());
    }

}
