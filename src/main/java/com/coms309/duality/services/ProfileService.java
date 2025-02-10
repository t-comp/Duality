package com.coms309.duality.services;


import com.coms309.duality.model.Profile;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.entity.ContentType.*;

@Service
public class ProfileService {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private ProfileRepository profileRepository;

    public String savePhotoToStorage(MultipartFile file, Long profileId) throws IOException {
        Path directory = Paths.get(uploadPath).resolve("profile photos");
        Files.createDirectories(directory);

        String fileName = profileId + "_" + UUID.randomUUID().toString() +
                "_" + file.getOriginalFilename();

        Path targetLocation = directory.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "/profile-photos/" + fileName;

    }

    public String uploadProfilePhoto(MultipartFile file, Long profileId) throws IOException {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow();
        // Save file to storage (local/cloud)
        String photoUrl = savePhotoToStorage(file, profileId);

        profile.setProfilePic(photoUrl);
        profileRepository.save(profile);

        return photoUrl;
    }

//    private void isImage(MultipartFile file) {
//        if(!Arrays.asList(
//                IMAGE_JPEG.getMimeType(),
//                IMAGE_GIF.getMimeType(),
//                IMAGE_PNG.getMimeType()).contains(file.getContentType())) {
//            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
//        }
//    }
//
//    private Map<String, String> extractMetadata(MultipartFile file) {
//        Map<String, String> metadata = new HashMap<>();
//        metadata.put("Content-Type", file.getContentType());
//        metadata.put("Content-Length", String.valueOf(file.getSize()));
//        return metadata;
//    }


}
