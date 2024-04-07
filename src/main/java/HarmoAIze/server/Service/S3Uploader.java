package HarmoAIze.server.Service;

import HarmoAIze.server.Repository.S3Repository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;
    private S3Repository s3Repository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public S3Uploader(AmazonS3Client amazonS3Client, S3Repository s3Repository) {
        this.amazonS3Client = amazonS3Client;
        this.s3Repository = s3Repository;
    }

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException { // dirName의 디렉토리가 S3 Bucket 내부에 생성됨
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadFileUrl = putS3(uploadFile, fileName);
        if (uploadFileUrl != null) {
            s3Repository.saveFileURL(fileName, uploadFileUrl);
        }
        removeNewFile(uploadFile);  // convert()함수로 인해서 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadFileUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            System.out.println("파일이 삭제되었습니다.");
        }else {
            System.out.println("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile multipartFile) throws  IOException {
        byte[] bytes = multipartFile.getBytes();
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        }

        //Mad::Lib를 이용해서 mp3파일을 midi파일로 변환하면 된다.
        return Optional.of(file);

    }

    public void deleteImageFromS3(String imageAddress){
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
        }catch (Exception e){
            throw new AmazonS3Exception("delete error");
        }
    }

    private String getKeyFromImageAddress(String imageAddress){
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new AmazonS3Exception("URL error");
        }
    }
}
