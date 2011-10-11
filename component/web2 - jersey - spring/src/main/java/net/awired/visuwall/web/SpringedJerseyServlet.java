package net.awired.visuwall.web;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.spring.container.SpringComponentProviderFactory;

/**
 * A servlet or filter for deploying root resource classes with Spring
 * integration.
 * <p>
 * This class extends {@link ServletContainer} and initiates the {@link WebApplication} with a Spring-based
 * {@link IoCComponentProviderFactory}, {@link SpringComponentProviderFactory}, such that instances of resource and
 * provider classes declared and managed by Spring can be obtained.
 * <p>
 * Classes of Spring beans declared using XML-based configuration or auto-wire-based confguration will be automatically
 * registered if such classes are root resource classes or provider classes. It is not necessary to provide
 * initialization parameters for declaring classes in the web.xml unless a mixture of Spring-managed and Jersey-managed
 * classes is required.
 * <p>
 * The servlet supports configuration of child applicationContexts, see {@link #CONTEXT_CONFIG_LOCATION}.
 * <p>
 * 
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @author Paul.Sandoz@Sun.Com
 * @author recht@java.net
 */
public class SpringedJerseyServlet extends ServletContainer {

    private static final long serialVersionUID = 5686655395749077671L;

    private static final Logger LOGGER = Logger.getLogger(SpringedJerseyServlet.class.getName());

    public SpringedJerseyServlet(Application application) {
        super(application);
    }

    /**
     * The context configuration location initialization parameter for declaring
     * that a child context should be used for bean definitions. This feature
     * can be used when configuration multiple Jersey/Spring servlets that
     * contain different SPring-managed resources.
     * <p>
     * The parameter name is the String "contextConfigLocation".
     * <p>
     * A parameter value is a reference to one more spring configuration files separated by commas, semicolons or
     * whitespace.
     * 
     * distinct locations separated by commas, semicolons or whitespace
     * <p>
     * If this parameter is absent then the default application context configuration is utilized.
     */
    public static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig)
            throws ServletException {
        return new DefaultResourceConfig();
    }

    @Override
    protected void initiate(ResourceConfig rc, WebApplication wa) {
        try {
            wa.initiate(rc, new SpringComponentProviderFactory(rc, getContext()));
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Exception occurred when intialization", e);
            throw e;
        }
    }

    /**
     * Get the application context.
     * <p>
     * If the initialization parameter {@link #CONTEXT_CONFIG_LOCATION} is present then this method will invoke
     * {@link #getChildContext(java.lang.String) } otherwise this method will invoke {@link #getDefaultContext() }.
     * 
     * @return the application context.
     */
    protected ConfigurableApplicationContext getContext() {
        final String contextConfigLocation = getWebConfig().getInitParameter(CONTEXT_CONFIG_LOCATION);
        if (contextConfigLocation == null) {
            LOGGER.info("Using default applicationContext");
            return getDefaultContext();
        } else {
            LOGGER.info("Creating new child context from " + contextConfigLocation);
            return getChildContext(contextConfigLocation);
        }
    }

    /**
     * Get the default application context.
     * <p>
     * The default application context will be looked up from the servlet context using
     * {@link WebApplicationContextUtils#getRequiredWebApplicationContext(javax.servlet.ServletContext) }.
     * 
     * @return the default application context.
     */
    protected ConfigurableApplicationContext getDefaultContext() {
        final WebApplicationContext springWebContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        final ConfigurableApplicationContext springContext = (ConfigurableApplicationContext) springWebContext;
        return springContext;
    }

    /**
     * Get the child application context.
     * <p>
     * The child application context is created as a child of the default application context obtained from
     * {@link #getDefaultContext() }.
     * 
     * @param contextConfigLocation
     *            the location of the child application
     *            context.
     * @return the child application context.
     */
    protected ConfigurableApplicationContext getChildContext(String contextConfigLocation) {
        final ConfigurableWebApplicationContext ctx = new XmlWebApplicationContext();
        ctx.setParent(getDefaultContext());
        ctx.setServletContext(getServletContext());
        ctx.setConfigLocation(contextConfigLocation);

        ctx.refresh();
        return ctx;
    }
}