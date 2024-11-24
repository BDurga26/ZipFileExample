package com.example.zip.file.example.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.zip.file.example.entity.ZipFileEntity;
import com.example.zip.file.example.service.ZipFileService;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/files")
public class ZipFileController {

 @Autowired
 private ZipFileService fileService;

 
 @PostMapping
 public ResponseEntity<String> uploadZipFile(@RequestParam("file") MultipartFile file) {
     try {
         String message = fileService.uploadAndUnzipFile(file);
         return ResponseEntity.ok(message);
     } catch (IllegalArgumentException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
     } catch (IOException e) {
         return ResponseEntity.status(500).body("Error processing the file upload.");
     }
 }

 
 @GetMapping("/{fileName}")
 public ResponseEntity<InputStreamResource> downloadZipFile(@PathVariable String fileName) {
     try {
         ZipFileEntity filePathEntity = fileService.getFilePathByFileName(fileName)
                 .orElseThrow(() -> new FileNotFoundException("File path not found for file name " + fileName));

         byte[] zipBytes = fileService.zipFilesFromDirectory(filePathEntity.getPath());

         InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(zipBytes));
         HttpHeaders headers = new HttpHeaders();
         headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

         return ResponseEntity.ok()
                 .headers(headers)
                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                 .body(resource);

     } catch (FileNotFoundException e) {
         return ResponseEntity.status(404).body(null);
     } catch (IOException e) {
         return ResponseEntity.status(500).body(null);
     }
 }
}
