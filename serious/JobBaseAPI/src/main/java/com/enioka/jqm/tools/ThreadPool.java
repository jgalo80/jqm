package com.enioka.jqm.tools;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.enioka.jqm.jpamodel.Queue;

class ThreadPool
{
	private static Logger jqmlogger = Logger.getLogger(ThreadPool.class);
	private Queue queue = null;
	private int nbThread = 0;
	private ExecutorService pool = null;
	private Map<String, URL[]> cache = null;

	ThreadPool(Queue queue, int n, Map<String, URL[]> cache)
	{
		this.queue = queue;
		this.cache = cache;
		nbThread = n;
		pool = Executors.newFixedThreadPool(nbThread);
	}

	void run(com.enioka.jqm.jpamodel.JobInstance ji, Polling p)
	{
		jqmlogger.info("Job instance will be inserted inside a thread pool: " + ji.getId());
		pool.submit(new Loader(ji, cache, p));
	}

	Queue getQueue()
	{
		return queue;
	}

	int getNbThread()
	{
		return nbThread;
	}
}
