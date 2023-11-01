package com.example.seckill.validation;
import com.example.seckill.util.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
public class IsMobileValidator implements ConstraintValidator<IsMobile,String>{

    private boolean required=false;

    @Override
    public void initialize(IsMobile isMobile) {
       required=isMobile.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            return ValidatorUtil.isValidPhoneNumbe(value);
        }else {
            if (StringUtils.isEmpty(value)) {
                return true;
            } else {
                return ValidatorUtil.isValidPhoneNumbe(value);
            }
        }
    }
}
