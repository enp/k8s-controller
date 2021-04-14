package ru.itx.enp.k8s

JobController jobController = new JobController()
WebController webController = new WebController(jobController)

addShutdownHook {
	webController.stop()
	jobController.stop()
}

jobController.start()
webController.start()
