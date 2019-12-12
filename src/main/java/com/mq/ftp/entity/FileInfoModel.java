package com.mq.ftp.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 上报数据
 *
 * @author wenlinzou
 */
@Data
public class FileInfoModel implements Serializable {

    private String id;

    private String content;

    private String filename;

    private Date createTime;

    private Date updateTime;
}
