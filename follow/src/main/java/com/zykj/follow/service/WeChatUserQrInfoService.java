package com.zykj.follow.service;

import com.zykj.follow.common.http.ImageDownload;
import com.zykj.follow.pojo.WeChatUserQrInfo;
import com.zykj.follow.repository.WeChatUserQrInfoRepository;
import com.zykj.follow.utils.FileToBase64;
import com.zykj.follow.utils.QrUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.Optional;

/**
 * 二维码
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-16
 */
@Service
public class WeChatUserQrInfoService {

    private final WeChatUserQrInfoRepository qrInfoRepository;

    @Autowired
    public WeChatUserQrInfoService(WeChatUserQrInfoRepository qrInfoRepository) {
        this.qrInfoRepository = qrInfoRepository;
    }

    @NotNull
    public String getParamQr(String userId,Integer expireSeconds,String actionName,Integer sceneId) {
        String paramQr;
        String path;
        Optional<WeChatUserQrInfo> qrInfoOptional = qrInfoRepository.findById(userId);

        if (!qrInfoOptional.isPresent()) {
            paramQr = QrUtils.getParamQr(expireSeconds, actionName, sceneId, userId);
            path = "./qrParam/" + userId + ".jpg";
            ImageDownload.downloadPicture(paramQr, path);
            WeChatUserQrInfo qrInfo = new WeChatUserQrInfo();
            qrInfo.setQrId(userId);
            qrInfo.setQrPath(path);
            qrInfo.setCreateDate(new Date());
            qrInfoRepository.save(qrInfo);
        }else {
            paramQr = "";
            path = qrInfoOptional.get().getQrPath();
        }

        try {
            File file = new File(path);
            if (!file.exists()) {
                paramQr = QrUtils.getParamQr(expireSeconds, actionName, sceneId, userId);
                path = "./qrParam/" + userId + ".jpg";
                ImageDownload.downloadPicture(paramQr, path);
            }
            paramQr = FileToBase64.encodeBase64File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paramQr = "data:image/jpeg;base64," + paramQr;
        return paramQr;
    }
}
