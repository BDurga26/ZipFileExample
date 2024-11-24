package com.example.zip.file.example.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.zip.file.example.entity.ZipFileEntity;
import com.example.zip.file.example.repository.ZipFileRepository;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipFileService {

 private static final String STORAGE_DIRECTORY = "C:\\Users\\jaidurgabhavanib\\OneDrive - Mavenir Systems, Inc\\Documents\\ZipFileTask\\"; 

 @Autowired
 private ZipFileRepository fileRepository;

 public String uploadAndUnzipFile(MultipartFile file) throws IOException {
	 
     String fileName = file.getOriginalFilename();
     String targetDir = STORAGE_DIRECTORY +"/" + UUID.randomUUID().toString();
 
	 if (fileName != null && fileName.endsWith(".zip")) {
    
    
     Path targetPath = Paths.get(targetDir);
     Files.createDirectories(targetPath);
     

     try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
         ZipEntry zipEntry;
         while ((zipEntry = zis.getNextEntry()) != null) {
             Path filePath = targetPath.resolve(zipEntry.getName());
             if (zipEntry.isDirectory()) {
                 Files.createDirectories(filePath);
             } else {
                 Files.createDirectories(filePath.getParent());
                 try (OutputStream os = Files.newOutputStream(filePath)) {
                     zis.transferTo(os);
                 }
             }
         }
     }
	 }
	 else {
		 
	            Path destinationPath = Paths.get(targetDir, fileName);
	            file.transferTo(destinationPath);
	        }
	 

     
     ZipFileEntity filePathEntity = new ZipFileEntity();
     filePathEntity.setPath(targetDir);
     filePathEntity.setFileName(file.getOriginalFilename());
     fileRepository.save(filePathEntity);

     return "File uploaded and unzipped successfully.";
 }

 public Optional<ZipFileEntity> getFilePathByFileName(String fileName) {
     return fileRepository.findByFileName(fileName);
 }

 public byte[] zipFilesFromDirectory(String directoryPath) throws IOException {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     try (ZipOutputStream zos = new ZipOutputStream(baos)) {
         Path folderPath = Paths.get(directoryPath);
         Files.walk(folderPath)
                 .filter(Files::isRegularFile)
                 .forEach(file -> {
                     ZipEntry zipEntry = new ZipEntry(folderPath.relativize(file).toString());
                     try {
                         zos.putNextEntry(zipEntry);
                         Files.copy(file, zos);
                         zos.closeEntry();
                     } catch (IOException e) {
                         throw new RuntimeException("Error zipping files", e);
                     }
                 });
     }
     return baos.toByteArray();
 }
}
