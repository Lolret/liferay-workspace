package spring.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import spring.test.dto.User;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Akadmon
 */
@Controller
@Slf4j
@RequestMapping("VIEW")
public class UserController {

    @Autowired
    private LocalValidatorFactoryBean _localValidatorFactoryBean;

    @Autowired
    private MessageSource _messageSource;

    @ModelAttribute("user")
    public User getUserModelAttribute() {
        return new User();
    }

    @RenderMapping
    public String prepareView(ModelMap modelMap, RenderResponse renderResponse) {

        modelMap.put("mainFormActionURL", renderResponse.createActionURL());
        modelMap.put("namespace", renderResponse.getNamespace());
        putTime(modelMap);

        return "user";
    }

    @RenderMapping(params = "javax.portlet.action=success")
    public String showGreeting(ModelMap modelMap) {

        putTime(modelMap);

        return "greeting";
    }

    private void putTime(ModelMap modelMap) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm, EEEE, d MMMM yyyy G");
        Calendar todayCalendar = Calendar.getInstance();

        modelMap.put("todaysDate", dateFormat.format(todayCalendar.getTime()));
    }

    @ActionMapping
    public void submitApplicant(
            @ModelAttribute("user") User user, BindingResult bindingResult,
            ModelMap modelMap, Locale locale, ActionResponse actionResponse,
            SessionStatus sessionStatus) {

        _localValidatorFactoryBean.validate(user, bindingResult);

        if (!bindingResult.hasErrors()) {
            if (log.isDebugEnabled()) {
                log.debug("firstName=" + user.getFirstName());
                log.debug("lastName=" + user.getLastName());
            }

            actionResponse.setRenderParameter("javax.portlet.action", "success");

            sessionStatus.setComplete();
        } else {
            bindingResult.addError(
                    new ObjectError(
                            "user",
                            _messageSource.getMessage(
                                    "please-correct-the-following-errors", null, locale)));
        }
    }

}