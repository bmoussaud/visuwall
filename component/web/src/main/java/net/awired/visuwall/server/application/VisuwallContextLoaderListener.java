/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.visuwall.server.application;

import javax.servlet.ServletContextEvent;
import net.awired.visuwall.core.application.common.ApplicationHelper;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class VisuwallContextLoaderListener extends ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(confFile.getBytes("UTF-8"));
        //
        //        // assume SLF4J is bound to logback in the current environment
        //        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        //
        //        try {
        //            JoranConfigurator configurator = new JoranConfigurator();
        //            configurator.setContext(lc);
        //            // the context was probably already configured by default configuration rules
        //            lc.reset();
        //            configurator.doConfigure(byteArrayInputStream);
        //        } catch (JoranException je) {
        //            je.printStackTrace();
        //        }
        //        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Level loglvl = ApplicationHelper.findLogLvl();
        if (loglvl != null) {
            root.setLevel(loglvl);
        }
        super.contextInitialized(event);
    }
}
