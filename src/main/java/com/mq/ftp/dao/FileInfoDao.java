package com.mq.ftp.dao;



import com.mq.ftp.entity.FileInfoModel;

import java.util.List;

public interface FileInfoDao {

    int insertByBatch(List<FileInfoModel> list);
}
