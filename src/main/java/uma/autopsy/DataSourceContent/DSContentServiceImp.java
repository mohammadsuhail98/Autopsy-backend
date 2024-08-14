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

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            AbstractFile content = skcase.getAbstractFileById(fileId);
            return getFileNode(content);
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    FileNode getFileNode(AbstractFile content) throws TskCoreException {
        return new FileNode(content.getName(), content.getUniquePath(), content.getType().getName(), content.getId(), content.getUid(),
                content.getGid(), content.isDir(), content.isFile(), content.isRoot(), content.getSize(), content.getDirFlagAsString(),
                content.getMetaFlagsAsString(), content.getKnown().getName(), content.getMd5Hash(), content.getSha1Hash(), content.getSha256Hash(),
                content.getMIMEType(), content.getNameExtension(), content.getType().getFileType(), content.getMtimeAsDate(), content.getCtimeAsDate(),
                content.getAtimeAsDate(), content.getCrtimeAsDate(),
                content.getFileSystem().getFsType().getDisplayName());
    }
    @Override
    public byte[] getHexFile(int dataSourceId, String deviceId, int fileId){
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        System.out.println(caseDir);
        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            AbstractFile file = skcase.getAbstractFileById(fileId);
            byte[] contentBytes = new byte[(int) file.getSize()];
            file.read(contentBytes, 0, contentBytes.length);
            return toHexString(contentBytes).getBytes();

        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 16) {
            sb.append(String.format("0x%08X: ", i));

            // Hex values
            for (int j = 0; j < 16; j++) {
                if (i + j < bytes.length) {
                    sb.append(String.format("%02X ", bytes[i + j]));
                } else {
                    sb.append("   ");
                }
            }

            sb.append(" ");

            // ASCII values
            for (int j = 0; j < 16; j++) {
                if (i + j < bytes.length) {
                    char c = (char) bytes[i + j];
                    if (c < 32 || c > 126) {
                        sb.append('.');
                    } else {
                        sb.append(c);
                    }
                }
            }

            sb.append('\n');
        }
        return sb.toString();
    }

    boolean validateDeviceId(String deviceId, DataSource dataSourceEntity){
        return deviceId.equalsIgnoreCase(dataSourceEntity.getCaseEntity().getDeviceId());
    }
}
