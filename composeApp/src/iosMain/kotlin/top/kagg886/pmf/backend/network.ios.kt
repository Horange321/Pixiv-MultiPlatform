package top.kagg886.pmf.backend

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

/**
 * Raw值	CFNetwork/CFProxySupport.h	CFNetwork/CFHTTPStream.h CFNetwork/CFSocketStream.h
 * "HTTPEnable"	kCFNetworkProxiesHTTPEnable	N/A
 * "HTTPProxy"	kCFNetworkProxiesHTTPProxy	kCFStreamPropertyHTTPProxyHost
 * "HTTPPort"	kCFNetworkProxiesHTTPPort	kCFStreamPropertyHTTPProxyPort
 * "HTTPSEnable"	kCFNetworkProxiesHTTPSEnable	N/A
 * "HTTPSProxy"	kCFNetworkProxiesHTTPSProxy	kCFStreamPropertyHTTPSProxyHost
 * "HTTPSPort"	kCFNetworkProxiesHTTPSPort	kCFStreamPropertyHTTPSProxyPort
 * "SOCKSEnable"	kCFNetworkProxiesSOCKSEnable	N/A
 * "SOCKSProxy"	kCFNetworkProxiesSOCKSProxy	kCFStreamPropertySOCKSProxyHost
 * "SOCKSPort"	kCFNetworkProxiesSOCKSPort	kCFStreamPropertySOCKSProxyPort
 */
private val InternalType: HttpClientConfig<DarwinClientEngineConfig>.() -> Unit = {
    engine {
        when (val config = AppConfig.bypassSettings) {
            AppConfig.BypassSetting.None -> Unit
            is AppConfig.BypassSetting.Proxy -> {
                configureSession {
                    connectionProxyDictionary = buildMap {
                        if (config.type == AppConfig.BypassSetting.Proxy.ProxyType.HTTP) {
                            put("HTTPEnable", true)
                            put("HTTPProxy", config.host)
                            put("HTTPPort", config.port)


                            put("HTTPSEnable", true)
                            put("HTTPSProxy", config.host)
                            put("HTTPSPort", config.port)
                        }
                        if (config.type == AppConfig.BypassSetting.Proxy.ProxyType.SOCKS) {
                            put("SOCKSEnable", true)
                            put("SOCKSProxy", config.host)
                            put("SOCKSPort", config.port)
                        }
                    }
                }
            }

            is AppConfig.BypassSetting.SNIReplace -> {
                Logger.w("SNIReplace is not supported on iOS")
            }
        }

        Logger.i("BypassType: " + AppConfig.bypassSettings::class.simpleName)
    }
}

actual val PlatformEngine: HttpClientEngineFactory<*> = Darwin

actual val PlatformConfig: HttpClientConfig<*>.() -> Unit = InternalType as HttpClientConfig<*>.() -> Unit
