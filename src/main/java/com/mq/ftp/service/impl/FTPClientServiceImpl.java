package com.mq.ftp.service.impl;

import com.mq.ftp.dto.FileInfoDto;
import com.mq.ftp.entity.FileInfoModel;
import com.mq.ftp.service.FTPClientService;
import com.mq.ftp.service.FtpDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class FTPClientServiceImpl implements FTPClientService {

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.username}")
    private String userName;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.filepath}")
    private String fileBasePath;

    @Resource
    FtpDataService ftpDataService;

    @Override
    public int readFtpFile(String ftpFilePath) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(1000 * 30);
        ftpClient.setControlEncoding("utf-8");

        InputStream inputStream = null;
        int rows = 0;

        try {
            ftpClient.setDefaultPort(ftpPort);
            ftpClient.connect(ftpHost, ftpPort);
            ftpClient.login(userName, password);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                log.error("未连接到FTP，用户名或密码错误!");
                return -1;
            } else {
                log.info("FTP连接成功!");
            }

            String ftpPath = fileBasePath + ftpFilePath;
            ftpClient.changeWorkingDirectory(ftpPath);

            //调用FTPClient.listFiles()方法时返回的始终为空，但是代码又运行正常没有异常抛出。
            // 因为ftp server可能每次开启不同的端口来传输数据，但是在linux上，由于安全限制，可能某些端口没有开启，所以就出现阻塞。
            ftpClient.enterLocalPassiveMode();

            FTPFile[] ftpFiles = ftpClient.listFiles(ftpPath);
            log.info("FtpFilePath={}", ftpPath);

            //遍历当前目录下的文件，判断要读取的文件是否在当前目录下
            for (FTPFile ftpFile : ftpFiles) {
                log.info("FtpFilename={}", ftpFile.getName());

                if (ftpFile.isFile()) {
                    inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
                    if (null == inputStream) {
                        log.info("FtpFilename={} is null", ftpFile.getName());
                        continue;
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    List<FileInfoModel> fileInfoModels = new ArrayList<>();
                    String filename = getListByFtpFile(ftpFile, reader, fileInfoModels);

                    rows += ftpDataService.process(fileInfoModels);
                    log.info("FtpFilename={} save db success, size={}", filename, rows);

                    // 要求用到操作ftp文件等功能，主要遇到的问题是当要遍历文件夹里的文件时或者下载所有文件时，
                    // 如果没有使用completePendingCommand()这方法，则只能处理一个文件，
                    // 在处理第二个文件的时候（即第二次调用retrieveFileStream()方法的时候）返回null。
                    ftpClient.completePendingCommand();
                }
            }

            ftpClient.logout();
        } catch (IOException e) {
            log.error("read file fail {}", e);
            return -1;

        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    log.error("disconnect fail {}", e);
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("inputStream close fail {}", e);
                }
            }

        }
        return rows;
    }

    private String getListByFtpFile(FTPFile ftpFile, BufferedReader reader, List<FileInfoModel> fileInfoModels) throws IOException {
        String line;
        int index = -1;
        String filename = ftpFile.getName();
        int subIndex = filename.indexOf(".");
        if (subIndex > 0) {
            filename = filename.substring(0, subIndex);
        }
        while ((line = reader.readLine()) != null) {
            index++;
            if (index == 0) {
                continue;
            }
            FileInfoModel dto = strConvertDto(filename, line);
            if (null != dto) {
                fileInfoModels.add(dto);
            }
        }
        return filename;
    }

    private FileInfoModel strConvertDto(String filename, String line) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        String[] arrays = line.split("\\|\\$");
        if (arrays.length > 0) {
            FileInfoDto dto = new FileInfoDto(
                    arrays[0], filename
            );
            FileInfoModel dbModel = new FileInfoModel();
            BeanUtils.copyProperties(dto, dbModel);
            dbModel.setId(UUID.randomUUID().toString().replace("-", ""));
            return dbModel;
        }
        return null;
    }

}
