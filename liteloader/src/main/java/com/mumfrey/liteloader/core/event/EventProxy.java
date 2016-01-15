package com.mumfrey.liteloader.core.event;

import java.util.concurrent.Callable;

import javax.management.RuntimeErrorException;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;

import org.objectweb.asm.Type;

import com.mumfrey.liteloader.transformers.ByteCodeUtilities;
import com.mumfrey.liteloader.transformers.event.EventInfo;

/**
 * EventProxy is a special class used by the EventInjectionTransformer, it is a
 * stub class into which all of the injected event callback methods are
 * injected. Each event handler method contains a try/catch block which invokes
 * one of the error reporting methods contained below when an error occurs, in
 * order to provide more meaningful information to the user and to mod makers.
 *  
 * @author Adam Mummery-Smith
 */
public final class EventProxy
{
    static String error;
    static StringBuilder errorDetails;

    static
    {
        new Exception().printStackTrace();
        if (true) throw new InstantiationError("EventProxy was loaded before transformation, this is bad!");
    }

    /**
     * Private because we never instance this class! 
     */
    private EventProxy() {}

    //  The event injection subsystem creates event stubs in this class which (if written in java) would
    //  look something like the following:
    // 
    //  public static void $event00000(EventInfo<?> e)
    //  {
    //      try
    //      {
    //          // Handlers sorted by priority
    //          com.example.mod.EventHandler.onWhateverEvent(e);
    //          com.example.anothermod.FooClass.onBarEvent(e);
    //      }
    //      catch (NoClassDefFoundError err)
    //      {
    //          onMissingClass(err, e);
    //      }
    //      catch (NoSuchMethodError err)
    //      {
    //          onMissingHandler(err, e);
    //      }
    //  }

    protected static void onMissingClass(Error err, EventInfo<?> e)
    {
        EventProxy.error = "Missing Event Handler Class!";
        EventProxy.errorDetails = new StringBuilder(); 

        EventProxy.addCrashDetailLine("\n");
        EventProxy.addCrashDetailLine("You are seeing this message because an event callback was injected by the Event");
        EventProxy.addCrashDetailLine("Injection Subsystem but the specified callback class was not defined! The");
        EventProxy.addCrashDetailLine("details of the missing callback are as follows:");
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("      Event Name: " + e.getName());
        EventProxy.addCrashDetailLine("     Cancellable: " + e.isCancellable());
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("  Callback class: " + err.getMessage().replace('/', '.'));
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("If you are the mod author then in order to fix the error you must provide an");
        EventProxy.addCrashDetailLine("implementation for the specified class, or check that the class name and package");
        EventProxy.addCrashDetailLine("are correct.");
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("This is an unrecoverable error, please report it to the mod author and remove");
        EventProxy.addCrashDetailLine("the offending mod.");
        EventProxy.addStackTrace(err);

        throw new RuntimeErrorException(err, "Missing event handler class for event " + e.getName() + ", see crash report for details");
    }

    protected static void onMissingHandler(Error err, EventInfo<?> e)
    {
        String descriptor = err.getMessage();
        int dotPos = descriptor.lastIndexOf('.');
        int bracketPos = descriptor.indexOf('(');

        String signature = descriptor.substring(bracketPos);
        String sourceClass = e.getSource() != null ? e.getSource().getClass().getSimpleName() : "?";

        EventProxy.error = "Missing Event Handler Method!";
        EventProxy.errorDetails = new StringBuilder(); 

        EventProxy.addCrashDetailLine("\n");
        EventProxy.addCrashDetailLine("You are seeing this message because an event callback was injected by the Event");
        EventProxy.addCrashDetailLine("Injection Subsystem but the specified callback method was not defined. The");
        EventProxy.addCrashDetailLine("details of the missing callback are as follows:");
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("      Event Name: " + e.getName());
        EventProxy.addCrashDetailLine("     Cancellable: " + e.isCancellable());
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("  Callback class: " + descriptor.substring(0, dotPos));
        EventProxy.addCrashDetailLine(" Callback method: " + descriptor.substring(dotPos + 1, bracketPos));
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("If you are the mod author then in order to fix the error you must add a suitable");
        EventProxy.addCrashDetailLine("callback method in the above class. The method signature should be as follows:");
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine(EventProxy.generateHandlerTemplate(descriptor.substring(dotPos + 1, bracketPos), signature, sourceClass));
        EventProxy.addDetailLineBreak();
        EventProxy.addCrashDetailLine("This is an unrecoverable error, please report it to the mod author and remove");
        EventProxy.addCrashDetailLine("the offending mod.");
        EventProxy.addStackTrace(err);

        throw new RuntimeErrorException(err, "Missing event handler method for event " + e.getName() + ", see crash report for details");
    }

    private static void addStackTrace(Error err)
    {
        EventProxy.addDetailLineBreak();
        EventProxy.errorDetails.append("Stacktrace:").append('\n');
        EventProxy.addDetailLineBreak();

        StackTraceElement[] stackTrace = err.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++)
        {
            EventProxy.addCrashDetailLine(String.format(" %3d) %s", i + 1, stackTrace[i]));
        }
    }

    protected static String generateHandlerTemplate(String methodName, String signature, String sourceClass)
    {
        Type[] argTypes = Type.getArgumentTypes(signature);

        StringBuilder tpl = new StringBuilder();
        tpl.append("    public static void ").append(methodName).append('(');
        for (int var = 0; var < argTypes.length; var++)
        {
            if (EventProxy.appendTypeName(tpl, argTypes[var], sourceClass)) tpl.append("[]");
            if (var == 0) tpl.append(" e");
            if (var > 0) tpl.append(" arg").append(String.valueOf(var));
            if (var < argTypes.length - 1) tpl.append(", ");
        }
        tpl.append(")\n\t    {\n\t        // handler code here\n\t    }");

        String template = tpl.toString();
        if (template.contains(", ReturnType>"))
        {
            template = template.replace("static void", "static <ReturnType> void");
        }
        return template;
    }

    private static boolean appendTypeName(StringBuilder tpl, Type type, String sourceClass)
    {
        switch (type.getSort())
        {
            case Type.ARRAY:
                EventProxy.appendTypeName(tpl, type.getElementType(), sourceClass);
                return true;
            case Type.OBJECT:
                String typeName = type.getClassName();
                typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
                tpl.append(typeName);
                if (typeName.endsWith("ReturnEventInfo"))
                {
                    tpl.append('<').append(sourceClass).append(", ReturnType>");
                }
                else if (typeName.endsWith("EventInfo"))
                {
                    tpl.append('<').append(sourceClass).append('>');
                }
                return false;
            default:
                tpl.append(ByteCodeUtilities.getTypeName(type));
                return false;
        }
    }

    private static void addDetailLineBreak()
    {
        System.err.println();
        EventProxy.errorDetails.append('\n');
    }

    private static void addCrashDetailLine(String string)
    {
        System.err.println(string);
        EventProxy.errorDetails.append('\t').append(string).append('\n');
    }

    public static void populateCrashReport(CrashReport crashReport)
    {
        if (EventProxy.error != null)
        {
            CrashReportCategory category = crashReport.makeCategoryDepth("Event Handler Error", 1);

            category.addCrashSectionCallable(EventProxy.error, new Callable<String>()
            {
                @Override
                public String call() throws Exception
                {
                    return EventProxy.errorDetails.toString();
                }
            });
        }
    }
}