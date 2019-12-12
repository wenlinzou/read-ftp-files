###### 1 打包maven
```shell
mvn clean install -Dmaven.test.skip=true 
```
###### 2 运行jar文件
```shell
# 指定运行环境
nohup java -jar read-ftp-files.jar --spring.profiles.active=prod &
```

