package com.mq.ftp.service;


import com.mq.ftp.entity.FileInfoModel;

import java.util.List;

public interface FtpDataService {
    int process(List<FileInfoModel> txtList);
}
