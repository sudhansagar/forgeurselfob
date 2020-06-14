package com.forgeurself.ob.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author madhusudhan.gr
 */
@Component
public class AzureBlobStorageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureBlobStorageUtils.class);

    @Value("${com.forgeurself.ob.blobEndPointUrl}")
    private String BLOB_END_POINT_URL;

    @Value("${com.forgeurself.ob.blobContainerName}")
    private String BLOB_CONTAINER_NAME;

    @Value("${com.forgeurself.ob.insert-your-sas-token}")
    private String SAS_TOKEN;

    public boolean blobFileStorage(String filePath, String fileName){
        LOGGER.info("Inside blobFileStorage... ");
        boolean status =false;
        String file = filePath+"/"+fileName;

        /* Create a new BlobServiceClient with a SAS Token */
      /*BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(BLOB_END_POINT_URL)
                .sasToken(SAS_TOKEN)
                .buildClient();
*/
        String connectStr = "DefaultEndpointsProtocol=https;AccountName=openbankingpoc;AccountKey=swZr7dtqWWoK7csG6TUViOTDkoAI1IyLXTzKLUb3Q9Bbr6woULt/i43HOaDc2ZbwmGUvFrKPpOZy8S7xBk2OAg==;EndpointSuffix=core.windows.net";
        // Create a BlobServiceClient object which will be used to create a container client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();

        /* Create a new container client */
        BlobContainerClient containerClient = null;
        try {

            //Create a unique name for the container
            //String containerName = "openbankingpoc" + java.util.UUID.randomUUID();
            //containerClient = blobServiceClient.createBlobContainer(containerName);

            LOGGER.debug("Uploading to blob container : " +BLOB_CONTAINER_NAME);
            containerClient = blobServiceClient.getBlobContainerClient(BLOB_CONTAINER_NAME);
            LOGGER.debug("Fetching blob container name : " + containerClient.getBlobContainerName());

            /* Upload the file to the container */
            BlobClient blobClient = containerClient.getBlobClient(fileName);
            LOGGER.debug("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());

            blobClient.uploadFromFile(file);

            status = true;
        } catch (BlobStorageException ex) {
            // The container may already exist, so don't throw an error
            if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                throw ex;
            }
        }catch (Exception ex){
            throw ex;
        }
        return status;
    }
}
