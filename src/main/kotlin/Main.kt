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
}

class MethodInterceptor<T>(private val target: T) : InvocationHandler {

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

inline fun <reified T> createMethodProxy(target : T, itr : Class<T> ) : T{
    val proxy = Proxy.newProxyInstance(
        itr.classLoader,
        arrayOf(itr,),
        MethodInterceptor(target)
    )
    return proxy as T
}



fun main(args: Array<String>) {
    var service : Service = ServiceImpl("localhost")
    service = createMethodProxy(service , Service::class.java)

    service.add(1.0,2.0)
    service.sub(10.0,2.0)

}