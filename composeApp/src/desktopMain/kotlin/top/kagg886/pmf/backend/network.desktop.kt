package top.kagg886.pmf.backend

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import top.kagg886.pmf.util.logger
import java.net.InetSocketAddress
import java.net.Proxy

private val InternalType: HttpClientConfig<OkHttpConfig>.() -> Unit = {
    engine {
        config {
            followRedirects(true)
            when (val config = AppConfig.bypassSettings) {
                AppConfig.BypassSetting.None -> Unit

                is AppConfig.BypassSetting.SNIReplace -> {
                    bypassSNIOnDesktop(
                        queryUrl =  config.url,
                        unsafeSSL = config.nonStrictSSL,
                        fallback = config.fallback,
                        dohTimeout = config.dohTimeout
                    )
                }

                is AppConfig.BypassSetting.Proxy -> {
                    this.proxy(
                        Proxy(Proxy.Type.valueOf(config.type.toString()),InetSocketAddress.createUnresolved(config.host,config.port))
                    )
                }
            }
            logger.i("BypassType: " + AppConfig.bypassSettings::class.simpleName)
        }
    }
}

actual val PlatformEngine: HttpClientEngineFactory<*> = OkHttp

actual val PlatformConfig0: HttpClientConfig<*>.() -> Unit = InternalType as HttpClientConfig<*>.() -> Unit
