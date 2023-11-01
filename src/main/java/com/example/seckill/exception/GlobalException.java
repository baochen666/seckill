package com.example.seckill.exception;

import com.example.seckill.result.CodeMsg;

public class GlobalException extends RuntimeException{
    private static final long servialVersionUID = 1L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.getMsg());
        this.codeMsg=codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }


}
