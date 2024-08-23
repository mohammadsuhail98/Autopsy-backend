package uma.autopsy.FileViews;

import org.apache.commons.lang3.StringUtils;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uma.autopsy.Cases.Case;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.DataSourceContent.Models.FileNode;
import uma.autopsy.Exceptions.CaseDoesNotExistException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileViewsServiceImpl implements FileViewsService {

    @Autowired
    CaseRepository caseRepository;

    @Override
    public List<FileNode> getFilesByViewType(int caseId, String deviceId, int type) {
        Case caseEntity = getCase(caseId);
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());
            QueryGenerator.FileViewType fileViewType = QueryGenerator.FileViewType.fromID(type);
            var list = skcase.findAllFilesWhere(QueryGenerator.getQueryForFileViewType(fileViewType));

            List<FileNode> files = new ArrayList<>();
            for (AbstractFile abstractFile : list) {
                files.add(FileNode.getNode(abstractFile));
            }
            return files;
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CurrentMimeType> getCurrentMimeTypes(int caseId, String deviceId) {
        Case caseEntity = getCase(caseId);
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());

            String query = STR."SELECT mime_type, count(*) AS count FROM tsk_files WHERE mime_type IS NOT null AND \{QueryGenerator.getBaseWhereExprQuery()} GROUP BY mime_type";

            final HashMap<String, Map<String, Long>> existingMimeTypeCounts = new HashMap<>();
            System.out.println(QueryGenerator.getBaseWhereExprQuery());
            System.out.println(query);
            SleuthkitCase.CaseDbQuery dbQuery = skcase.executeQuery(query);

            ResultSet resultSet = dbQuery.getResultSet();
            while (resultSet.next()) {
                final String mime_type = resultSet.getString("mime_type");
                if (!mime_type.isEmpty()) {
                    //if the mime_type contained multiple slashes then everything after the first slash will become the subtype
                    final String mediaType = StringUtils.substringBefore(mime_type, "/");
                    final String subType = StringUtils.removeStart(mime_type, mediaType + "/");
                    if (!mediaType.isEmpty() && !subType.isEmpty()) {
                        final long count = resultSet.getLong("count");
                        existingMimeTypeCounts.computeIfAbsent(mediaType, t -> new HashMap<>())
                                .put(subType, count);
                    }
                }
            }
            return getCurrentMimeTypes(existingMimeTypeCounts);
        } catch (TskCoreException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<CurrentMimeType> getCurrentMimeTypes(HashMap<String, Map<String, Long>> existingMimeTypeCounts) {
        List<CurrentMimeType> fileViewMimeTypes = new ArrayList<>();

        for (Map.Entry<String, Map<String, Long>> mediaTypeEntry : existingMimeTypeCounts.entrySet()) {

            CurrentMimeType fileViewMimeType = new CurrentMimeType();
            List<CurrentMimeType.CurrentMimeSubtype> subtypes = new ArrayList<>();

            fileViewMimeType.setName(mediaTypeEntry.getKey());

            for (Map.Entry<String, Long> subTypeEntry : mediaTypeEntry.getValue().entrySet()) {

                CurrentMimeType.CurrentMimeSubtype subtype = new CurrentMimeType.CurrentMimeSubtype();

                subtype.setName(subTypeEntry.getKey());
                subtype.setFullName(STR."\{mediaTypeEntry.getKey()}/\{subTypeEntry.getKey()}");
                subtype.setCount(subTypeEntry.getValue());

                subtypes.add(subtype);
            }
            fileViewMimeType.setMimeSubtypes(subtypes);
            fileViewMimeTypes.add(fileViewMimeType);
        }
        return fileViewMimeTypes;
    }

    @Override
    public List<FileNode> getFilesByMimeTypes(int caseId, String deviceId, String mimeType) {
        Case caseEntity = getCase(caseId);
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());

            var list = skcase.findAllFilesWhere(QueryGenerator.getFilesByMimeTypeQuery(mimeType));
            List<FileNode> files = new ArrayList<>();

            for (AbstractFile abstractFile : list) {
                files.add(FileNode.getNode(abstractFile));
            }

            return files;
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    boolean validateDeviceId(String deviceId, Case caseEntity){
        return deviceId.equalsIgnoreCase(caseEntity.getDeviceId());
    }

    private Case getCase(int caseId){
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseDoesNotExistException(STR."Case not found for this id : \{caseId}"));
    }
}
