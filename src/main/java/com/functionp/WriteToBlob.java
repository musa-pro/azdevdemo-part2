package com.functionp;

import java.io.*;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

/**
 * Azure Functions with HTTP Trigger.
 */
public class WriteToBlob {
    /**
     * This function listens at endpoint "/api/writeToBlob". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/writeToBlob
     * 2. curl {your host}/api/writeToBlob?name=HTTP%20Query
     */
    @FunctionName("writeToBlob")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        //  String query = request.getQueryParameters().get("name");
        String content = request.getBody().get();

        // Parse the connection string and create a blob client to interact with Blob storage
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(System.getenv("AzureWebJobsStorage"));
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference("testblobstorage");

        // Create the container if it does not exist with public access.
        File sourceFile = File.createTempFile("sampleFile", ".txt");
        Writer output = new BufferedWriter(new FileWriter(sourceFile));
        output.write(content);
        output.close();

        CloudBlockBlob blob = container.getBlockBlobReference(sourceFile.getName());
        blob.uploadFromFile(sourceFile.getAbsolutePath());

        return request.createResponseBuilder(HttpStatus.OK).build();

    }
}
