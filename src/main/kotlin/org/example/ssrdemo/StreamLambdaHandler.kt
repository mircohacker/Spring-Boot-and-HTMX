package org.example.ssrdemo

import com.amazonaws.serverless.exceptions.ContainerInitializationException
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class StreamLambdaHandler : RequestStreamHandler {

    @Throws(IOException::class)
    override fun handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context) {
        handler!!.proxyStream(inputStream, outputStream, context)
    }

    companion object {
        private var handler: SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse>? = null

        init {
            try {
                handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(SseDemoApplication::class.java)
            } catch (e: ContainerInitializationException) {
                // if we fail here. We re-throw the exception to force another cold start
                e.printStackTrace()
                throw RuntimeException("Could not initialize Spring Boot application", e)
            }
        }
    }
}
