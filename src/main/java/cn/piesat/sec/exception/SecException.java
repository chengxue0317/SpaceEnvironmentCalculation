package cn.piesat.sec.exception;

import cn.piesat.kjyy.core.exception.BaseException;
import cn.piesat.kjyy.core.model.enums.ICommonResponse;
/**
 * sec异常类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
public class SecException extends BaseException {
    /**
     * 所属模块
     * @param iCommonResponse 通用响应类
     */
    public SecException(ICommonResponse iCommonResponse) {
        super(iCommonResponse);
    }
}
