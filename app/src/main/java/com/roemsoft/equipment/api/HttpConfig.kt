package com.roemsoft.equipment.api

class HttpConfig {

    companion object {

        const val URL_PATH = "ajax/ajaxDataServer.aspx"

        const val REQ_TYPE = "ReqType"
        const val REQ_STR = "ReqStr"

        /********************** REQ_TYPE *********************/

        // 登录
        const val REQ_TYPE_LOGIN = "APPUserLogin"

        // 设备档案列表查询
        const val REQ_TYPE_ARCHIVE_LIST = "GDItemSearch"

        // 设备类别名称列表
        const val REQ_TYPE_CATEGORY = "GDItemClass"
        // 设备档案 提交
        const val REQ_TYPE_ARCHIVE_SUBMIT = "PostGDItem"

        // 归属公司 查询
        const val REQ_TYPE_COST_COMPANY = "GetDepartlist"
        // 责任部门 查询
        const val REQ_TYPE_DEPARTMENT = "GetDepart"
        // 责任人 查询
        const val REQ_TYPE_PERSON = "GetPerson"

        // 盘点入库 提交
        const val REQ_TYPE_CHECKIN_SUBMIT = "CHPDSMIn"

        // 资产查询
        const val REQ_TYPE_ASSETS_SEARCH = "GDNumSearch"

        // 设备和基础信息查询
        const val REQ_TYPE_ARCHIVE_BASE_SEARCH = "GDSearch"

        // 设备转移未审核列表 查询
        const val REQ_TYPE_TRANSFER_LIST = "GDZY"

        // 设备转移未审核列表 提交
        const val REQ_TYPE_TRANSFER_LIST_SUBMIT = "GDMtlStgZYTJApp"

        // 设备转移 提交
        const val REQ_TYPE_TRANSFER_SUBMIT = "GDZYIn"

        // 设备借出未审核列表 查询
        const val REQ_TYPE_LEND_LIST = "GDJC"

        // 设备借出未审核列表 提交
        const val REQ_TYPE_LEND_LIST_SUBMIT = "GDMtlStgJCOutTJApp"

        // 设备借出 提交
        const val REQ_TYPE_LEND_SUBMIT = "GDJCOut"

        // 设备归还未审核列表 查询
        const val REQ_TYPE_RETURN_LIST = "GDGH"

        // 设备归还未审核列表 提交
        const val REQ_TYPE_RETURN_LIST_SUBMIT = "GDMtlStgGHTJApp"

        // 设备归还 提交
        const val REQ_TYPE_RETURN_SUBMIT = "GDGHIn"

        // 供应商 往来单位 查询
        const val REQ_TYPE_PROVIDER_SEARCH = "GetProvider"



        /********************** REQ_STR *********************/

        // 用户名
        const val REQ_STR_USER_NO = "UserNo"
        // 用户密码
        const val REQ_STR_USER_PW = "UserPwd"

        // 设备档案 类别名称
        const val REQ_STR_ARCHIVE_CATEGORY = "ClassName"
        // 设备档案 设备编号
        const val REQ_STR_ARCHIVE_NO = "ItemNo"
        // 设备档案 设备名称
        const val REQ_STR_ARCHIVE_NAME = "ItemName"
        // 设备档案 品牌
        const val REQ_STR_ARCHIVE_BRAND = "XHModel"
        // 设备档案 规格型号
        const val REQ_STR_ARCHIVE_SPEC = "SpecName"
        // 设备档案 计量单位
        const val REQ_STR_ARCHIVE_UNIT = "UnitName"

        // 用户名
        const val REQ_STR_USER = "UserName"

        // 产量
        const val REQ_STR_QTY = "Qty"

        // 归属公司编码
        const val REQ_STR_COST_COM_NO = "CostDepartNo"
        // 固定资产编码
        const val REQ_STR_ASSETS_NO = "GDNum"
        // 责任部门
        const val REQ_STR_DEPARTMENT = "GDUseDepartName"
        // 责任人
        const val REQ_STR_PERSON = "GDPerson"
        // 存放位置
        const val REQ_STR_AREA = "CFPostion"
        // 原责任部门
        const val REQ_STR_DEPARTMENT_OLD = "OldGDUseDepartName"
        // 原责任人
        const val REQ_STR_PERSON_OLD = "OldGDPerson"
        // 原存放位置
        const val REQ_STR_AREA_OLD = "OldCFPostion"
        // 来源类别
        const val REQ_STR_SOURCE = "LYType"
        // 借出归还往来单位
        const val REQ_STR_COMPANY = "CompanyName"

        //单号
        const val REQ_STR_TK_NO = "TkNo"

        const val REQ_STR_MAKER = "Maker"
    }

}