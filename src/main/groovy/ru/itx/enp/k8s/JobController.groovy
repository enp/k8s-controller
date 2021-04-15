package ru.itx.enp.k8s

import com.fasterxml.jackson.databind.ObjectMapper

import groovy.util.logging.Slf4j
import io.fabric8.kubernetes.api.model.batch.Job
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.Watch
import io.fabric8.kubernetes.client.Watcher
import io.fabric8.kubernetes.client.WatcherException

@Slf4j
class JobController {
	private KubernetesClient k8s
	private String namespace = 'default'
	private List results = []
	private Watch watch
	void start() {
		log.info('start')
		k8s = new DefaultKubernetesClient()
		watch = k8s.batch().jobs().inAnyNamespace().watch(new Watcher<Job>() {
			public void eventReceived(Action action, Job job) {
				String jobName = job.metadata.name
				log.info("JOB {} {}", jobName, action)
				if (action == Action.MODIFIED && job.status.conditions.type) {
					String jobType = job.status.conditions.type[0]
					String jobLog = k8s.batch().jobs().inNamespace(namespace).withName(jobName).log
					results.add([name: jobName, type: jobType, log: jobLog])
					log.info("DELETE JOB : {}({}) : {}", jobName, jobType, jobLog)
					k8s.resource(job).delete()
				}
			}
			public void onClose(WatcherException e) {
				log.info("JOB CLOSE ERROR : {}", e.message)
			}
		})
	}
	String run(String message) {
		Job job = k8s.batch().jobs().load(getClass().getResourceAsStream('/job-deployment.yaml')).get()
		job.spec.template.spec.containers[0].command[1] = message
		k8s.batch().jobs().inNamespace(namespace).create(job).metadata.name
	}
	String list() {
		new ObjectMapper().writeValueAsString(results)
	}
	void stop() {
		watch.close()
		k8s.close()
		log.info('stop')
	}
}
