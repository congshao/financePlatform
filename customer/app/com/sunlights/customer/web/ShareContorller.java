package com.sunlights.customer.web;

import com.sunlights.common.MsgCode;
import com.sunlights.common.Severity;
import com.sunlights.common.exceptions.BusinessRuntimeException;
import com.sunlights.common.utils.QRcodeByte;
import com.sunlights.common.vo.Message;
import com.sunlights.customer.ActivityConstant;
import com.sunlights.customer.factory.ShareInfoServiceFactory;
import com.sunlights.customer.service.ShareInfoService;
import com.sunlights.customer.vo.QRcodeVo;
import com.sunlights.customer.vo.ShareInfoContext;
import com.sunlights.customer.vo.ShareInfoVo;
import com.sunlights.customer.vo.ShareVo;
import models.CustomerSession;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by Administrator on 2014/12/3.
 */
@Transactional
public class ShareContorller extends ActivityBaseController{


    private Form<ShareVo> shareParameterForm = Form.form(ShareVo.class);

    /**
     * 获得byte流图片
     * @return
     */
    public Result getQRcodeToByte(){

        //1、首先获得手机号
        CustomerSession customerSession = getCustomerSession();
        String custNo = customerSession.getCustomerId();//获得客户id
        Logger.debug("custNo === " + custNo);
        if(StringUtils.isEmpty(custNo)){
            return notFound("用户登录已经超时,请重新登录");
        }

        ShareVo shareVo = null;
        Http.RequestBody body = request().body();
        if (body.asJson() != null) {
            shareVo = Json.fromJson(body.asJson(), ShareVo.class);
        }

        if (body.asFormUrlEncoded() != null) {
            shareVo = shareParameterForm.bindFromRequest().get();
        }
        String type = shareVo.getType();
        String id = shareVo.getId();
        if(StringUtils.isEmpty(type)) {
            type = ActivityConstant.SHARE_TYPE_INVITER;
        }
        Logger.debug("type = " + type + " id = " + id);

        Message message = null;
        ShareInfoService shareInfoService = ShareInfoServiceFactory.getShareInfoService(type);
        if(shareInfoService == null) {
            Logger.error("不支持的分享类型");
            message = new Message(Severity.INFO, MsgCode.NOT_SUPPORT_SHARE_TYPE);
            messageUtil.setMessage(message);
            return ok(messageUtil.toJson());
        }

        message = new Message(Severity.INFO, MsgCode.SHARE_QUERY_SUCC);

        ShareInfoContext context = new ShareInfoContext();
        context.setRefId(id);
        context.setCustNo(custNo);
        context.setType(type);
        ShareInfoVo shareInfoVo = shareInfoService.getShareInfoByType(context);

        String shorturl = shareInfoVo.getShortUrl();
        //3、将内容存入对象
        QRcodeByte qrcode = new QRcodeByte();
        byte[] pngData = qrcode.getQRcodeByte(shorturl);//加入短路径如："http://t.cn/RzJWtFA"
        QRcodeVo qRcodeVo=new QRcodeVo();
        qRcodeVo.setQrcodeByte(pngData);
        Logger.debug("图片二进制流:"+qRcodeVo.getQrcodeByte());

        messageUtil.setMessage(new Message(Severity.INFO, MsgCode.ABOUT_QUERY_SUCC), qRcodeVo);
        Logger.debug("返回给前端的内容----:"+messageUtil.toJson());
        return ok(messageUtil.toJson());

    }





    /**
     * 分享接口
     *
     * @return
     */
    public Result share() {
        String custNo = "";
        try {
            CustomerSession customerSession = getCustomerSession();
            custNo = customerSession.getCustomerId();//获得客户id
        } catch (Exception e) {
            Logger.debug("没有登录。。");
        }

        ShareVo shareVo = null;
        Http.RequestBody body = request().body();
        if (body.asJson() != null) {
            shareVo = Json.fromJson(body.asJson(), ShareVo.class);
        }

        if (body.asFormUrlEncoded() != null) {
            shareVo = shareParameterForm.bindFromRequest().get();
        }
        String type = shareVo.getType();
        String id = shareVo.getId();
        Logger.debug("type = " + type + " id = " + id);

        Message message = null;
        ShareInfoService shareInfoService = ShareInfoServiceFactory.getShareInfoService(type);
        if(shareInfoService == null) {
            Logger.error("不支持的分享类型");
            message = new Message(Severity.INFO, MsgCode.NOT_SUPPORT_SHARE_TYPE);
            messageUtil.setMessage(message);
            return ok(messageUtil.toJson());
        }

        message = new Message(Severity.INFO, MsgCode.SHARE_QUERY_SUCC);
        try {
            ShareInfoContext context = new ShareInfoContext();
            context.setRefId(id);
            context.setCustNo(custNo);
            context.setType(type);
            ShareInfoVo shareInfoVo = shareInfoService.getShareInfoByType(context);

            shareVo = new ShareVo();
            shareVo.setId(id);
            shareVo.setType(type);
            shareVo.setShorturl(shareInfoVo.getShortUrl());
            shareVo.setContent(shareInfoVo.getContent());
            shareVo.setImageurl(shareInfoVo.getImageUrl());
            shareVo.setTitle(shareInfoVo.getTitle());

            messageUtil.setMessage(message, shareVo);
            Logger.debug("返回给前端的内容----》:" + messageUtil.toJson());
            return ok(messageUtil.toJson());
        } catch (BusinessRuntimeException be) {
            if(MsgCode.LOGIN_TIMEOUT.getCode().equals(be.getErrorCode())) {
                message = new Message(Severity.INFO, MsgCode.LOGIN_TIMEOUT);
                message.setSummary("您还没有登录");
                Logger.error("您还没有登录", be);
            }
        } catch (Exception e) {
            message = new Message(Severity.INFO, MsgCode.ACTIVITY_SYS_ERROR);
            message.setSummary("系统异常");
            Logger.error("系统异常", e);
        }
        messageUtil.setMessage(message);
        return ok(messageUtil.toJson());
    }
}
