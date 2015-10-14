package org.lanternpowered.server.event;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import org.spongepowered.api.event.Event;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClassEventHandlerFactory implements AnnotatedEventHandlerFactory {

    private final AtomicInteger id = new AtomicInteger();
    private final LocalClassLoader classLoader = new LocalClassLoader(this.getClass().getClassLoader());
    private final LoadingCache<Method, Class<? extends AnnotatedEventHandler>> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .weakValues()
            .build(new CacheLoader<Method, Class<? extends AnnotatedEventHandler>>() {

                @Override
                public Class<? extends AnnotatedEventHandler> load(Method method) throws Exception {
                    return createClass(method);
                }

            });

    private final String targetPackage;

    public ClassEventHandlerFactory(String targetPackage) {
        checkNotNull(targetPackage, "targetPackage");
        checkArgument(!targetPackage.isEmpty(), "targetPackage cannot be empty");

        this.targetPackage = targetPackage + '.';
    }

    @Override
    public AnnotatedEventHandler create(Object handle, Method method) throws Exception {
        return this.cache.get(method).getConstructor(method.getDeclaringClass()).newInstance(handle);
    }

    private Class<? extends AnnotatedEventHandler> createClass(Method method) {
        Class<?> handle = method.getDeclaringClass();
        Class<?> eventClass = method.getParameterTypes()[0];
        String name = this.targetPackage
                + eventClass.getSimpleName() + "Handler_" +  handle.getSimpleName() + '_' + method.getName()
                + this.id.incrementAndGet();
        return this.classLoader.defineClass(name, generateClass(name, handle, method, eventClass));
    }

    private static final String BASE_HANDLER = Type.getInternalName(AnnotatedEventHandler.class);
    private static final String HANDLE_METHOD_DESCRIPTOR = '(' + Type.getDescriptor(Event.class) + ")V";

    private static byte[] generateClass(String name, Class<?> handle, Method method, Class<?> eventClass) {
        name = name.replace('.', '/');

        final String handleName = Type.getInternalName(handle);
        final String handleDescriptor = Type.getDescriptor(handle);
        final String eventName = Type.getInternalName(eventClass);

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, name, null, BASE_HANDLER, null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", '(' + handleDescriptor + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, BASE_HANDLER, "<init>", "(Ljava/lang/Object;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "handle", HANDLE_METHOD_DESCRIPTOR, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, name, "handle", "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, handleName);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventName);
            mv.visitMethodInsn(INVOKEVIRTUAL, handleName, method.getName(), "(L" + eventName + ";)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static class LocalClassLoader extends ClassLoader {

        private LocalClassLoader(ClassLoader parent) {
            super(parent);
        }

        @SuppressWarnings("unchecked")
        private <T> Class<T> defineClass(String name, byte[] b) {
            return (Class<T>) this.defineClass(name, b, 0, b.length);
        }
    }

}
