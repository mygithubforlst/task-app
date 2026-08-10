package com.nrec.service.common.constants;

import com.nrec.base.common.exception.StatusType;

/**
 * 类说明
 * app服务错误码定义
 *
 * @author chenjia
 * @date 2017/10/19
 */
public class AppConstant {

    public static final String MASTER_ADMIN = "MASTER_ADMIN";

    public enum Status implements StatusType {
        /*-----------app服务错误码-------------*/
        GENERIC_API_ERROR_CODE(101, "API接口错误"),
        EXCEL_EXPORT_ERROR(109, "业务数据转为Workbook失败"),
        EXCEL_SAVE_ERROR(108, "业务数据导出保存为Excel文件失败"),
        EXCEL_CREATE_ERROR(107, "Excel临时文件创建失败"),
        FILE_NOT_EXISTED(103, "文件不存在");
        private final int code;
        private final String reason;

        Status(int statusCode, String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
        }

        @Override
        public int getStatusCode() {
            return this.code;
        }

        @Override
        public String getReasonPhrase() {
            return this.reason;
        }

    }
}