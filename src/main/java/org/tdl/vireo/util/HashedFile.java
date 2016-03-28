package org.tdl.vireo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.tdl.vireo.Application;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.model.repo.ConfigurationRepo;

@Service
@DependsOn("systemDataLoader")
public class HashedFile {    
    
    @Autowired
    private ConfigurationRepo configurationRepo;
    
    public HashedFile() { }

    /**
     * Writes an InputStream out to a new uuid-based filename in the attachments location configured in Vireo
     * 
     * @param is
     *            the InputStream
     * @throws IOException
     */
    public UUID write(InputStream is) throws IOException {
        UUID uuid = UUID.randomUUID();

        // Make sure the hash directory exists.
        File file = getFile(uuid);
        file.getParentFile().mkdirs();
        OutputStream os = new FileOutputStream(file);
        FileCopyUtils.copy(is, os);
        
        return uuid;
    }

    /**
     * Returns pointer to a file in the Vireo attachments location from a given uuid
     * 
     * @param uuid
     *            of file
     * @return File pointer of file
     */
    public File getFile(UUID uuid) {
        // Get the UUID as a string
        String uuidString = uuid.toString();

        // get subdirectory names
        String subDir1 = uuidString.substring(0, 2);
        String subDir2 = uuidString.substring(2, 4);
        String subDir3 = uuidString.substring(4, 6);
        String subDir4 = uuidString.substring(6, 8);

        // Concatenate the subDirs with the Store location
        File hashDir = new File(getStore(), subDir1 + File.separator + subDir2 + File.separator + subDir3 + File.separator + subDir4);

        // get the file
        File file = new File(hashDir, uuidString);

        return file;
    }

    /**
     * Gets the attachment storage location from Vireo Configuration
     * 
     * @return File pointing to parent directory for attachments
     */
    public File getStore() {
        String name = configurationRepo.getByName(ConfigurationName.APPLICATION_ATTACHMENTS_PATH).getValue();
        File store = null;
        
        if (new File(name).isAbsolute()) {
            store = new File(name);
        } else {
            store = new File(Application.BASE_PATH + File.separator + name);
        }
        if (!store.exists()) {
            store.mkdirs();
        }
        return store;
    }

}
