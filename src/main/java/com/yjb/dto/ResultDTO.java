package com.yjb.dto;

import com.yjb.exception.CustomizeErrorCode;
import com.yjb.exception.CustomizeException;
import lombok.Data;

@Data
public class ResultDTO<T> {
    private Integer code;
    private String message;
    private T data;

    public static ResultDTO errorOf(Integer code, String message) {
        ResultDTO resultDTO = new ResultDTO<>();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {
        ResultDTO resultDTO = new ResultDTO<>();
        resultDTO.setCode(errorCode.getCode());
        resultDTO.setMessage(errorCode.getMessage());
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeException e) {
        ResultDTO resultDTO = new ResultDTO<>();
        resultDTO.setCode(e.getCode());
        resultDTO.setMessage(e.getMessage());
        return resultDTO;
    }

    public static ResultDTO okOf() {
        ResultDTO resultDTO = new ResultDTO<>();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }

    public static <T> ResultDTO okOf(T data) {
        ResultDTO resultDTO = new ResultDTO<>();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(data);
        return resultDTO;
    }
}
