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

    executorService(PersistenceContextExecutorWrapper ) { bean->
        bean.destroyMethod = 'destroy'
        persistenceInterceptor = ref("persistenceInterceptor")
        executor = Executors.newCachedThreadPool()
    }
}
