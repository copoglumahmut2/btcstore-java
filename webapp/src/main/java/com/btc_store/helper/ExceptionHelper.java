package com.btc_store.helper;

import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.service.ParameterService;
import com.btc_store.service.SiteService;
import com.btc_store.service.exception.StoreException;
import com.btc_store.service.exception.StoreRuntimeException;
import com.btc_store.service.exception.model.ModelNotFoundException;
import com.btc_store.service.exception.model.ModelReadException;
import com.btc_store.service.exception.model.ModelRemoveException;
import com.btc_store.service.exception.model.ModelSaveException;
import constant.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import util.Messages;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionHelper {
    protected static String EXCEPTION_MESSAGE_DETAIL_VISIBLE = "exception.message.detail.visible";

    protected final MessageSource messageSource;
    protected final Environment environment;
    protected final ParameterService parameterService;
    protected final SiteService siteService;
    @ExceptionHandler(value = {StoreRuntimeException.class})
    public ResponseEntity<ServiceResponseData> handleBambooRuntimeException(StoreRuntimeException ex) {
        var uid = UUID.randomUUID().toString();
        log.error("******************** {} ********************", uid);
        log.error(ExceptionUtils.getMessage(ex));
        return new ResponseEntity<>(fillServiceResponseData(ex, uid), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ServiceResponseData> handleIllegalArgumentException(IllegalArgumentException ex) {
        var uid = UUID.randomUUID().toString();
        log.error("******************** {} ********************", uid);
        log.error(String.join("Illegal Argument Exception: ", ExceptionUtils.getMessage(ex)));
        return new ResponseEntity<>(fillServiceResponseData(ex, uid), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {TransactionSystemException.class})
    public ResponseEntity<ServiceResponseData> handleTransactionSystemException(TransactionSystemException ex) {
        var uid = UUID.randomUUID().toString();
        log.error("******************** {} ********************", uid);
        log.error(String.join("TransactionSystem Exception: ", ExceptionUtils.getMessage(ex)));
        return new ResponseEntity<>(fillServiceResponseData(ex, uid), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class, DataAccessException.class})
    public ResponseEntity<ServiceResponseData> handleTransactionSystemException(DataAccessException ex) {
        var uid = UUID.randomUUID().toString();
        log.error("******************** {} ********************", uid);
        log.error(String.join("ObjectOptimisticLockingFailureException Exception: ", ExceptionUtils.getMessage(ex)));
        return new ResponseEntity<>(fillServiceResponseData(ex, uid), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ServiceResponseData> handleAccessDeniedException(HttpServletRequest request,
                                                                           AccessDeniedException ex) {
        var uid = UUID.randomUUID().toString();
        log.error("******************** {} ********************", uid);
        log.error(String.join("Access Denied Exception: ", ExceptionUtils.getMessage(ex)));
        var modelNonFormattedName = request.getRequestURI().split("/")[4];
        var modelLength = request.getRequestURI().split("/")[4].length();
        var modelFormattedName = StringUtils.capitalize(modelNonFormattedName.substring(0, modelLength - 1));
        var roleName = modelFormattedName + "Model_Read";
        log.error("------------------------");
        log.error("ADD ROLE: " + roleName);
        log.error("------------------------");
        return new ResponseEntity<>(fillServiceResponseData(ex, uid), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {ModelReadException.class})
    public ResponseEntity<ServiceResponseData> handleModelReadException(ModelReadException ex) {
        var uid = UUID.randomUUID().toString();
        log.error("******************** {} ********************", uid);
        log.error(String.join("Model Read Exception: ", ExceptionUtils.getMessage(ex)));
        var exceptionBody = fillServiceResponseData(ex, uid);
        exceptionBody.setStatus(ProcessStatus.UNAUTHORIZED);
        return new ResponseEntity<>(exceptionBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ServiceResponseData> handleException(Exception ex) {
        var uid = UUID.randomUUID().toString();
        log.error("******************** {} ********************", uid);
        log.error(String.join("Exception: ", ExceptionUtils.getMessage(ex)));
        return new ResponseEntity<>(fillServiceResponseData(ex, uid), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ServiceResponseData fillServiceResponseData(Exception ex, String uuid) {
        var serviceResponseData = new ServiceResponseData();

        serviceResponseData.setErrorLogUid(uuid);
        serviceResponseData.setNode(environment.getProperty("server.node", StringUtils.EMPTY));

        serviceResponseData.setStatus(ProcessStatus.ERROR);

        if (ex instanceof StoreRuntimeException && StringUtils.isNotEmpty(((StoreRuntimeException) ex).getMessageKey())) {
            serviceResponseData.setErrorMessage(messageSource.getMessage(((StoreRuntimeException) ex).getMessageKey(), ((StoreRuntimeException) ex).getArgs(), Messages.getMessagesLocale()));

        } else if (ex instanceof StoreException && StringUtils.isNotEmpty(((StoreException) ex).getMessageKey())) {
            serviceResponseData.setErrorMessage(messageSource.getMessage(((StoreException) ex).getMessageKey(), ((StoreException) ex).getArgs(), Messages.getMessagesLocale()));
        } else if (ex instanceof TransactionSystemException && ExceptionUtils.getRootCause(ex) instanceof ConstraintViolationException) {
            ConstraintViolationException modelValidatorEx =
                    ((ConstraintViolationException) ((TransactionSystemException) ex).getRootCause());
            Set<ConstraintViolation<?>> constraintViolations = modelValidatorEx.getConstraintViolations();
            if (CollectionUtils.isNotEmpty(constraintViolations)) {
                var localeMessage = messageSource.getMessage(StringUtils.substringBetween(constraintViolations.iterator().next().getMessage(), "{", "}"), null, Messages.getMessagesLocale());
                serviceResponseData.setErrorMessage(localeMessage);
            } else {
                serviceResponseData.setErrorMessage(StringUtils.defaultString(ex.getMessage()));
            }
        } else if (ex instanceof DataIntegrityViolationException) {
            serviceResponseData.setErrorMessage(messageSource.getMessage(MessageConstant.DATA_INTEGRATION_VIOLATION_EXCEPTION,
                    null, Messages.getMessagesLocale()));

        } else if (ex instanceof DataAccessResourceFailureException) {
            serviceResponseData.setErrorMessage(messageSource.getMessage(MessageConstant.DATA_ACCESS_RESOURCE_EXCEPTION,
                    null, Messages.getMessagesLocale()));
        } else if (ex instanceof ModelSaveException) {
            serviceResponseData.setErrorMessage(messageSource.getMessage(MessageConstant.MODEL_SAVE_EXCEPTION_MSG,
                    null, Messages.getMessagesLocale()));
        } else if (ex instanceof ModelRemoveException) {
            serviceResponseData.setErrorMessage(messageSource.getMessage(MessageConstant.MODEL_REMOVE_EXCEPTION_MSG,
                    null, Messages.getMessagesLocale()));
        } else if (ex instanceof AccessDeniedException) {
            serviceResponseData.setStatus(ProcessStatus.UNAUTHORIZED);
            serviceResponseData.setErrorMessage(messageSource.getMessage("service.access.denied.exception",
                    null, Messages.getMessagesLocale()));
        } else {
            serviceResponseData.setErrorMessage(StringUtils.defaultString(ex.getMessage()));
        }


        try{
            var exceptionDetailMessageVisible = parameterService.getParameterModel(EXCEPTION_MESSAGE_DETAIL_VISIBLE, siteService.getCurrentSite());
            var visible = Objects.nonNull(exceptionDetailMessageVisible) ? Boolean.valueOf(exceptionDetailMessageVisible.getValue()) : false;
            if(visible){
                serviceResponseData.setErrorMessageDetail(Arrays.asList(ExceptionUtils.getStackFrames(ex))
                        .stream().limit(50).collect(Collectors.joining()));
            }
        }
        catch (ModelNotFoundException e){
            serviceResponseData.setErrorMessageDetail(Arrays.asList(ExceptionUtils.getStackFrames(ex))
                    .stream().limit(50).collect(Collectors.joining()));
        }






        return serviceResponseData;
    }


}
