package ru.itx.enp.k8s

import groovy.util.logging.Slf4j
import io.fabric8.kubernetes.api.model.batch.Job
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.Watch
import io.fabric8.kubernetes.client.Watcher
import io.fabric8.kubernetes.client.WatcherException
import io.fabric8.kubernetes.client.Watcher.Action

@Slf4j
class JobController {
	private KubernetesClient k8s
	private Watch watch
	void start() {
		log.info('start')
		k8s = new DefaultKubernetesClient()
		watch = k8s.batch().jobs().inAnyNamespace().watch(new Watcher<Job>() {
			public void eventReceived(Action action, Job job) {
				if (action == Action.MODIFIED && job.status.conditions.type) {
					String jobName = job.metadata.labels['job-name']
					String jobType = job.status.conditions.type[0]
					log.info("JOB : {} : {}", jobName, jobType)
					k8s.resource(job).delete()
				}
			}
			public void onClose(WatcherException e) {
				log.info("JOB CLOSE ERROR : {}", e.message)
			}
		})
		Job job = k8s.batch().jobs().load(getClass().getResourceAsStream('/job-deployment.yaml')).get()
		k8s.batch().jobs().create(job)
	}
	void stop() {
		watch.close()
		k8s.close()
		log.info('stop')
	}
}
