package com.sunlights.account.web;

import com.sunlights.account.service.CapitalService;
import com.sunlights.account.vo.Capital4Product;
import com.sunlights.account.vo.TotalCapitalInfo;
import com.sunlights.common.MsgCode;
import com.sunlights.common.utils.MessageUtil;
import com.sunlights.common.vo.Message;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * 我的资产的后台服务
 * 提供RESTful接口
 * 
 * @author tangweiqun	2014/10/22
 *
 */

public class CapitalController extends Controller {

    private static final play.Logger.ALogger LOG = Logger.of("customer");


	private CapitalService capitalService;
    private final Message operateSuccessMsg = new Message(MsgCode.OPERATE_SUCCESS);

    /**
	 * 获取客户的总资产信息
	 * @return
	 */
	public Result getTotalCapitalInfo() {
		Form<String> form = Form.form(String.class).bindFromRequest();
		String mobile = form.data().get("mobile");

        LOG.info("mobile = === " + mobile);
        TotalCapitalInfo totalCapitalInfo = capitalService.getTotalCapital(mobile, false);
        MessageUtil.getInstance().setMessage(operateSuccessMsg, totalCapitalInfo);
		return play.mvc.Controller.ok(MessageUtil.getInstance().toJson());
	}
	
	/**
	 * 获取客户所有产品对应的资产信息
	 * @return
	 */
	public Result getAllCapital4Prd() {
		Form<String> form = Form.form(String.class).bindFromRequest();
		String mobile = form.data().get("mobile");

        List<Capital4Product> capital4Products = capitalService.getAllCapital4Product(mobile);
		MessageUtil.getInstance().setMessage(operateSuccessMsg, capital4Products);
		return play.mvc.Controller.ok(MessageUtil.getInstance().toJson());
	}
	
	/**
	 * 获取我的资产所有信息
	 * @return
	 */
	public Result getMyCapital() {
		Form<String> form = Form.form(String.class).bindFromRequest();
		String mobile = form.data().get("mobile");
		System.out.println("mobile = " + mobile);
		
		TotalCapitalInfo totalCapitalInfo = capitalService.getTotalCapital(mobile, true);
		MessageUtil.getInstance().setMessage(operateSuccessMsg, totalCapitalInfo);
		return play.mvc.Controller.ok(MessageUtil.getInstance().toJson());
	}
}
