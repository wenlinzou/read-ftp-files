package com.mq.ftp.service.impl;

import com.mq.ftp.dao.FileInfoDao;
import com.mq.ftp.entity.FileInfoModel;
import com.mq.ftp.service.FtpDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 将文件数据批量存入数据库表
 *
 * @author wenlinzou
 */
@Slf4j
@Service
public class FtpDataServiceImpl implements FtpDataService {

    @Value("${db.insert.max.size}")
    private int maxSize;

    @Resource
    private FileInfoDao fileInfoDao;

    @Override
    public int process(List<FileInfoModel> txtList) {
        int saveDbSize = 0;
        if (!CollectionUtils.isEmpty(txtList)) {
            int txtTotalSize = txtList.size();
            if (txtTotalSize <= maxSize) {
                saveDbSize = fileInfoDao.insertByBatch(txtList);
            } else {
                int loop = txtTotalSize / maxSize;
                List<FileInfoModel> subList;
                for (int i = 0; i < loop; i++) {
                    subList = txtList.subList(i * maxSize, (i + 1) * maxSize);
                    saveDbSize += fileInfoDao.insertByBatch(subList);
                }
                subList = txtList.subList(loop * maxSize, txtTotalSize);
                if (!CollectionUtils.isEmpty(subList)) {
                    saveDbSize += fileInfoDao.insertByBatch(subList);
                }
            }

        }
        log.info("save file to db size={}", saveDbSize);
        return saveDbSize;
    }


}
