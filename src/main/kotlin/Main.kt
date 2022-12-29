import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

interface Service{
    fun add( num1 : Double , num2 : Double): Double
    fun sub (num1 : Double , num2 : Double): Double
}


data class ServiceImpl(val source : String) : Service{

    override fun add(num1: Double, num2: Double): Double {
        return num1 + num2
    }

    override fun sub(num1: Double, num2: Double) : Double {
        return num1 - num2
    }

     companion object{
         fun staticMethod(){
            println("hi i'm a static method")
         }
     }
}


class ServiceFactory< out T0>(private val targetClass : Class<T0>){

    private val _params : MutableList<Any> = mutableListOf()
    private lateinit var _interfaces : Array<Class<*>>

    private class MethodInterceptor<T>(private val target: T) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any {
            println("Method Name : ${method.name} ")
            print("Arguments:")
            args.forEach {
                print(it.toString())
                print(',')
            }
            println()


            val res = method.invoke(target, *args)
            println(res.toString())
            return res
        }

    }

    private fun createServiceProxy(target: T0, interfaces: Array<Class<*>>): T0 {
        return Proxy.newProxyInstance(
            target!!::class.java.classLoader,
            interfaces,
            MethodInterceptor(target)
        ) as T0

    }

    fun addParam(param : String): ServiceFactory<T0> {
        this._params.add(param)
        return this
    }

    fun setInterfaces(interfaces : Array<Class<*>>): ServiceFactory<T0> {
        _interfaces = interfaces
        return this

    }

    fun build(): T0 {
        return createServiceProxy(
            targetClass.getConstructor(
                *_params.map { it::class.java }.toTypedArray()
            ).newInstance(*_params.toTypedArray()) ,
            _interfaces)
    }

}




fun main(args: Array<String>) {
    val service : Service = ServiceFactory(ServiceImpl::class.java)
        .addParam("localhost")
        .setInterfaces(arrayOf(Service::class.java))
        .build()

    service.add(1.0,2.0)
    service.sub(10.0,2.0)

}