# :core:network 模块消费方 R8 混淆规则
# 保留 Retrofit 接口方法签名及参数注解，避免混淆导致动态代理反射失败
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
