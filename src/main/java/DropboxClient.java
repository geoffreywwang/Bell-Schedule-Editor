import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that authenticates and uploads files to Dropbox.
 */
public class DropboxClient {

    private DbxClientV2 client; //Dropbox Client

    /**
     * Creates a Dropbox client
     *
     * @param ACCESS_TOKEN API key that should not be shared
     */
    public DropboxClient(String ACCESS_TOKEN) {
        DbxRequestConfig config = new DbxRequestConfig("bellScheduleEditor/1.0");
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    /**
     * Uploads file to dropbox
     *
     * @param currentFileLocation Location of the file one wants to upload
     * @param targetFileName Name of file on Dropbox
     * @throws DbxException Error in uploading file
     * @throws IOException Error in finding file
     */
    public void uploadFile(String currentFileLocation, String targetFileName) throws DbxException, IOException {
        try (InputStream in = new FileInputStream(currentFileLocation)) {
            FileMetadata metadata = client.files().uploadBuilder("/" + targetFileName).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        }
    }
}
