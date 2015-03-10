import org.modelcatalogue.core.security.ajax.AjaxAwareLoginUrlAuthenticationEntryPoint

import java.util.concurrent.Executors
import  grails.plugin.executor.PersistenceContextExecutorWrapper

// Place your Spring DSL code here
beans = {
    authenticationEntryPoint(AjaxAwareLoginUrlAuthenticationEntryPoint) {
        loginFormUrl = '/login/auth' // has to be specified even though it's ignored
        portMapper = ref('portMapper')
        portResolver = ref('portResolver')
    }

    // TODO: re-enable concurrency as soon as the actions are safe to be run from multiple threads
    executorService(PersistenceContextExecutorWrapper ) { bean->
        bean.destroyMethod = 'destroy'
        persistenceInterceptor = ref("persistenceInterceptor")
        // only want to run things async, don't really need any concurrency
        executor = Executors.newSingleThreadExecutor()
    }
}
