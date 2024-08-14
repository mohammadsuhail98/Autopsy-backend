package uma.autopsy.DataSourceContent;

import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.DataSource.DataSource;
import uma.autopsy.Exceptions.ResourceNotFoundException;

@Service
public class DSContentServiceImp implements DSContentService {

    @Autowired
    DSContentRepository dsContentRepository;
    @Autowired
    private CaseRepository caseRepository;

    @Override
    public FileNode getDataSourceContentById(int dataSourceId, String deviceId) {
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        SleuthkitCase skcase = null;
        FileNode tree = null;

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            var content = skcase.getImageById(dataSource.getDataSourceId());
            DirectoryTreeBuilder treeBuilder = new DirectoryTreeBuilder();
            tree = treeBuilder.buildTree(content);

        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }

        return tree;
    }

    @Override
    public FileNode getFileContent(int dataSourceId, String deviceId, int fileId){
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        SleuthkitCase skcase = null;
        FileNode fileNode = new FileNode();

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            AbstractFile content = skcase.getAbstractFileById(fileId);
            fileNode = getFileNode(content);
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }

        return fileNode;
    }

    FileNode getFileNode(AbstractFile content) throws TskCoreException {
        FileNode fileNode = new FileNode();

        fileNode.setId(content.getId());
        fileNode.setName(content.getName());
        fileNode.setPath(content.getUniquePath());
        fileNode.setUid(content.getUid());
        fileNode.setGid(content.getGid());
        fileNode.setDir(content.isDir());
        fileNode.setFile(content.isFile());
        fileNode.setRoot(content.isRoot());
        fileNode.setSize(content.getSize());
        fileNode.setType(content.getType().getName());
        fileNode.setFlagsDir(content.getDirFlagAsString());
        fileNode.setFlagsMeta(content.getMetaFlagsAsString());
        fileNode.setKnown(content.getKnown().getName());
        fileNode.setMd5Hash(content.getMd5Hash());
        fileNode.setSha1Hash(content.getSha1Hash());
        fileNode.setSha256Hash(content.getSha256Hash());
        fileNode.setMimeType(content.getMIMEType());
        fileNode.setExtension(content.getNameExtension());
        fileNode.setFileType(content.getType().getFileType());
        fileNode.setMTime(content.getMtimeAsDate());
        fileNode.setCTime(content.getCtimeAsDate());
        fileNode.setATime(content.getAtimeAsDate());
        fileNode.setCrTime(content.getCrtimeAsDate());
        fileNode.setFileSystemType(content.getFileSystem().getFsType().getDisplayName());

        return fileNode;
    }

    boolean validateDeviceId(String deviceId, DataSource dataSourceEntity){
        return deviceId.equalsIgnoreCase(dataSourceEntity.getCaseEntity().getDeviceId());
    }
}
