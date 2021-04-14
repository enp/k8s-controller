package ru.itx.enp.k8s

import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j

@Slf4j
@TupleConstructor()
class WebController {
	JobController jobController
	void start() {
		log.info('start')
	}
	void stop() {
		log.info('stop')
	}
}
