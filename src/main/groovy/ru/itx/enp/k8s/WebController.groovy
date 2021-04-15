package ru.itx.enp.k8s

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j

@Slf4j
@TupleConstructor()
class WebController {
	JobController jobController
	private String httpPort = System.getenv('HTTP_PORT') ?: '8080'
	private HttpServer httpServer = HttpServer.create(new InetSocketAddress(httpPort.toInteger()), 0)
	void start() {
		log.info('start')
		httpServer.createContext("/") { HttpExchange httpExchange ->
			String method = httpExchange.requestMethod
			String message = httpExchange.requestBody.text
			String response = ''
			log.info("request from ${httpExchange.remoteAddress} : ${method} : ${message}")
			switch (method) {
				case 'POST':
					response = jobController.run(message)
					break
				case 'GET':
					response = jobController.list()
					break
			}
			httpExchange.responseHeaders.add("Content-type", "text/plain")
			httpExchange.sendResponseHeaders(200, response.length())
			httpExchange.responseBody.withWriter { writer ->
				writer << response
			}
		}
		httpServer.start()
	}
	void stop() {
		httpServer.stop(0)
		log.info('stop')
	}
}
