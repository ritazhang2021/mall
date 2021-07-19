package com.rita.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**desk
 * @Author: Rita
 * @Date:6/30/2021 5:55 PM
 */
//必须要的元注解
@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class})//指定校验器
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})//可以在哪些类下用
@Retention(RetentionPolicy.RUNTIME)//可以在runtime时获取
public @interface ListValue {
    //必须要有的三个属性，符合JSR303

    //从properties文件中获取
    String message() default "{com.rita.common.valid.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] vals() default {};
}
