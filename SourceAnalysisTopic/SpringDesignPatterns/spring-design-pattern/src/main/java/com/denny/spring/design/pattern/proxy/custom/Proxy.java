package com.denny.spring.design.pattern.proxy.custom;

import com.denny.spring.design.pattern.proxy.Subject;
import javassist.*;
import javassist.bytecode.AccessFlag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Proxy {

    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h){
        Object object = null;


        try {
            ClassPool pool = ClassPool.getDefault();
            pool.importPackage("java.lang.reflect.Method;");
            pool.importPackage("com.denny.spring.design.pattern.proxy.custom.InvocationHandler");
            //1、创建动态代理类
            CtClass cc = pool.makeClass("$Proxy1");

            //2、设置字段
            CtClass invocationHandlerCC =  pool.get(InvocationHandler.class.getName());
            CtField ctField = new CtField(invocationHandlerCC, "h", cc);
            ctField.setModifiers(AccessFlag.PRIVATE);
            cc.addField(ctField);

            //3、设置构造函数
            //CtConstructor ct = CtNewConstructor.make("public $Proxy0(InvocationHandler h) { this.h = h;}", cc);
            CtConstructor ct = new CtConstructor(new CtClass[]{invocationHandlerCC}, cc);
            ct.setBody("{$0.h = $1;}");
            cc.addConstructor(ct);

            //4、设置接口
            //cc.setInterfaces(new CtClass[]{pool.makeInterface(interfaces[0].getName())});
            CtClass interfaceCc = pool.get(interfaces[0].getName());
            cc.addInterface(interfaceCc);

            //5、添加接口方法实现
            int i = 0;
            for(CtMethod ctMethod : interfaceCc.getDeclaredMethods()){
                String methodFiledName = "m" + i;
                String classParamsStr = "new Class[0]";

                CtClass[] parameterTypes = ctMethod.getParameterTypes();
                CtClass[] ctClasses = new CtClass[parameterTypes.length];
                if(ctClasses.length > 0){
                    for(int num = 0; num < parameterTypes.length; num++){
                        classParamsStr = classParamsStr.equals("new Class[0]")? parameterTypes[num].getName() + ".class" : parameterTypes+","+parameterTypes[num].getName()+".class";
                    }
                }
                System.out.println(classParamsStr);

                String methodFiledStr = "private static java.lang.reflect.Method " + methodFiledName + " = "+"Class.forName(\""+interfaceCc.getName()+"\").getMethod(\""+ctMethod.getName()+"\", "+classParamsStr+");";
                CtField ctMethodField = CtField.make(methodFiledStr, cc);
                cc.addField(ctMethodField);
                String methodBody="$0.h.invoke($0, "+methodFiledName+", $args);";
                if(CtPrimitiveType.voidType != ctMethod.getReturnType()){
                    if(ctMethod.getReturnType() instanceof CtPrimitiveType){
                        CtPrimitiveType ctPrimitiveType = (CtPrimitiveType) ctMethod.getReturnType();
                        methodBody = "return ("+ctPrimitiveType.getWrapperName()+")"+methodBody;
                    }else{
                        methodBody = "return "+methodBody;
                    }
                }
                CtMethod implementMethod = new CtMethod(ctMethod.getReturnType(), ctMethod.getName(), ctMethod.getParameterTypes(), cc);
                implementMethod.setBody(methodBody);
                cc.addMethod(implementMethod);
                i++;
            }
            String userDir = System.getProperty("user.dir");
            cc.writeFile(userDir);
            return cc.toClass().getConstructor(InvocationHandler.class).newInstance(h);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return object;

    }

    private static String generateSrc(Class<?>[] interfaces) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package com.denny.spring.design.pattern.proxy.custom;"+"\r\n");
        stringBuilder.append("import java.lang.reflect.Method;"+"\r\n");
        stringBuilder.append("public final class $Proxy0 extends Proxy implements "+interfaces[0].getName()+" {\r\n");
        stringBuilder.append("InvocationHandler h;\r\n");
        stringBuilder.append("public $Proxy0(java.lang.reflect.InvocationHandler h)  {"+"\r\n");
        stringBuilder.append("this.h = h;"+"\r\n");
        stringBuilder.append("}");

        for(Method method : interfaces[0].getDeclaredMethods()){
            stringBuilder.append("public "+method.getReturnType().getName()+" "+ method.getName()+" {"+"\r\n");
            stringBuilder.append("try{"+"\r\n");
            stringBuilder.append("Method m = "+interfaces[0].getName()+".class.getMethod(\""+method.getName()+"\");"+"\r\n");
            stringBuilder.append("this.h.invoke(this, m, (Object[])null);"+"\r\n");
            stringBuilder.append("} catch (Throwable e) {"+"\r\n");

            stringBuilder.append("}");
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        Proxy.newProxyInstance(Proxy.class.getClassLoader(), new Class[]{Subject.class}, null);
    }
}
