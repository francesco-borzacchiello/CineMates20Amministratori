package it.unina.ingSw.cineMates20.model;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import it.unina.ingSw.cineMates20.utils.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class S3Manager {

    private final AmazonS3 s3Client;
    private final String BUCKET_NAME;

    public S3Manager() {
        String accessKey = Resources.getAwsAccessKey();
        String secretKey = Resources.getAwsSecretKey();
        BUCKET_NAME = Resources.getS3BucketName();

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        s3Client = AmazonS3ClientBuilder.standard().withCredentials
                (new AWSStaticCredentialsProvider(credentials)).build();
    }

    public boolean uploadImage(@NotNull String userEmail, @NotNull File file) {
        try {
            s3Client.putObject(new PutObjectRequest(BUCKET_NAME, "Img/" + userEmail + ".jpg", file));
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    @Nullable
    public InputStream getProfilePictureInputStream(@NotNull String userEmail) {
        InputStream pictureInputStream = null;
        S3Object s3Object = null;

        if(s3Client.doesObjectExist(BUCKET_NAME, "Img/" + userEmail + ".jpg"))
            s3Object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, "Img/" + userEmail + ".jpg"));

        if(s3Object != null)
            pictureInputStream = s3Object.getObjectContent().getDelegateStream();

        return pictureInputStream;
    }
}