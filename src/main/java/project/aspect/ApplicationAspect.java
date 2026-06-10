package project.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ApplicationAspect {
    @AfterReturning("execution(* project.service.impl.ApplicationServiceImpl.applyJob(..))")
    public void loggingBeforeCallMethod(JoinPoint joinPoint){
        try {
            String candidateEmail = SecurityContextHolder.getContext().getAuthentication().getName();

            Object[] args = joinPoint.getArgs();
            Long jobId = null;

            if (args != null && args.length > 0 && args[0] instanceof Long) {
                jobId = (Long) args[0];
            }

            log.info("Candidate ID: {} applied for Job ID: {}", candidateEmail, jobId);

        } catch (Exception e) {
            log.error("Lỗi xảy ra khi ghi log AOP: {}", e.getMessage());
        }
    }
}
