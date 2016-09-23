import org.apache.activemq.ActiveMQSslConnectionFactory
import org.apache.activemq.camel.component.ActiveMQComponent
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry

def static runCamel() {
    def brokerUrl = 'failover:(ssl://your-full-route-name:443)?randomize=false'
    def queueUri = 'jms:queue:my.test.queue'
    def jms = new ActiveMQComponent(connectionFactory: new ActiveMQSslConnectionFactory(brokerURL: brokerUrl,
                                        userName: "admin", password: "admin",
                        trustStore: "C:\\Path\\To\\Your\\TrustStore\\trustStore.ts",
    trustStorePassword: "password"), useMessageIDAsCorrelationID: true)
    def camelCtx = new DefaultCamelContext(new SimpleRegistry(['jms':jms]))
    camelCtx.addRoutes(new RouteBuilder() {
        def void configure() {
            from('timer://helloTimer?period=5000').setBody(constant("message body!")).to(queueUri)
        }
    })
    camelCtx.start()
    // Stop Camel when the JVM is shut down
    Runtime.runtime.addShutdownHook({ ->
        camelCtx.stop()
    })
    synchronized(this){ this.wait() }
}

println 'about to run camel....!'
runCamel()