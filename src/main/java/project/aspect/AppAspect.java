package project.aspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AppAspect {
    @AfterReturning("execution(* project.service.impl.*.*(..))")
    public void loggingAfterCallMethod(JoinPoint joinPoint){
        try {
            String emailUser = SecurityContextHolder.getContext().getAuthentication().getName();

            Object[] args = joinPoint.getArgs();

            log.info("User : {} thực hiện chức năng : {}", emailUser, joinPoint.getSignature().getName());

        } catch (Exception e) {
            log.error("Lỗi xảy ra khi ghi log AOP: {}", e.getMessage());
        }
    }
}
