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

package com.enioka.jqm.jpamodel;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * <strong>Not part of any API - this an internal JQM class and may change without notice.</strong> <br>
 * JPA persistence class for storing the execution log. All finished {@link JobInstance}s end up in this table (and are purged from
 * {@link JobInstance}).
 */
@Entity
@Table(name = "History")
public class History implements Serializable
{
    private static final long serialVersionUID = -5249529794213078668L;

    @Id
    private Integer id;

    /************/
    /* Identity */

    @JoinColumn(name = "jobdef_id")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = com.enioka.jqm.jpamodel.JobDef.class)
    private JobDef jd;

    @Column(nullable = false, name = "applicationName", length = 100)
    private String applicationName;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = com.enioka.jqm.jpamodel.Queue.class)
    @JoinColumn(name = "queue_id")
    private Queue queue;

    @Column(nullable = false, length = 50, name = "name")
    private String queueName;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = com.enioka.jqm.jpamodel.Node.class)
    @JoinColumn(name = "node_id")
    private Node node;
    
    @Column(nullable = false, length = 50, name = "profileName")
    private String profileName;
    
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = com.enioka.jqm.jpamodel.Profile.class)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // null if cancelled before running
    @Column(nullable = true, length = 100, name = "nodeName")
    private String nodeName;

    @Column(name = "highlander")
    private boolean highlander = false;

    /***********/
    /* RESULTS */

    @Column(length = 20, name = "status")
    @Enumerated(EnumType.STRING)
    private State status = State.SUBMITTED;

    @Column(name = "return_code")
    private Integer returnedValue;

    @Column
    private Integer progress;

    /***********/
    /* TIME */

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "enqueue_date")
    private Calendar enqueueDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "attributionDate")
    private Calendar attributionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "execution_date")
    private Calendar executionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = true)
    private Calendar endDate;

    /***************************/
    /* Instance classification */

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "username")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "parent_job_id")
    private Integer parentId;

    @Column(length = 50, name = "instance_application")
    private String instanceApplication;

    @Column(length = 50, name = "instance_module")
    private String instanceModule;

    @Column(length = 50, name = "instance_keyword1")
    private String instanceKeyword1;

    @Column(length = 50, name = "instance_keyword2")
    private String instanceKeyword2;

    @Column(length = 50, name = "instance_keyword3")
    private String instanceKeyword3;

    /**************************/
    /* Job Def classification */
    @Column(length = 50, name = "keyword1")
    private String keyword1;

    @Column(length = 50, name = "keyword2")
    private String keyword2;

    @Column(length = 50, name = "keyword3")
    private String keyword3;

    @Column(length = 50, name = "application")
    private String application;

    @Column(length = 50, name = "module")
    private String module;

    /**
     * This is both the ID (PK) of the {@link History} (i.e. log) object and the ID of the {@link JobInstance} which was the source of this
     * log. Therefore it is NOT generated by the database and must be set.
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * See {@link #getId()}
     */
    public void setId(final Integer id)
    {
        this.id = id;
    }

    /**
     * @deprecated was never used.
     */
    public Integer getReturnedValue()
    {
        return returnedValue;
    }

    /**
     * @deprecated was never used.
     */
    public void setReturnedValue(final Integer returnedValue)
    {
        this.returnedValue = returnedValue;
    }

    /**
     * Time at which the execution request was committed inside the database.
     */
    public Calendar getEnqueueDate()
    {
        return enqueueDate;
    }

    /**
     * See {@link #getEnqueueDate()}
     */
    public void setEnqueueDate(final Calendar enqueueDate)
    {
        this.enqueueDate = enqueueDate;
    }

    /**
     * Time at which the execution request entered the RUNNING status - a few milliseconds before actual execution.
     */
    public Calendar getExecutionDate()
    {
        return executionDate;
    }

    /**
     * See {@link #getExecutionDate()}
     */
    public void setExecutionDate(final Calendar executionDate)
    {
        this.executionDate = executionDate;
    }

    /**
     * Time at which the payload (i.e. user code) returned.
     */
    public Calendar getEndDate()
    {
        return endDate;
    }

    /**
     * See {@link #getEndDate()}
     */
    public void setEndDate(final Calendar endDate)
    {
        this.endDate = endDate;
    }

    /**
     * The {@link Queue} on which the {@link JobInstance} run took place. the actual queue, not necessarily the one defined inside
     * {@link JobDef} as it can be overloaded in the execution request)
     */
    public Queue getQueue()
    {
        return queue;
    }

    /**
     * See {@link #getQueue()}
     */
    public void setQueue(final Queue queue)
    {
        this.queue = queue;
    }

    /**
     * An optional classification tag which can be specified inside the execution request (default is NULL).
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * See {@link #getUserName()}
     */
    public void setUserName(final String userName)
    {
        this.userName = userName;
    }

    /**
     * The actual {@link Node} (i.e. JQM engine) that has run the {@link JobInstance}.
     */
    public Node getNode()
    {
        return node;
    }

    /**
     * See {@link #getNode()}
     */
    public void setNode(final Node node)
    {
        this.node = node;
    }

    /**
     * An optional classification tag which can be specified inside the execution request (default is NULL).
     */
    public String getSessionId()
    {
        return sessionId;
    }

    /**
     * See {@link #getSessionId()}
     */
    public void setSessionId(final String sessionId)
    {
        this.sessionId = sessionId;
    }

    /**
     * The {@link JobDef} that was run (i.e. the {@link JobInstance} is actually an instance of this {@link JobDef}).
     */
    public JobDef getJd()
    {
        return jd;
    }

    /**
     * See {@link #getJd()}
     */
    public void setJd(JobDef jd)
    {
        this.jd = jd;
    }

    /**
     * Comes directly from the execution request. If specified, an e-mail is sent to this address at run end. Stored in {@link History} only
     * for the sake of being able to duplicate a launch.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * See {@link #getEmail()}
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Only set when a job request is created by a running job, in which case it contains the job ID.
     */
    public Integer getParentJobId()
    {
        return parentId;
    }

    /**
     * See {@link #getParentJobId()}
     */
    public void setParentJobId(Integer parentJobId)
    {
        this.parentId = parentJobId;
    }

    /**
     * The end status of the job (CRASHED, ENDED, ...)
     */
    public State getStatus()
    {
        return status;
    }

    /**
     * See {@link #getStatus()}
     */
    public State getState()
    {
        return status;
    }

    /**
     * See {@link #getStatus()}
     */
    public void setStatus(State status)
    {
        this.status = status;
    }

    /**
     * An optional classification tag which can be specified inside the {@link JobDef} (default is NULL).
     */
    public String getKeyword1()
    {
        return keyword1;
    }

    /**
     * See {@link #getKeyword1()}
     */
    public void setKeyword1(String keyword1)
    {
        this.keyword1 = keyword1;
    }

    /**
     * An optional classification tag which can be specified inside the {@link JobDef} (default is NULL).
     */
    public String getKeyword2()
    {
        return keyword2;
    }

    /**
     * See {@link #getKeyword2()}
     */
    public void setKeyword2(String keyword2)
    {
        this.keyword2 = keyword2;
    }

    /**
     * An optional classification tag which can be specified inside the {@link JobDef} (default is NULL).
     */
    public String getKeyword3()
    {
        return keyword3;
    }

    /**
     * See {@link #getKeyword3()}
     */
    public void setKeyword3(String keyword3)
    {
        this.keyword3 = keyword3;
    }

    /**
     * An optional classification tag which can be specified inside the {@link JobDef} (default is NULL).
     */
    public String getApplication()
    {
        return application;
    }

    /**
     * See {@link #getApplication()}
     */
    public void setApplication(String application)
    {
        this.application = application;
    }

    /**
     * An optional classification tag which can be specified inside the {@link JobDef} (default is NULL).
     */
    public String getModule()
    {
        return module;
    }

    /**
     * See {@link #getModule()}
     */
    public void setModule(String module)
    {
        this.module = module;
    }

    /**
     * User code may signal its progress through this integer. Purely optional.
     */
    public Integer getProgress()
    {
        return progress;
    }

    /**
     * See {@link #getProgress()}
     */
    public void setProgress(Integer progress)
    {
        this.progress = progress;
    }

    /**
     * True if the {@link JobInstance} was run in Highlander mode (i.e. never more than one concurrent execution of the same {@link JobDef}
     * inside the whole cluster)
     */
    public boolean isHighlander()
    {
        return highlander;
    }

    /**
     * See {@link #isHighlander()}
     */
    public void setHighlander(boolean highlander)
    {
        this.highlander = highlander;
    }

    /**
     * An optional classification tag which can be specified inside the execution request (default is NULL).
     */
    public String getInstanceApplication()
    {
        return instanceApplication;
    }

    /**
     * See {@link #getInstanceApplication()}
     */
    public void setInstanceApplication(String instanceApplication)
    {
        this.instanceApplication = instanceApplication;
    }

    /**
     * An optional classification tag which can be specified inside the execution request (default is NULL).
     */
    public String getInstanceModule()
    {
        return instanceModule;
    }

    /**
     * See {@link #setInstanceModule(String)}
     */
    public void setInstanceModule(String instanceModule)
    {
        this.instanceModule = instanceModule;
    }

    /**
     * An optional classification tag which can be specified inside the execution request (default is NULL).
     */
    public String getInstanceKeyword1()
    {
        return instanceKeyword1;
    }

    /**
     * See {@link #setInstanceKeyword1(String)}
     */
    public void setInstanceKeyword1(String instanceKeyword1)
    {
        this.instanceKeyword1 = instanceKeyword1;
    }

    /**
     * An optional classification tag which can be specified inside the execution request (default is NULL).
     */
    public String getInstanceKeyword2()
    {
        return instanceKeyword2;
    }

    /**
     * See {@link #setInstanceKeyword2(String)}
     */
    public void setInstanceKeyword2(String instanceKeyword2)
    {
        this.instanceKeyword2 = instanceKeyword2;
    }

    /**
     * An optional classification tag which can be specified inside the execution request (default is NULL).
     */
    public String getInstanceKeyword3()
    {
        return instanceKeyword3;
    }

    /**
     * See {@link #setInstanceKeyword3(String)}
     */
    public void setInstanceKeyword3(String instanceKeyword3)
    {
        this.instanceKeyword3 = instanceKeyword3;
    }

    /**
     * Time at which the job execution request (the {@link JobInstance}) was taken by an engine.
     */
    public Calendar getAttributionDate()
    {
        return attributionDate;
    }

    /**
     * See {@link #getAttributionDate()}
     */
    public void setAttributionDate(Calendar attributionDate)
    {
        this.attributionDate = attributionDate;
    }

    /**
     * The applicative key of the {@link JobDef} that has run. {@link JobDef} are always retrieved through this name.<br>
     * Max length is 100.
     */
    public String getApplicationName()
    {
        return this.applicationName;
    }

    /**
     * See {@link #getApplicationName()}
     */
    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    /**
     * Functional key. Queues are specified by name inside all APIs. Must be unique.<br>
     * Max length is 50.
     */
    public String getQueueName()
    {
        return queueName;
    }

    /**
     * See {@link #getQueueName()}
     */
    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    /**
     * The functional key of the node. It is unique.<br>
     * Max length is 100.
     */
    public String getNodeName()
    {
        return nodeName;
    }

    /**
     * See {@link #getName()}
     */
    public void setNodeName(final String nodeName)
    {
        this.nodeName = nodeName;
    }

    /**
     * The name of the profile (i.e. the sub-environment) this launch belongs to.
     */
    public String getProfileName()
    {
        return profileName;
    }

    /**
     * See {@link #getProfileName()}
     */
    public void setProfileName(String profileName)
    {
        this.profileName = profileName;
    }

    /**
     * The profile (i.e. the sub-environment) this launch belongs to.
     */
    public Profile getProfile()
    {
        return profile;
    }

    /**
     * See {@link #getProfile()}
     */
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }
}
