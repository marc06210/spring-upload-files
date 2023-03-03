package com.example.uploadfiles;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@RestController
@Log4j2
//@CrossOrigin() // open for every origins
public class MultipleFilesUploadController {

    @PostConstruct
    public void init() {
        log.debug("init()");
        createDirIfNotExist(FileUtil.targetDirectory(null));
    }

    /**
     * http -f POST :8080/upload files@file1.jpg files@files2.jpg ... files@filesX.jpg
     * @param files
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam(name = "dirName", defaultValue = "") String dirName) {
        log.debug("uploadFiles({} files, {})", files.length, dirName);
        createDirIfNotExist(FileUtil.targetDirectory(dirName));
        StringJoiner joiner = new StringJoiner(", ", "Uploaded files: '", "'");
        Arrays.asList(files)
                .forEach(file -> {
                    byte[] bytes = new byte[0];
                    try {
                        bytes = file.getBytes();
                        log.debug("uploading file: {}", file.getOriginalFilename());
                        Files.write(Paths.get(FileUtil.targetDirectoryName(dirName) + "/" + file.getOriginalFilename()), bytes);
                        joiner.add(file.getOriginalFilename());
                    } catch(IOException eIO) {
                        log.error("error uploading file", eIO);
                    }
                });
        return ResponseEntity.ok(joiner.toString());
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> getListFiles(@RequestParam(name = "dirName", defaultValue = "") String dirName) {
        log.info("getListFiles({})", dirName);
        File directory = new File(FileUtil.folderPath + "/" + dirName);
        return directory.exists() ?
                ResponseEntity.ok(Arrays.asList(directory.list()))
                :
                ResponseEntity.internalServerError().body(Arrays.asList("unknown target"));
    }

    private void createDirIfNotExist(File directory) {
        log.debug("createDirIfNotExist({})", directory.getAbsolutePath());
        if (!directory.exists()) {
            log.debug("creating the directory");
            directory.mkdirs();
            log.debug("directory created");
        }
    }

    @ExceptionHandler({ Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        log.error("Something went wrong", e);
        e.printStackTrace();
        return "MGU unknown error detected";
    }
}
