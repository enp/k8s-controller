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
			log.info("request from ${httpExchange.remoteAddress}")
			httpExchange.responseHeaders.add("Content-type", "text/plain")
			httpExchange.sendResponseHeaders(200, 0)
			httpExchange.responseBody.withWriter { out ->
				out << "HELLO\n"
			}
		}
		httpServer.start()
	}
	void stop() {
		httpServer.stop(0)
		log.info('stop')
	}
}
