package com.btc.service.impl;

import com.btc.domain.model.custom.SiteModel;
import com.btc.domain.model.custom.audit.DeleteAuditLogModel;
import com.btc.domain.model.custom.extend.ItemModel;
import com.btc.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc.persistence.dao.ModelDao;
import com.btc.service.ModelService;
import com.btc.service.constant.ServiceConstant;
import com.btc.service.exception.StoreRuntimeException;
import com.btc.service.exception.interceptor.InterceptorException;
import com.btc.service.exception.model.ModelRemoveException;
import com.btc.service.exception.model.ModelSaveException;
import com.btc.service.interceptor.Interceptor;
import com.btc.service.interceptor.registry.InterceptorRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import constant.PackageConstant;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import util.StoreClassUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    protected final ModelDao modelDao;
    protected final InterceptorRegistry interceptorRegistry;
    protected final EntityManager entityManager;
    protected static final String SAVE_ERROR_MSG = "Error while saving model.Model : {} Error message : {}";

    @Value("${delete.item.audit.fields}")
    protected List<String> deleteItemAuditFields;

    @Value("${delete.item.audit.domains}")
    protected List<String> deleteItemAuditDomains;


    @Override
    public <T extends ItemModel> T save(T t) {
        try {
            runBeforeInterceptors(t, Transaction.SAVE);
            var model = modelDao.save(t);
            //set modified fields
            model.setModifiedAttributes(t.getModifiedAttributes());
            runAfterInterceptors(model, Transaction.SAVE);
            return model;

        } catch (Exception e) {
            var errorMessage = getValidatorMessageWhenSave(e);
            log.error(SAVE_ERROR_MSG, StoreClassUtils.getSimpleName(t), errorMessage);
            if (e instanceof StoreRuntimeException) {
                throw ((StoreRuntimeException) e);
            }
            throw new ModelSaveException(errorMessage);
        }

    }

    @Override
    public <T extends ItemModel> Iterable<T> saveAll(Iterable<T> t) {
        try {
            for (T item : t) {
                runBeforeInterceptors(item, Transaction.SAVE);
            }
            var models = modelDao.saveAll(t);
            //set modified fields
            models.stream().forEach(m -> m.setModifiedAttributes(t.iterator().next().getModifiedAttributes()));

            for (T item : models) {
                runAfterInterceptors(item, Transaction.SAVE);
            }
            return models;
        } catch (Exception e) {
            var errorMessage = getValidatorMessageWhenSave(e);
            log.error(SAVE_ERROR_MSG, StoreClassUtils.getSimpleName(t), errorMessage);
            if (e instanceof StoreRuntimeException) {
                throw ((StoreRuntimeException) e);
            }
            throw new ModelSaveException(errorMessage);
        }

    }

    @Override
    public <T extends ItemModel> Iterable<T> saveAll(T... t) {

        try {
            for (T item : t) {
                runBeforeInterceptors(item, Transaction.SAVE);
            }
            var items = Arrays.asList(t);
            var models = modelDao.saveAll(Arrays.asList(t));
            //set modified fields
            IntStream.range(0, models.size()).forEach(i ->
                    models.get(i).setModifiedAttributes(items.get(i).getModifiedAttributes())
            );
            for (T item : models) {
                runAfterInterceptors(item, Transaction.SAVE);
            }
            return models;


        } catch (Exception e) {
            var errorMessage = getValidatorMessageWhenSave(e);
            log.error(SAVE_ERROR_MSG, StoreClassUtils.getSimpleName(t), errorMessage);
            if (e instanceof StoreRuntimeException) {
                throw ((StoreRuntimeException) e);
            }
            throw new ModelSaveException(errorMessage);
        }
    }

    @Override
    public <T extends ItemModel> void remove(T t) {
        try {
            if (Objects.isNull(t)) {
                throw new ModelRemoveException("Model Remove Exception...Item is null");
            }
            runBeforeInterceptors(t, Transaction.REMOVE);
            modelDao.delete(t);
            runAfterInterceptors(t, Transaction.REMOVE);
            createAuditLogs(Arrays.asList(t));
        } catch (Exception e) {
            var errorMessage = getValidatorMessageWhenSave(e);
            log.error("Error while removing model.Error message : {}", errorMessage);
            if (e instanceof StoreRuntimeException) {
                throw ((StoreRuntimeException) e);
            }
            throw new ModelRemoveException(errorMessage);
        }

    }

    @Override
    public <T extends ItemModel> void removeAll(Iterable<T> t) {
        try {
            for (T item : t) {
                runBeforeInterceptors(item, Transaction.REMOVE);
            }
            modelDao.deleteAll(t);

            for (T item : t) {
                runAfterInterceptors(item, Transaction.REMOVE);
            }
            createAuditLogs(ImmutableList.copyOf(t));
        } catch (Exception e) {

            var errorMessage = getValidatorMessageWhenSave(e);
            log.error("Error while removing all model.Error message : {}", errorMessage);
            if (e instanceof StoreRuntimeException) {
                throw ((StoreRuntimeException) e);
            }
            throw new ModelRemoveException(errorMessage);
        }
    }

    @Override
    public <T extends ItemModel> void removeAll(T... t) {
        try {
            for (T item : t) {
                runBeforeInterceptors(item, Transaction.REMOVE);
            }
            var list = Arrays.asList(t);
            modelDao.deleteAll(list);

            for (T item : t) {
                runAfterInterceptors(item, Transaction.REMOVE);
            }

            //audit logs...
            createAuditLogs(list);
        } catch (Exception e) {
            var errorMessage = getValidatorMessageWhenSave(e);
            log.error("Error while removing all model.Error message : {}", errorMessage);
            if (e instanceof StoreRuntimeException) {
                throw ((StoreRuntimeException) e);
            }
            throw new ModelSaveException(errorMessage);
        }
    }

    protected <T extends ItemModel> void createAuditLogs(List<T> list) {

        if (CollectionUtils.isNotEmpty(list)) {
            var items = new ArrayList<DeleteAuditLogModel>();
            list.forEach(m -> {

                var isItemAudit = deleteItemAuditDomains.stream().anyMatch(p -> StringUtils.equals(StoreClassUtils.generateClassName(p,
                        ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX), m.getClass().getSimpleName()));
                if (BooleanUtils.isTrue(isItemAudit)) {
                    var deleteAuditLogModel = this.create(DeleteAuditLogModel.class);
                    deleteAuditLogModel.setCode(UUID.randomUUID().toString());
                    deleteAuditLogModel.setItemType(ClassUtils.getSimpleName(m));
                    deleteAuditLogModel.setSite(getSiteForItem(m));
                    deleteAuditLogModel.setData(convertItemToJsonForDelete(m));
                    items.add(deleteAuditLogModel);
                }

            });
            if (CollectionUtils.isNotEmpty(items)) {
                this.saveAll(items);
            }

        }
    }

    protected <T extends ItemModel> SiteModel getSiteForItem(T t) {
        try {
            return (SiteModel) PropertyUtils.getProperty(t, StoreSiteBasedItemModel.Fields.site);
        } catch (Exception e) {
            return null;
        }

    }

    protected <T extends ItemModel> String convertItemToJsonForDelete(T t) {
        var data = new StringBuilder();
        var mapper = new ObjectMapper();
        var node = mapper.createObjectNode();
        data.append("[");
        deleteItemAuditFields.forEach(v -> {
            try {
                var value = PropertyUtils.getProperty(t, v);
                node.put(v, String.valueOf(value));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        data.append(node.toPrettyString());
        data.append("]");
        return data.toString();
    }


    @Override
    public <T extends ItemModel> T create(Class<T> tClass) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new ClassCastException();
        }
    }

    @Override
    public <T extends ItemModel> T refresh(T t) {
        entityManager.refresh(t);
        return t;
    }

    protected <T extends ItemModel> void runBeforeInterceptors(T t, Transaction transaction) throws InterceptorException {
        var interceptor = StringUtils.EMPTY;
        try {
            if (Transaction.REMOVE.equals(transaction)) {
                var beforeRemoveInterceptors = interceptorRegistry.getBeforeRemoveInterceptor().entrySet().stream()
                        .filter(p -> StringUtils.equals(p.getValue().getTypeName(), getClassTypeName(t)))
                        .map(Map.Entry::getKey).toList();

                for (Interceptor<T> p : beforeRemoveInterceptors) {
                    interceptor = StoreClassUtils.getSimpleName(p);
                    p.invoke(t);
                }
            } else if (Transaction.SAVE.equals(transaction)) {
                if (Objects.isNull(t.getId()) || t.getId() == 0) {
                    t.setNewTransaction(Boolean.TRUE);
                }
                var beforeSaveInterceptors = interceptorRegistry.getBeforeSaveInterceptor().entrySet().stream()
                        .filter(p -> StringUtils.equals(p.getValue().getTypeName(), getClassTypeName(t)))
                        .map(Map.Entry::getKey).toList();

                for (Interceptor<T> p : beforeSaveInterceptors) {
                    interceptor = StoreClassUtils.getSimpleName(p);
                    p.invoke(t);
                }
            }
        } catch (final Exception e) {
            if (e instanceof InterceptorException) {
                throw new InterceptorException("Interceptor : " + interceptor
                        + StringUtils.SPACE + ExceptionUtils.getMessage(e), ((InterceptorException) e).getMessageKey(), ((InterceptorException) e).getArgs());
            } else {
                throw new InterceptorException("Interceptor : " + interceptor
                        + StringUtils.SPACE + ExceptionUtils.getMessage(e));
            }

        }

    }

    protected <T extends ItemModel> void runAfterInterceptors(T t, Transaction transaction) throws InterceptorException {
        var interceptor = StringUtils.EMPTY;
        try {
            if (Transaction.REMOVE.equals(transaction)) {
                var afterRemoveInterceptors = interceptorRegistry.getAfterRemoveInterceptor().entrySet().stream()
                        .filter(p -> StringUtils.equals(p.getValue().getTypeName(), getClassTypeName(t)))
                        .map(Map.Entry::getKey).collect(Collectors.toList());

                for (Interceptor<T> p : afterRemoveInterceptors) {
                    interceptor = StoreClassUtils.getSimpleName(p);
                    p.invoke(t);
                }
            } else if (Transaction.SAVE.equals(transaction)) {
                var afterSaveInterceptors = interceptorRegistry.getAfterSaveInterceptor().entrySet().stream()
                        .filter(p -> StringUtils.equals(p.getValue().getTypeName(), getClassTypeName(t)))
                        .map(Map.Entry::getKey).collect(Collectors.toList());

                for (Interceptor<T> p : afterSaveInterceptors) {
                    interceptor = StoreClassUtils.getSimpleName(p);
                    p.invoke(t);
                }
            }
        } catch (final Exception e) {
            if (e instanceof InterceptorException) {
                throw new InterceptorException("Interceptor : " + interceptor
                        + StringUtils.SPACE + ExceptionUtils.getMessage(e), ((InterceptorException) e).getMessageKey(),
                        ((InterceptorException) e).getArgs());
            } else {
                throw new InterceptorException("Interceptor : " + interceptor
                        + StringUtils.SPACE + ExceptionUtils.getMessage(e));
            }
        }


    }

    protected <T extends ItemModel> String getClassTypeName(T model) {
        if (model instanceof HibernateProxy) {
            return model.getClass().getSuperclass().getTypeName();
        }

        return model.getClass().getName();
    }

    public String getValidatorMessageWhenSave(Exception e) {
        String message = ExceptionUtils.getMessage(e);
        if (e instanceof TransactionSystemException
                && ((TransactionSystemException) e).getRootCause() instanceof ConstraintViolationException) {
            ConstraintViolationException modelValidatorEx =
                    ((ConstraintViolationException) ((TransactionSystemException) e).getRootCause());
            if (Objects.nonNull(modelValidatorEx)) {
                Set<ConstraintViolation<?>> constraintViolations = modelValidatorEx.getConstraintViolations();
                message = constraintViolations.iterator().hasNext()
                        ? constraintViolations.iterator().next().getMessage() : message;
            }
        } else if (e instanceof DataIntegrityViolationException) {
            message = ExceptionUtils.getRootCauseMessage(e);

        }
        return message;
    }


    enum Transaction {
        SAVE, REMOVE
    }
}
