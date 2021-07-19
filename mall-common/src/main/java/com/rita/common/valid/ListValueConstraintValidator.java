package com.rita.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Rita
 * @Date:6/30/2021 6:30 PM
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer > {

    private Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {

        //从这个注解中拿到vals,@ListValue(vals = {0,1})

        int[] vals = constraintAnnotation.vals();
        if(vals.length != 0 || vals != null){
            for (int val : vals) {
                set.add(val);
            }
        }

    }

    //是否校验成功
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext context) {
        //integer是entity中private Integer showStatus传进来的值,需要校验的
        return set.contains(integer);
    }
}
