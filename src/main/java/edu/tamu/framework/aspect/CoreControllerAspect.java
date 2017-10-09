/* 
 * CoreControllerAspect.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.aspect;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.INVALID;
import static edu.tamu.weaver.response.ApiStatus.WARNING;
import static edu.tamu.framework.util.EntityUtility.getValueForProperty;
import static edu.tamu.framework.util.EntityUtility.queryWithClassById;
import static edu.tamu.framework.util.EntityUtility.recursivelyFindJsonIdentityReference;
import static edu.tamu.framework.util.EntityUtility.setValueForProperty;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiValidation;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.AbstractCoreUser;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.framework.model.BaseEntity;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.HttpRequest;
import edu.tamu.framework.model.ValidatingBase;
import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.HttpRequestService;
import edu.tamu.framework.service.RoleService;
import edu.tamu.framework.service.SecurityContextService;
import edu.tamu.framework.service.StompService;
import edu.tamu.framework.service.WebSocketRequestService;
import edu.tamu.framework.util.ValidationUtility;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.BusinessValidator;
import edu.tamu.framework.validation.MethodValidator;
import edu.tamu.framework.validation.ValidationResults;

/**
 * Core Controller Aspect
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Component
@Aspect
public abstract class CoreControllerAspect<U extends AbstractCoreUser> {

    @Value("${app.aspect.retries:3}")
    private int NUMBER_OF_RETRY_ATTEMPTS;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    private WebSocketRequestService<U> webSocketRequestService;

    @Autowired
    private HttpRequestService<U> httpRequestService;

    @Autowired
    private SecurityContextService<U> securityContextService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StompService stompService;

    @Autowired
    private MessageConverter messageConverter;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* *.*.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && @annotation(edu.tamu.framework.aspect.annotation.ApiValidation) && @annotation(auth)")
    public ApiResponse transactionallyPolpulateCredentialsAndAuthorize(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
        List<ApiResponse> apiresponses = new ArrayList<ApiResponse>();
        createTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus ts) {
                try {
                    apiresponses.add(authorizeAndProceed(joinPoint, auth));
                } catch (Throwable e) {
                    apiresponses.add(new ApiResponse(ERROR, "Failed to process request!"));
                    e.printStackTrace();
                }
            }
        });
        return apiresponses.get(0);
    }

    @Around("execution(* *.*.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && !@annotation(edu.tamu.framework.aspect.annotation.ApiValidation) && @annotation(auth)")
    public ApiResponse polpulateCredentialsAndAuthorize(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
        return authorizeAndProceed(joinPoint, auth);
    }

    @Around("execution(* *.*.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && @annotation(edu.tamu.framework.aspect.annotation.ApiValidation) && !@annotation(edu.tamu.framework.aspect.annotation.Auth)")
    public ApiResponse transactionallyPopulateCredentials(ProceedingJoinPoint joinPoint) throws Throwable {
        List<ApiResponse> apiresponses = new ArrayList<ApiResponse>();
        createTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus ts) {
                try {
                    apiresponses.add(proceed(joinPoint));
                } catch (Throwable e) {
                    apiresponses.add(new ApiResponse(ERROR, "Failed to process request!"));
                    e.printStackTrace();
                }
            }
        });
        return apiresponses.get(0);
    }

    @Around("execution(* *.*.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && !@annotation(edu.tamu.framework.aspect.annotation.ApiValidation) && !@annotation(edu.tamu.framework.aspect.annotation.Auth)")
    public ApiResponse populateCredentials(ProceedingJoinPoint joinPoint) throws Throwable {
        return proceed(joinPoint);
    }

    private TransactionTemplate createTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        return transactionTemplate;
    }

    private ApiResponse authorize(PreProcessObject preProcessObject, Auth auth) {
        ApiResponse apiresponse = null;
        if (roleService.valueOf(preProcessObject.shib.getRole()).ordinal() < roleService.valueOf(auth.role()).ordinal()) {
            logger.info(preProcessObject.shib.getFirstName() + " " + preProcessObject.shib.getLastName() + "(" + preProcessObject.shib.getUin() + ") attempted restricted access.");
            apiresponse = new ApiResponse(preProcessObject.requestId, ERROR, "You are not authorized for this request.");
        }
        return apiresponse;
    }

    private ApiResponse proceed(ProceedingJoinPoint joinPoint, PreProcessObject preProcessObject) throws Throwable {
        ApiResponse apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);
        if (apiresponse != null) {
            // retry endpoint if error response type
            int attempt = 0;
            while (attempt <= NUMBER_OF_RETRY_ATTEMPTS && apiresponse.getMeta().getStatus() == ERROR) {
                attempt++;
                logger.debug("Retry attempt " + attempt);
                apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);
            }
        } else {
            apiresponse = new ApiResponse(WARNING, "Endpoint returns void!");
        }
        return apiresponse;
    }

    private void broadcast(PreProcessObject preProcessObject, ApiResponse apiresponse) {
        // if using combined ApiMapping annotation send message as similar to SendToUser annotation
        if (preProcessObject.protocol == Protocol.WEBSOCKET) {
            apiresponse.getMeta().setId(preProcessObject.requestId);
            stompService.sendReliableMessage(preProcessObject.destination, preProcessObject.requestId, apiresponse);
        }
    }

    private ApiResponse authorizeAndProceed(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
        PreProcessObject preProcessObject = preProcess(joinPoint);
        ApiResponse apiresponse = null;
        if (preProcessObject.valid) {
            apiresponse = authorize(preProcessObject, auth);
            if (apiresponse == null) {
                apiresponse = proceed(joinPoint, preProcessObject);
            }
        } else {
            apiresponse = new ApiResponse(INVALID, preProcessObject.validation);
        }
        broadcast(preProcessObject, apiresponse);
        return apiresponse;
    }

    private ApiResponse proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        PreProcessObject preProcessObject = preProcess(joinPoint);
        ApiResponse apiresponse = null;
        if (preProcessObject.valid) {
            apiresponse = proceed(joinPoint, preProcessObject);
        } else {
            apiresponse = new ApiResponse(INVALID, preProcessObject.validation);
        }
        broadcast(preProcessObject, apiresponse);
        return apiresponse;
    }

    private PreProcessObject preProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        U user = null;

        Credentials credentials = null;

        Map<String, String> apiVariables = null;

        String requestId = null;

        String data = null;

        String headerData = null;

        Map<String, String[]> parameters = new HashMap<String, String[]>();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Object[] arguments = joinPoint.getArgs();

        Class<?> clazz = methodSignature.getDeclaringType();

        String[] argNames = methodSignature.getParameterNames();

        Type[] argTypes = new Type[argNames.length];

        Method method = methodSignature.getMethod();

        int i = 0;
        for (Parameter parameter : method.getParameters()) {
            argTypes[i++] = parameter.getParameterizedType();
        }

        Protocol protocol;

        String destination = "";

        Message<?> message = null;

        HttpServletRequest servletRequest = null;

        if (RequestContextHolder.getRequestAttributes() != null) {

            protocol = Protocol.HTTP;

            // determine endpoint path either from ApiMapping or RequestMapping annotation
            String path = servletContext.getContextPath();

            if (clazz.getAnnotationsByType(RequestMapping.class).length > 0) {
                path += clazz.getAnnotationsByType(RequestMapping.class)[0].value()[0];
            } else {
                path += clazz.getAnnotationsByType(ApiMapping.class)[0].value()[0];
            }

            if (method.getAnnotation(RequestMapping.class) != null) {
                path += method.getAnnotation(RequestMapping.class).value()[0];
            } else {
                path += method.getAnnotation(ApiMapping.class).value()[0];
            }

            HttpRequest<U> request = httpRequestService.getAndRemoveRequestByDestinationAndContextUin(path, securityContextService.getAuthenticatedName());

            servletRequest = request.getRequest();

            parameters = servletRequest.getParameterMap();

            logger.debug("The request: " + servletRequest);

            if (path.contains("{")) {
                apiVariables = getApiVariable(path, servletContext.getContextPath() + servletRequest.getServletPath());
            }

            credentials = request.getCredentials();

            user = (U) request.getUser();

            if (servletRequest.getMethod().equals("POST")) {
                data = StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8);
            }

            if (servletRequest.getAttribute("data") != null) {
                headerData = (String) servletRequest.getAttribute("data");
            }

        } else {

            // determine endpoint path either from ApiMapping or MessageMapping annotation
            String path = "";

            if (clazz.getAnnotationsByType(MessageMapping.class).length > 0) {
                path += clazz.getAnnotationsByType(MessageMapping.class)[0].value()[0];
            } else {
                path += clazz.getAnnotationsByType(ApiMapping.class)[0].value()[0];
            }

            if (method.getAnnotation(MessageMapping.class) != null) {
                path += method.getAnnotation(MessageMapping.class).value()[0];
                protocol = Protocol.DEFAULT;
            } else {
                path += method.getAnnotation(ApiMapping.class).value()[0];
                protocol = Protocol.WEBSOCKET;
            }

            WebSocketRequest<U> request = webSocketRequestService.getAndRemoveMessageByDestinationAndContextUin(path, securityContextService.getAuthenticatedName());

            message = request.getMessage();

            logger.debug("The message: " + message);

            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            destination = accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId();

            requestId = accessor.getNativeHeader("id").get(0);

            credentials = request.getCredentials();

            user = (U) request.getUser();

            if (path.contains("{")) {
                apiVariables = getApiVariable(path, accessor.getDestination());
            }

            data = (String) messageConverter.fromMessage(message, String.class);

            if (data == null && accessor.getNativeHeader("data") != null) {
                data = accessor.getNativeHeader("data").get(0).toString();
            }

        }

        PreProcessObject preProcessObject = new PreProcessObject(credentials, requestId, arguments, protocol, destination, true);

        int index = 0;
        for (Annotation[] annotations : method.getParameterAnnotations()) {

            String annotationString = null;

            for (Annotation annotation : annotations) {
                annotationString = annotation.toString();
                annotationString = annotationString.substring(annotationString.lastIndexOf('.') + 1, annotationString.indexOf("("));
            }

            if (annotationString != null) {
                switch (annotationString) {
                case "ApiVariable": {
                    arguments[index] = apiVariables.get(argNames[index]) != null ? objectMapper.convertValue(apiVariables.get(argNames[index]), objectMapper.constructType(argTypes[index])) : null;
                }
                    break;
                case "ApiCredentials": {
                    arguments[index] = credentials;
                }
                    break;
                case "ApiUser": {
                    arguments[index] = user;
                }
                    break;
                case "ApiData": {
                    String pData = headerData != null ? headerData : data;
                    arguments[index] = pData != null ? objectMapper.convertValue(objectMapper.readTree(pData), objectMapper.constructType(argTypes[index])) : null;
                }
                    break;
                case "ApiModel": {
                    String pData = headerData != null ? headerData : data;
                    arguments[index] = ensureCompleteModel(pData != null ? objectMapper.convertValue(objectMapper.readTree(pData), objectMapper.constructType(argTypes[index])) : null);
                }
                    break;
                case "ApiValidatedModel": {
                    String pData = headerData != null ? headerData : data;
                    arguments[index] = ensureCompleteModel(pData != null ? objectMapper.convertValue(objectMapper.readTree(pData), objectMapper.constructType(argTypes[index])) : null);
                    preProcessObject.validation = validateModel((ValidatingBase) arguments[index], method);
                }
                    break;
                case "ApiParameters": {
                    arguments[index] = parameters;
                }
                    break;
                case "ApiInputStream": {
                    arguments[index] = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
                }
                    break;
                }
            }
            index++;
        }

        ValidationUtility.aggregateValidationResults(preProcessObject.validation, validateMethod(method, arguments));

        preProcessObject.valid = preProcessObject.validation.isValid();

        return preProcessObject;
    }

    public Object ensureCompleteModel(Object model) {
        if (model != null) {

            List<String> serializedProperties = recursivelyFindJsonIdentityReference(model.getClass());

            if (serializedProperties.size() > 0) {
                List<Object> response = queryWithClassById(model.getClass(), ((BaseEntity) model).getId());
                if (response.size() > 0) {
                    Object fullModel = response.get(0);

                    serializedProperties.forEach(serializedProperty -> {
                        setValueForProperty(model, serializedProperty, getValueForProperty(fullModel, serializedProperty));
                    });
                }
            }
        }
        return model;
    }

    public <V extends ValidatingBase> ValidationResults validateModel(V model, Method method) {
        for (Annotation validationAnnotation : method.getAnnotations()) {
            if (validationAnnotation instanceof ApiValidation) {
                for (ApiValidation.Business businessAnnotation : ((ApiValidation) validationAnnotation).business()) {
                    ((BaseModelValidator) ((ValidatingBase) model).getModelValidator()).addBusinessValidator(new BusinessValidator(businessAnnotation.value(), businessAnnotation.joins(), businessAnnotation.params(), businessAnnotation.path(), businessAnnotation.restrict()));
                }
            }
        }

        return ((ValidatingBase) model).validate((ValidatingBase) model);
    }

    public ValidationResults validateMethod(Method method, Object[] args) {

        ValidationResults validationResults = new ValidationResults();

        for (Annotation validationAnnotation : method.getAnnotations()) {
            if (validationAnnotation instanceof ApiValidation) {
                for (ApiValidation.Method methodAnnotation : ((ApiValidation) validationAnnotation).method()) {
                    ValidationUtility.aggregateValidationResults(validationResults, ValidationUtility.validateMethod(new MethodValidator(methodAnnotation.value(), methodAnnotation.model(), methodAnnotation.params(), args)));
                }
            }
        }

        return validationResults;
    }

    protected Map<String, String> getApiVariable(String mapping, String path) {
        if (path.contains("/ws")) {
            mapping = "/ws" + mapping;
        }
        if (path.contains("/private/queue")) {
            mapping = "/private/queue" + mapping;
        }
        Map<String, String> valuesMap = new HashMap<String, String>();
        String[] keys = mapping.split("/");
        String[] values = path.split("/");
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].contains("{") && keys[i].contains("}")) {
                valuesMap.put(keys[i].substring(1, keys[i].length() - 1), values[i]);
            }
        }
        return valuesMap;
    }

    protected class PreProcessObject {

        Credentials shib;
        String requestId;
        Object[] arguments;
        Protocol protocol;
        String destination;

        Boolean valid;

        ValidationResults validation;

        public PreProcessObject(Credentials shib, Object[] arguments) {
            this.shib = shib;
            this.arguments = arguments;
        }

        public PreProcessObject(Credentials shib, String requestId, Object[] arguments) {
            this(shib, arguments);
            this.requestId = requestId;
        }

        public PreProcessObject(Credentials shib, String requestId, Object[] arguments, Protocol protocol) {
            this(shib, requestId, arguments);
            this.protocol = protocol;
        }

        public PreProcessObject(Credentials shib, String requestId, Object[] arguments, Protocol protocol, String destination) {
            this(shib, requestId, arguments, protocol);
            this.destination = destination;
        }

        public PreProcessObject(Credentials shib, String requestId, Object[] arguments, Protocol protocol, String destination, Boolean valid) {
            this(shib, requestId, arguments, protocol, destination);
            this.valid = valid;
            validation = new ValidationResults();
        }

    }

    private enum Protocol {
        WEBSOCKET, HTTP, DEFAULT
    }

}
