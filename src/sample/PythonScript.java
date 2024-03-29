package sample;

import org.python.core.PyInstance;
import org.python.util.PythonInterpreter;


public class PythonScript
{

    PythonInterpreter interpreter = null;


    public PythonScript()
    {
        PythonInterpreter.initialize(System.getProperties(),
                System.getProperties(), new String[0]);

        this.interpreter = new PythonInterpreter();
    }

    void execfile(final String fileName)
    {
        this.interpreter.execfile(fileName);
    }

    PyInstance createClass( final String className, final String opts )
    {
        return (PyInstance) this.interpreter.eval(className + "(" + opts + ")");
    }
}
