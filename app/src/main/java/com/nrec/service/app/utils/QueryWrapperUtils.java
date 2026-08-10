package com.nrec.service.app.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nrec.base.common.enums.JavaBasicType;
import com.nrec.base.common.exception.ServiceException;
import com.nrec.base.common.model.PageBean;
import com.nrec.base.common.model.dto.TableFilterDto;
import com.nrec.base.common.model.dto.TableSelectFilterDto;
import com.nrec.base.common.model.qo.TableFilterQo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 条件过滤器工具类
 * @author zhuyf
 * @date 2021/12/4
 */
@Component
public class QueryWrapperUtils {

    @Value("${spring.datasource.url}")
    private String url;

    private static String dbType;


    /**
     * 暂时处理mysql、oracle(DM/kingBase)数据库
     * @author zhuyf
     * @date 2021/12/4
     */
    @PostConstruct
    public void init() {
        QueryWrapperUtils.dbType = this.url.contains("mysql") ? "mysql" : "oracle";
    }

    /**
     * 根据多个参数构建条件构造器
     * @param qw                 条件构造器
     * @param tableFilterQoList  条件过滤器qo参数
     * @param tableFilterDtoList 条件过滤器dto参数
     * @author zhuyf
     * @date 2021/12/4
     */
    private static <T> void queryWrapperBuilder(QueryWrapper<T> qw, List<TableFilterQo> tableFilterQoList, List<TableFilterDto<TableSelectFilterDto>> tableFilterDtoList) {
        if (tableFilterQoList != null && tableFilterQoList.size() > 0) {
            for (TableFilterQo tableFilterQo : tableFilterQoList) {
                checkTableFilterQoKey(tableFilterQo, tableFilterDtoList);
                switch (tableFilterQo.getType()) {
                    case Input:
                    case TimePicker:
                    case MonthPicker:
                    case TreeSelect:
                    case Select: {
                        queryWrapperSingleNoneDateBuilder(qw, tableFilterQo);
                        break;
                    }
                    case Checkbox: {
                        queryWrapperCheckboxBuilder(qw, tableFilterQo);
                        break;
                    }
                    case MultTreeSelect:
                    case MultSelect: {
                        queryWrapperMultNoneDateBuilder(qw, tableFilterQo);
                        break;
                    }
                    case RangePicker: {
                        queryWrappeRangePickerBuilder(qw, tableFilterQo);
                        break;
                    }
                    case DatePicker: {
                        queryWrapperDatePickerBuilder(qw, tableFilterQo);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 根据多个参数构建条件构造器
     * @param qw                 条件构造器
     * @param tableFilterQoList  条件过滤器qo参数
     * @param tableFilterDtoList 条件过滤器dto参数
     * @param pageBean           分页参数
     * @author zhuyf
     * @date 2021/12/4
     */
    public static <T> void queryWrapperBuilder(QueryWrapper<T> qw, List<TableFilterQo> tableFilterQoList, List<TableFilterDto<TableSelectFilterDto>> tableFilterDtoList, PageBean pageBean) {
        qw.orderBy(StringUtils.isNotEmpty(pageBean.getSortField()), pageBean.isAsc(), pageBean.getSortField());
        queryWrapperBuilder(qw, tableFilterQoList, tableFilterDtoList);
    }

    /**
     * 校验qo中key是否存在sql注入的风险
     * @param tableFilterQo      条件过滤器qo参数
     * @param tableFilterDtoList 条件过滤器dto参数
     * @author zhuyf
     * @date 2021/12/4
     */
    private static void checkTableFilterQoKey(TableFilterQo tableFilterQo, List<TableFilterDto<TableSelectFilterDto>> tableFilterDtoList) {
        List<TableFilterDto<TableSelectFilterDto>> tableFilterList = tableFilterDtoList.stream().filter(o -> o.getKey().equals(tableFilterQo.getKey())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tableFilterList)) {
            throw new ServiceException("参数key存在非法参数值");
        }
        tableFilterQo.setValueType(tableFilterList.get(0).getValueType());
    }

    /**
     * 根据参数构建条件构造器-checkbox
     * @param qw            条件构造器
     * @param tableFilterQo 条件过滤器qo参数
     * @author zhuyf
     * @date 2021/12/4
     */
    private static <T> void queryWrapperCheckboxBuilder(QueryWrapper<T> qw, TableFilterQo tableFilterQo) {
        boolean condition = StringUtils.isNotBlank(tableFilterQo.getValue());
        boolean selected = Boolean.valueOf(tableFilterQo.getValue());
        switch (tableFilterQo.getOperationType()) {
            case eq: {
                qw.eq(condition, tableFilterQo.getKey(), selected);
                break;
            }
            case ne: {
                qw.ne(condition, tableFilterQo.getKey(), selected);
                break;
            }
            case notNull: {
                if (selected) {
                    qw.isNotNull(condition, tableFilterQo.getKey());
                } else {
                    qw.isNull(condition, tableFilterQo.getKey());
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * web :YYYY-MM-DD HH:mm:ss
     * mysql:%Y-%m-%d %H:%i:%S
     * oralce:yyyy-mm-dd hh24:mi:ss
     * java ：yyyy-MM-dd HH:mm:ss
     * @param format     日期格式化表达式
     * @param dateFormat 是否日期格式化
     * @param index      条件构造器中参数索引
     * @return String
     * @author zhuyf
     * @date 2021/12/4
     */
    private static String buildDateByDbType(String format, boolean dateFormat, int index) {
        if (dateFormat) {
            String dbTypeMysql = "mysql";
            if (StringUtils.isBlank(format)) {
                throw new ServiceException("format值不能为空或者参数设置不合理！");
            }
            String[] webTimeFormat = {"YYYY", "MM", "DD", "HH", "mm", "ss"};
            String[] dbTimeFormat;
            if (dbTypeMysql.equals(QueryWrapperUtils.dbType)) {
                dbTimeFormat = new String[]{"%Y", "%m", "%d", "%H", "%i", "%S"};
            } else {
                //oralce/dm/kingbase/postgresql
                dbTimeFormat = new String[]{"YYYY", "MM", "DD", "HH24", "MI", "SS"};
            }
            for (int i = 0; i < webTimeFormat.length; i++) {
                format = format.replace(webTimeFormat[i], dbTimeFormat[i]);
            }
            if (dbTypeMysql.equals(QueryWrapperUtils.dbType)) {
                return "str_to_date({" + index + "},'" + format + "')";
            } else {
                return "to_date({" + index + "},'" + format + "')";
            }
        }
        return "{" + index + "}";
    }

    /**
     * 根据参数构建条件构造器-日期或者日期时间
     * @param qw            条件构造器
     * @param tableFilterQo 条件过滤器qo参数
     * @author zhuyf
     * @date 2021/12/4
     */
    private static <T> void queryWrapperDatePickerBuilder(QueryWrapper<T> qw, TableFilterQo tableFilterQo) {
        boolean condition = StringUtils.isNotBlank(tableFilterQo.getValue());
        Object value = convertType(tableFilterQo.getValue(), tableFilterQo.getValueType());
        switch (tableFilterQo.getOperationType()) {
            case eq: {
                qw.apply(condition, tableFilterQo.getKey() + "=" + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), value);
                break;
            }
            case ne: {
                qw.apply(condition, tableFilterQo.getKey() + "<>" + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), value);
                break;
            }
            case gt: {
                qw.apply(condition, tableFilterQo.getKey() + ">" + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), value);
                break;
            }
            case ge: {
                qw.apply(condition, tableFilterQo.getKey() + ">=" + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), value);
                break;
            }
            case lt: {
                qw.apply(condition, tableFilterQo.getKey() + "<" + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), value);
                break;
            }
            case le: {
                qw.apply(condition, tableFilterQo.getKey() + "<=" + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), value);
                break;
            }
            case like: {
                qw.apply(condition, tableFilterQo.getKey() + " like " + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), "%" + value + "%");
                break;
            }
            case likeLeft: {
                qw.apply(condition, tableFilterQo.getKey() + " like " + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), "%" + value);
                break;
            }
            case likeRight: {
                qw.apply(condition, tableFilterQo.getKey() + " like " + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), value + "%");
                break;
            }
            case notLike: {
                qw.apply(condition, tableFilterQo.getKey() + " not like " + buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0), "%" + value + "%");
                break;
            }
            default:
                break;
        }
    }

    /**
     * 根据参数构建条件构造器-时间段
     * @param qw            条件构造器
     * @param tableFilterQo 条件过滤器qo参数
     * @author zhuyf
     * @date 2021/12/4
     */
    private static <T> void queryWrappeRangePickerBuilder(QueryWrapper<T> qw, TableFilterQo tableFilterQo) {
        boolean condition = StringUtils.isNotBlank(tableFilterQo.getValue()) && tableFilterQo.getValue().indexOf(",") > 0;
        String[] timePeriod = tableFilterQo.getValue().split(",");
        switch (tableFilterQo.getOperationType()) {
            case between: {
                qw.apply(condition, tableFilterQo.getKey() + " between " +
                        buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0) + " and " +
                        buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 1), timePeriod[0], timePeriod[1]);
                break;
            }
            case notBetween: {
                qw.apply(condition, tableFilterQo.getKey() + " not between " +
                        buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 0) + " and " +
                        buildDateByDbType(tableFilterQo.getFormat(), tableFilterQo.isDateFormat(), 1), timePeriod[0], timePeriod[1]);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 根据参数构建条件构造器-多个数值-不包含日期
     * @param qw            条件构造器
     * @param tableFilterQo 条件过滤器qo参数
     * @author zhuyf
     * @date 2021/12/4
     */
    private static <T> void queryWrapperMultNoneDateBuilder(QueryWrapper<T> qw, TableFilterQo tableFilterQo) {
        List<Object> valueList = convertType(tableFilterQo.getData(), tableFilterQo.getValueType());
        switch (tableFilterQo.getOperationType()) {
            case in: {
                qw.in(tableFilterQo.getData() != null && tableFilterQo.getData().size() > 0, tableFilterQo.getKey(), valueList);
                break;
            }
            case notIn: {
                qw.notIn(tableFilterQo.getData() != null && tableFilterQo.getData().size() > 0, tableFilterQo.getKey(), valueList);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 根据参数构建条件构造器-单个数值-不包含日期
     * @param qw            条件构造器
     * @param tableFilterQo 条件过滤器qo参数
     * @author zhuyf
     * @date 2021/12/4
     */
    private static <T> void queryWrapperSingleNoneDateBuilder(QueryWrapper<T> qw, TableFilterQo tableFilterQo) {
        boolean condition = StringUtils.isNotBlank(tableFilterQo.getValue());
        Object value = convertType(tableFilterQo.getValue(), tableFilterQo.getValueType());
        switch (tableFilterQo.getOperationType()) {
            case eq: {
                qw.eq(condition, tableFilterQo.getKey(), value);
                break;
            }
            case ne: {
                qw.ne(condition, tableFilterQo.getKey(), value);
                break;
            }
            case ge: {
                qw.ge(condition, tableFilterQo.getKey(), value);
                break;
            }
            case gt: {
                qw.gt(condition, tableFilterQo.getKey(), value);
                break;
            }
            case le: {
                qw.le(condition, tableFilterQo.getKey(), value);
                break;
            }
            case lt: {
                qw.lt(condition, tableFilterQo.getKey(), value);
                break;
            }
            case like: {
                qw.like(condition, tableFilterQo.getKey(), value);
                break;
            }
            case notLike: {
                qw.notLike(condition, tableFilterQo.getKey(), value);
                break;
            }
            case likeLeft: {
                qw.likeLeft(condition, tableFilterQo.getKey(), value);
                break;
            }
            case likeRight: {
                qw.likeRight(condition, tableFilterQo.getKey(), value);
                break;
            }
            case notNull: {
                if (condition) {
                    String one = "1";
                    String trueStr = "true";
                    if (one.equals(value.toString()) || trueStr.equals(value.toString())) {
                        qw.isNotNull(tableFilterQo.getKey());
                    } else {
                        qw.isNull(tableFilterQo.getKey());
                    }
                }
                break;
            }
            default:
                break;
        }
    }


    /**
     * 类型转换
     * @author zhuyf
     * @date 2022/11/9
     */
    private static Object convertType(String value, JavaBasicType valueType) {
        if (StringUtils.isNotBlank(value) && valueType != null) {
            switch (valueType) {
                case Integer: {
                    return Integer.valueOf(value);
                }
                case Boolean: {
                    return Boolean.valueOf(value);
                }
                case Byte: {
                    return Byte.valueOf(value);
                }
                case Short: {
                    return Short.valueOf(value);
                }
                case Long: {
                    return Long.valueOf(value);
                }
                case Float: {
                    return Float.valueOf(value);
                }
                case Double: {
                    return Double.valueOf(value);
                }
                default: {
                    return value;
                }
            }
        }
        return value;
    }

    /**
     * 类型转换
     * @author zhuyf
     * @date 2022/11/9
     */
    private static List<Object> convertType(List<String> valueList, JavaBasicType valueType) {
        List<Object> dataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(valueList)) {
            if (valueType != null) {
                for (String value : valueList) {
                    if (StringUtils.isNotBlank(value)) {
                        dataList.add(convertType(value, valueType));
                    }
                }
            } else {
                dataList.addAll(valueList);
            }
        }
        return dataList;
    }
}
