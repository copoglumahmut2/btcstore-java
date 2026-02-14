package com.btc.service.impl;

import com.btc.domain.model.custom.CmsCategoryModel;
import com.btc.domain.model.custom.MediaModel;
import com.btc.domain.model.custom.SiteModel;
import com.btc.persistence.dao.MediaDao;
import com.btc.service.CmsCategoryService;
import com.btc.service.MediaService;
import com.btc.service.ModelService;
import com.btc.service.exception.media.MediaDeleteException;
import com.btc.service.exception.media.MediaStorageException;
import com.btc.service.exception.system.StoreSystemException;
import com.btc.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    protected static final char DOT = '.';
    protected static final char SLASH = '/';
    protected static final char UNDERSCORE = '_';
    protected static final FileNameMap MIMETYPES = URLConnection.getFileNameMap();
    protected static final String DATE_FORMAT = "yyyyMMdd";
    protected static final String TIME_FORMAT = "HHmmss";
    protected static final String MEDIA_ERROR_MSG = "Error occurred while saving media ";
    public static final String MEDIA_PATTERN = "/media";

    protected final ModelService modelService;
    protected final CmsCategoryService cmsCategoryService;
    protected final MediaDao mediaDao;


    @Value("${media.folder.public.path}")
    protected String publicMediaFolder;

    @Value("${media.folder.root.path}")
    protected String mediaRootPath;

    @Value("${media.folder.private.path}")
    protected String secureMediaFolder;

    @Value("${media.folder.serve.path}")
    protected String mediaServePath;


    @Override
    public MediaModel storage(MultipartFile multipartFile, boolean secure, CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException {

        try {
            var localDateTime = LocalDateTime.now();
            var todayDate = localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            var todayTime = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));

            var rootPath = Path.of(mediaRootPath).toString();
            var filePath = StringUtils.join(secure ? secureMediaFolder : publicMediaFolder, siteModel.getCode(), SLASH, todayDate, SLASH, todayTime);


            var fullPath = StringUtils.join(rootPath, filePath);

            Files.createDirectories(Path.of(fullPath));

            var fileNameHash = RandomStringUtils.random(15, true, true);
            var destFilePath = fullPath + File.separator + fileNameHash + DOT + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            multipartFile.transferTo(new File(FilenameUtils.separatorsToSystem(destFilePath)));

            //create Media...

            var mediaModel = modelService.create(MediaModel.class);
            mediaModel.setSite(siteModel);
            mediaModel.setRealFileName(FilenameUtils.getName(multipartFile.getOriginalFilename()));
            mediaModel.setEncodedFileName(fileNameHash);
            mediaModel.setExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
            mediaModel.setFilePath(filePath);
            mediaModel.setRootPath(rootPath);
            mediaModel.setSize(multipartFile.getSize());
            mediaModel.setMime(multipartFile.getContentType());
            mediaModel.setSecure(secure);
            mediaModel.setCode(UUID.randomUUID().toString());
            mediaModel.setCmsCategory(cmsCategoryModel);
            var absolutePath = StringUtils.join(mediaModel.getFilePath(),
                    SLASH, mediaModel.getEncodedFileName(), DOT, mediaModel.getExtension());
            mediaModel.setAbsolutePath(absolutePath);
            mediaModel.setServePath(StringUtils.replace(absolutePath, MEDIA_PATTERN, StringUtils.EMPTY));
            modelService.save(mediaModel);
            return mediaModel;

        } catch (Exception e) {
            log.error(MEDIA_ERROR_MSG, ExceptionUtils.getMessage(e));
            throw new MediaStorageException(ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public MediaModel storage(String realFileName, MultipartFile multipartFile, boolean secure,
                              CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException {

        try {
            var localDateTime = LocalDateTime.now();
            var todayDate = localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            var todayTime = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));

            var rootPath = Path.of(mediaRootPath).toString();
            var filePath = StringUtils.join(secure ? secureMediaFolder : publicMediaFolder, siteModel.getCode(), SLASH, todayDate, SLASH, todayTime);


            var fullPath = StringUtils.join(rootPath, filePath);

            Files.createDirectories(Path.of(fullPath));

            var fileNameHash = RandomStringUtils.random(15, true, true);
            var destFilePath = fullPath + File.separator + fileNameHash + DOT + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            multipartFile.transferTo(new File(FilenameUtils.separatorsToSystem(destFilePath)));

            //create Media...

            var mediaModel = modelService.create(MediaModel.class);
            mediaModel.setSite(siteModel);
            mediaModel.setRealFileName(StringUtils.isNotEmpty(realFileName) ? realFileName
                    : FilenameUtils.getName(multipartFile.getOriginalFilename()));
            mediaModel.setEncodedFileName(fileNameHash);
            mediaModel.setExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
            mediaModel.setFilePath(filePath);
            mediaModel.setRootPath(rootPath);
            mediaModel.setSize(multipartFile.getSize());
            mediaModel.setMime(multipartFile.getContentType());
            mediaModel.setSecure(secure);
            mediaModel.setCode(UUID.randomUUID().toString());
            mediaModel.setCmsCategory(cmsCategoryModel);
            var absolutePath = StringUtils.join(mediaModel.getFilePath(),
                    SLASH, mediaModel.getEncodedFileName(), DOT, mediaModel.getExtension());
            mediaModel.setAbsolutePath(absolutePath);
            mediaModel.setServePath(StringUtils.replace(absolutePath, MEDIA_PATTERN, StringUtils.EMPTY));
            modelService.save(mediaModel);
            return mediaModel;

        } catch (Exception e) {
            log.error(MEDIA_ERROR_MSG, ExceptionUtils.getMessage(e));
            throw new MediaStorageException(ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public MediaModel storage(File file, boolean secure, boolean move, CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException {
        try {
            var localDateTime = LocalDateTime.now();
            var todayDate = localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            var todayTime = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));

            var rootPath = Path.of(mediaRootPath).toString();
            var filePath = StringUtils.join(secure ? secureMediaFolder : publicMediaFolder, siteModel.getCode(), SLASH, todayDate, SLASH, todayTime);


            var fullPath = StringUtils.join(rootPath, filePath);

            Files.createDirectories(Path.of(fullPath));

            var fileNameHash = RandomStringUtils.random(15, true, true);
            var destFilePath = fullPath + File.separator + fileNameHash + DOT + FilenameUtils.getExtension(file.getName());

            if (move) {
                FileUtils.moveFile(file, new File(FilenameUtils.separatorsToSystem(destFilePath)), StandardCopyOption.REPLACE_EXISTING);
            } else {
                FileUtils.copyFile(file, new File(FilenameUtils.separatorsToSystem(destFilePath)), StandardCopyOption.REPLACE_EXISTING);
            }

            //create Media...

            var mediaModel = modelService.create(MediaModel.class);
            mediaModel.setSite(siteModel);
            mediaModel.setRealFileName(FilenameUtils.getName(file.getName()));
            mediaModel.setEncodedFileName(fileNameHash);
            mediaModel.setExtension(FilenameUtils.getExtension(file.getName()));
            mediaModel.setFilePath(filePath);
            mediaModel.setRootPath(rootPath);
            mediaModel.setSize(file.length());
            mediaModel.setMime(MIMETYPES.getContentTypeFor(file.getName()));
            mediaModel.setSecure(secure);
            mediaModel.setCode(UUID.randomUUID().toString());
            mediaModel.setCmsCategory(cmsCategoryModel);
            var absolutePath = StringUtils.join(mediaModel.getFilePath(),
                    SLASH, mediaModel.getEncodedFileName(), DOT, mediaModel.getExtension());
            mediaModel.setAbsolutePath(absolutePath);
            mediaModel.setServePath(StringUtils.replace(absolutePath, MEDIA_PATTERN, StringUtils.EMPTY));
            modelService.save(mediaModel);
            return mediaModel;

        } catch (Exception e) {
            log.error(MEDIA_ERROR_MSG, ExceptionUtils.getMessage(e));
            throw new MediaStorageException(ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public MediaModel storageFromContent(String fileContent, String originalFileNameWithExtention, MimeType mimeType, boolean secure,
                                         CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException {

        try {
            var localDateTime = LocalDateTime.now();
            var todayDate = localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            var todayTime = localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));

            var rootPath = Path.of(mediaRootPath).toString();
            var filePath = StringUtils.join(secure ? secureMediaFolder : publicMediaFolder, siteModel.getCode(), SLASH, todayDate, SLASH, todayTime);
            var fullPath = StringUtils.join(rootPath, filePath);

            Files.createDirectories(Path.of(fullPath));

            var fileNameHash = RandomStringUtils.random(15, true, true);
            var fileDiskPath = StringUtils.join(fullPath, File.separator, fileNameHash, DOT, FilenameUtils.getExtension(originalFileNameWithExtention));
            var diskFile = Files.writeString(Path.of(fileDiskPath), fileContent, StandardCharsets.UTF_8);

            //create Media...

            var mediaModel = modelService.create(MediaModel.class);
            mediaModel.setSite(siteModel);
            mediaModel.setRealFileName(FilenameUtils.getName(originalFileNameWithExtention));
            mediaModel.setEncodedFileName(fileNameHash);
            mediaModel.setExtension(FilenameUtils.getExtension(originalFileNameWithExtention));

            mediaModel.setFilePath(filePath);
            mediaModel.setRootPath(rootPath);
            mediaModel.setSize(diskFile.toFile().length());
            mediaModel.setMime(mimeType.toString());
            mediaModel.setSecure(secure);
            mediaModel.setCode(UUID.randomUUID().toString());
            mediaModel.setCmsCategory(cmsCategoryModel);
            var absolutePath = StringUtils.join(mediaModel.getFilePath(),
                    SLASH, mediaModel.getEncodedFileName(), DOT, mediaModel.getExtension());
            mediaModel.setAbsolutePath(absolutePath);
            mediaModel.setServePath(StringUtils.replace(absolutePath, MEDIA_PATTERN, StringUtils.EMPTY));
            modelService.save(mediaModel);
            return mediaModel;

        } catch (Exception e) {
            log.error(MEDIA_ERROR_MSG, ExceptionUtils.getMessage(e));
            throw new MediaStorageException(ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public MediaModel getMediaByCode(String code, SiteModel siteModel) {
        code = StringUtils.replace(code,"#gotoweb",StringUtils.EMPTY);
        var mediaModel = mediaDao.getMediaModelByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(mediaModel, MediaModel.class, siteModel, code);
        return mediaModel;
    }

    @Override
    public String getMediaFileAsBinary(MediaModel mediaModel) {
        var filePath = mediaModel.getRootPath() + mediaModel.getAbsolutePath();
        try {
            return FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error occurred while reading file", ExceptionUtils.getMessage(e));
            throw new StoreSystemException("Error occurred while reading file");
        }
    }

    @Override
    public Page<MediaModel> getMediasByCategory(Pageable pageable, CmsCategoryModel cmsCategoryModel, SiteModel siteModel) {
        return mediaDao.getMediaModelsByCmsCategoryAndSiteOrderByLastModifiedDateDesc(pageable, cmsCategoryModel, siteModel);
    }


    @Override
    public Set<MediaModel> getMediasFlaggedAsDeleted(SiteModel siteModel) {
        return mediaDao.getMediaModelsByDeletedAndSite(Boolean.TRUE, siteModel);
    }

    @Override
    public void flagMediaForDelete(String code, SiteModel siteModel) throws MediaDeleteException {
        try {
            var mediaModel = mediaDao.getMediaModelByCodeAndSite(code, siteModel);
            mediaModel.setDeleted(Boolean.TRUE);
            modelService.save(mediaModel);
        } catch (Exception e) {
            throw new MediaDeleteException(ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public String generateMediaUrl(String url) {
        if (StringUtils.isNotEmpty(url)) {
            return StringUtils.join(mediaServePath, url);
        }
        return StringUtils.EMPTY;
    }
}
