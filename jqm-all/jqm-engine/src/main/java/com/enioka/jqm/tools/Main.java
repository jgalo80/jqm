/**
 * Copyright © 2013 enioka. All rights reserved
 * Authors: Marc-Antoine GOUILLART (marc-antoine.gouillart@enioka.com)
 *          Pierre COPPEE (pierre.coppee@enioka.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.enioka.jqm.tools;

import javax.persistence.EntityManager;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.enioka.jqm.api.JobInstance;
import com.enioka.jqm.api.JqmClientFactory;
import com.enioka.jqm.jpamodel.RRole;
import com.enioka.jqm.jpamodel.RUser;

/**
 * Starter class & parameter parsing
 * 
 */
public class Main
{
    private static Logger jqmlogger = Logger.getLogger(Main.class);
    private static JqmEngine engine;

    private Main()
    {
        // Static class
    }

    /**
     * Windows service entry point for service start
     * 
     * @param args
     */
    static void start(String[] args)
    {
        jqmlogger.info("Service start");
        main(args);
    }

    /**
     * Windows service entry point for service stop
     * 
     * @param args
     */
    static void stop(String[] args)
    {
        jqmlogger.info("Service stop");
        engine.stop();
    }

    /**
     * Startup method for the packaged JAR
     * 
     * @param args
     *            0 is node name
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args)
    {
        Helpers.setLogFileName("cli");
        Option o00 = OptionBuilder.withArgName("nodeName").hasArg().withDescription("name of the JQM node to start").isRequired()
                .create("startnode");
        Option o01 = OptionBuilder.withDescription("display help").withLongOpt("help").create("h");
        Option o11 = OptionBuilder.withArgName("applicationname").hasArg().withDescription("name of the application to launch")
                .isRequired().create("enqueue");
        Option o21 = OptionBuilder.withArgName("xmlpath").hasArg().withDescription("path of the XML configuration file to import")
                .isRequired().create("importjobdef");
        Option o31 = OptionBuilder.withArgName("xmlpath").hasArg().withDescription("export all queue definitions into an XML file")
                .isRequired().create("exportallqueues");
        OptionBuilder.withArgName("xmlpath").hasArg().withDescription("export some queue definitions into an XML file").isRequired()
                .create("exportqueuefile");
        OptionBuilder.withArgName("queues").hasArg().withDescription("queues to export").withValueSeparator(',').isRequired()
                .create("queue");
        Option o51 = OptionBuilder.withArgName("xmlpath").hasArg().withDescription("import all queue definitions from an XML file")
                .isRequired().create("importqueuefile");
        Option o61 = OptionBuilder.withArgName("nodeName").hasArg()
                .withDescription("creates a JQM node of this name, or updates it if it exists. Implies -u.").isRequired()
                .create("createnode");
        Option o71 = OptionBuilder.withDescription("display JQM engine version").withLongOpt("version").create("v");
        Option o81 = OptionBuilder.withDescription("upgrade JQM database").withLongOpt("upgrade").create("u");
        Option o91 = OptionBuilder.withArgName("jobInstanceId").hasArg().withDescription("get job instance status by ID").isRequired()
                .withLongOpt("getstatus").create("g");
        Option o101 = OptionBuilder.withArgName("password").hasArg().withDescription("creates or resets root admin account password")
                .isRequired().withLongOpt("root").create("r");
        Option o111 = OptionBuilder.withArgName("option").hasArg()
                .withDescription("ws handling. Possible values are: enable, disable, ssl, nossl, internalpki, externalapi").isRequired()
                .withLongOpt("gui").create("w");
        Option o121 = OptionBuilder.withArgName("id[,logfilepath]").hasArg().withDescription("single launch mode").isRequired()
                .withLongOpt("gui").create("s");
        Option o131 = OptionBuilder.withArgName("resourcefile").hasArg()
                .withDescription("resource parameter file to use. Default is resources.xml").withLongOpt("resources").create("p");

        Options options = new Options();
        OptionGroup og1 = new OptionGroup();
        og1.setRequired(true);
        og1.addOption(o00);
        og1.addOption(o01);
        og1.addOption(o11);
        og1.addOption(o21);
        og1.addOption(o31);
        og1.addOption(o51);
        og1.addOption(o61);
        og1.addOption(o71);
        og1.addOption(o81);
        og1.addOption(o91);
        og1.addOption(o101);
        og1.addOption(o111);
        og1.addOption(o121);
        options.addOptionGroup(og1);
        OptionGroup og2 = new OptionGroup();
        og2.addOption(o131);
        options.addOptionGroup(og2);

        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(160);

        try
        {
            // Parse arguments
            CommandLineParser parser = new BasicParser();
            CommandLine line = parser.parse(options, args);

            // Other db connection?
            if (line.getOptionValue(o131.getOpt()) != null)
            {
                jqmlogger.info("Using resource XML file " + line.getOptionValue(o131.getOpt()));
                Helpers.resourceFile = line.getOptionValue(o131.getOpt());
            }

            // Set db connection
            Helpers.registerJndiIfNeeded();

            // Enqueue
            if (line.getOptionValue(o11.getOpt()) != null)
            {
                enqueue(line.getOptionValue(o11.getOpt()));
            }
            // Get status
            if (line.getOptionValue(o91.getOpt()) != null)
            {
                getStatus(Integer.parseInt(line.getOptionValue(o91.getOpt())));
            }
            // Import XML
            else if (line.getOptionValue(o21.getOpt()) != null)
            {
                importJobDef(line.getOptionValue(o21.getOpt()));
            }
            // Start engine
            else if (line.getOptionValue(o00.getOpt()) != null)
            {
                startEngine(line.getOptionValue(o00.getOpt()));
            }
            // Export all Queues
            else if (line.getOptionValue(o31.getOpt()) != null)
            {
                exportAllQueues(line.getOptionValue(o31.getOpt()));
            }
            // Import queues
            else if (line.getOptionValue(o51.getOpt()) != null)
            {
                importQueues(line.getOptionValue(o51.getOpt()));
            }
            // Create node
            else if (line.getOptionValue(o61.getOpt()) != null)
            {
                createEngine(line.getOptionValue(o61.getOpt()));
            }
            // Upgrade
            else if (line.hasOption(o81.getOpt()))
            {
                upgrade();
            }
            // Help
            else if (line.hasOption(o01.getOpt()))
            {
                formatter.printHelp("java -jar jqm-engine.jar", options, true);
            }
            // Version
            else if (line.hasOption(o71.getOpt()))
            {
                jqmlogger.info("Engine version: " + Helpers.getMavenVersion());
            }
            // Root password
            else if (line.hasOption(o101.getOpt()))
            {
                root(line.getOptionValue(o101.getOpt()));
            }
            // Web options
            else if (line.hasOption(o111.getOpt()))
            {
                ws(line.getOptionValue(o111.getOpt()));
            }
            // Web options
            else if (line.hasOption(o121.getOpt()))
            {
                single(line.getOptionValue(o121.getOpt()));
            }
        }
        catch (ParseException exp)
        {
            jqmlogger.fatal("Could not read command line: " + exp.getMessage());
            formatter.printHelp("java -jar jqm-engine.jar", options, true);
            return;
        }
    }

    private static void enqueue(String applicationName)
    {
        jqmlogger.info("Will enqueue application named " + applicationName + " without parameter overloads");
        jqmlogger.info("Request ID is: " + JqmClientFactory.getClient().enqueue(applicationName, "CommandLineUser"));
    }

    private static void getStatus(int id)
    {
        jqmlogger.info("Status is: " + JqmClientFactory.getClient().getJob(id).getState());
    }

    private static void importJobDef(String xmlpath)
    {
        try
        {
            EntityManager em = Helpers.getNewEm();
            if (em.createQuery("SELECT q FROM Queue q WHERE q.defaultQueue = true").getResultList().size() != 1)
            {
                jqmlogger
                        .fatal("Cannot import a Job Definition when there are no queues defined. Create at least an engine first to create one");
                em.close();
                return;
            }

            String[] pathes = xmlpath.split(",");
            for (String path : pathes)
            {
                XmlJobDefParser.parse(path, em);
            }
            em.close();
        }
        catch (Exception e)
        {
            jqmlogger.fatal("Could not import file", e);
            return;
        }
    }

    private static void startEngine(String nodeName)
    {
        try
        {
            engine = new JqmEngine();
            engine.start(nodeName);
        }
        catch (Exception e)
        {
            jqmlogger.fatal("Could not launch the engine named " + nodeName
                    + ". This may be because no node with this name was declared (with command line option createnode).", e);
        }
    }

    private static void createEngine(String nodeName)
    {
        try
        {
            Helpers.allowCreateSchema();
            jqmlogger.info("Creating engine node " + nodeName);
            EntityManager em = Helpers.getNewEm();
            Helpers.updateConfiguration(em);
            Helpers.updateNodeConfiguration(nodeName, em);
            em.close();
        }
        catch (Exception e)
        {
            jqmlogger.fatal("Could not create the engine", e);
        }
    }

    private static void upgrade()
    {
        try
        {
            Helpers.allowCreateSchema();
            jqmlogger.info("Upgrading database and shared metadata");
            EntityManager em = Helpers.getNewEm();
            Helpers.updateConfiguration(em);
            em.close();
        }
        catch (Exception e)
        {
            jqmlogger.fatal("Could not upgrade", e);
        }
    }

    private static void exportAllQueues(String xmlPath)
    {
        EntityManager em = null;
        try
        {
            em = Helpers.getNewEm();
            XmlQueueExporter.export(xmlPath, em);
        }
        catch (Exception ex)
        {
            jqmlogger.fatal("Could not create the file", ex);
        }
        finally
        {
            Helpers.closeQuietly(em);
        }
    }

    private static void importQueues(String xmlPath)
    {
        EntityManager em = null;
        try
        {
            em = Helpers.getNewEm();
            XmlQueueParser.parse(xmlPath, em);
        }
        catch (Exception ex)
        {
            jqmlogger.fatal("Could not parse and import the file", ex);
        }
        finally
        {
            Helpers.closeQuietly(em);
        }
    }

    private static void root(String password)
    {
        EntityManager em = null;
        try
        {
            em = Helpers.getNewEm();
            em.getTransaction().begin();
            RRole r = Helpers.createRoleIfMissing(em, "config admin",
                    "can read and write all configuration, except security configuration", "node:*", "queue:*", "qmapping:*", "jndi:*",
                    "prm:*", "jd:*");

            RUser u = Helpers.createUserIfMissing(em, "root", "all powerfull user", r);
            u.setPassword(password);
            Helpers.encodePassword(u);

            em.getTransaction().commit();
        }
        catch (Exception ex)
        {
            jqmlogger.fatal("Could not parse and import the file", ex);
        }
        finally
        {
            Helpers.closeQuietly(em);
        }
    }

    private static void ws(String option)
    {
        if ("enable".equals(option))
        {
            EntityManager em = Helpers.getNewEm();
            Helpers.setSingleParam("disableWsApi", "false", em);
            Helpers.setSingleParam("enableWsApiSsl", "false", em);
            Helpers.setSingleParam("enableWsApiAuth", "true", em);
            Helpers.setSingleParam("disableWsApiSimple", "false", em);
            Helpers.setSingleParam("disableWsApiClient", "false", em);
            Helpers.setSingleParam("disableWsApiAdmin", "false", em);
            Helpers.setSingleParam("enableInternalPki", "true", em);

            em.getTransaction().begin();
            em.createQuery("UPDATE Node n set n.loapApiSimple = true, n.loadApiClient = true, n.loadApiAdmin = true").executeUpdate();
            em.getTransaction().commit();
            em.close();
        }
        else if ("disable".equals(option))
        {
            EntityManager em = Helpers.getNewEm();
            em.getTransaction().begin();
            em.createQuery("UPDATE Node n set n.loadApiClient = false, n.loadApiAdmin = false").executeUpdate();
            em.getTransaction().commit();
            em.close();
        }
        if ("ssl".equals(option))
        {
            EntityManager em = Helpers.getNewEm();
            Helpers.setSingleParam("enableWsApiSsl", "true", em);
            Helpers.setSingleParam("enableWsApiAuth", "true", em);
            em.close();
        }
        if ("nossl".equals(option))
        {
            EntityManager em = Helpers.getNewEm();
            Helpers.setSingleParam("enableWsApiSsl", "false", em);
            em.close();
        }
        if ("internalpki".equals(option))
        {
            EntityManager em = Helpers.getNewEm();
            Helpers.setSingleParam("enableInternalPki", "true", em);
            em.close();
        }
        if ("externalpki".equals(option))
        {
            EntityManager em = Helpers.getNewEm();
            Helpers.setSingleParam("enableInternalPki", "false", em);
            em.close();
        }
    }

    private static void single(String option)
    {
        int id = Integer.parseInt(option);
        JobInstance res = JqmSingleRunner.run(id);
        jqmlogger.info(res.getState());
    }
}
